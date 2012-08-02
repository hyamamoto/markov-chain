/**
 * Copyright 2012 C. A. Fitzgerald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import markov_chain.gson.GsonStub;
import markov_chain.gson.GsonStub.Meta;
import markov_chain.gson.GsonStub.StateStub;
import markov_chain.model.EndTagGenerator;
import markov_chain.model.Generator;
import markov_chain.model.NormalizedGenerator;
import markov_chain.model.State;
import markov_chain.model.StateTransitionDiagram;
import markov_chain.model.Trainer;

import org.apache.commons.math.distribution.NormalDistributionImpl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class markov {

	private static final String HELP = "Usage: markov [OPTION]\n\n  -c[K]\n\t\tgenerate K lines of output\n  -e\n\t\tinclude an 'end' symbol when training sequences. if an 'end' is\n\t\tencountered during generation it will terminate the current chain\n\t\tand start a new one\n  -g\n\t\toutputs state transition diagram in Graphviz compliant notation\n  -h\n\t\tprints this message\n  -J\n\t\tstdin is to be parsed as json not as a training corpus\n  -j\n\t\toutputs state transition diagram as json\n  -v\n\t\toutputs debug information on stderr\n\nIf no -e, it uses the normal distribution of the trained input to determine chain length.\nIf no -j, -g or, -c, will continuously output chains until stopped.\nIf no -J, data on stdin is processed as a training corpus.\n";
	static boolean verbose = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// hack: eclipse don't support IO redirection worth a shit
		// try {
		// System.setIn(new FileInputStream("./json"));
		// } catch (FileNotFoundException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }

		boolean graphMode = false;
		boolean jsonMode = false;
		boolean jsonRecoverMode = false;
		boolean endNode = false;

		int count = -1;

		long n = 0;
		long sumOfSqr = 0;
		long sum = 0;

		for (String s : args) {

			if (!s.matches("^-[vegjJh]*(c[0-9]*)?$")) {
				System.out.println("invalid argument");
				return;
			}

			if (s.matches("^-.*h.*")) {
				System.out.println(HELP);
				return;
			}
			if (s.matches("^-.*v.*")) {
				verbose = true;
				log("verbose mode");
			}
			if (s.matches("^-.*g.*")) {
				graphMode = true;
				log("graph mode");
			}
			if (s.matches("^-.*j.*")) {
				jsonMode = true;
				log("json mode");
			}
			if (s.matches("^-.*J.*")) {
				jsonRecoverMode = true;
				log("json recover mode");
			}
			if (s.matches("^-.*e.*")) {
				endNode = true;
				log("include end node");
			}
			if (s.matches("^-.*c[0-9]*$")) {
				log("counted output mode");
				count = Integer.parseInt(s.replaceAll("^-.*c", ""));
			}
			
			boolean error = (graphMode == true && jsonMode == true);
			if ( !error ){
				error = ( count > -1 ) && (graphMode == true || jsonMode == true) ;
			}
			
			if ( error ){
				System.err.println("[error] switches j, g and, c are mutualy exclusive.");
				return;
			}

		}

		StateTransitionDiagram<Character> std;
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			if (!jsonRecoverMode) {
				Trainer<Character> trainer = new Trainer<Character>();
				String s = br.readLine();
				while (s != null) {
					trainer.train(string2List(s));
					n++;
					sumOfSqr += s.length() * s.length();
					sum += s.length();
					s = br.readLine();
				}
				if (n == 0) {
					System.err
							.println("Invalid corpus: At least one sample is required, two to make it interesting");
					return;
				}
				std = trainer.getTransitionDiagram();
			} else {
				std = new StateTransitionDiagram<Character>();
				GsonStub gstub = new Gson().fromJson(br, GsonStub.class);
				n = gstub.meta.n;
				sum = gstub.meta.sum;
				sumOfSqr = gstub.meta.sumOfSqr;

				for (Entry<String, StateStub> entry : gstub.states.entrySet()) {
					State<Character> state;
					if (entry.getKey().equals("null")) {
						state = std.getGuard();
					} else {
						state = std.getState(Character.valueOf(entry.getKey()
								.charAt(0)));
					}
					for (Entry<String, Integer> transitions : entry.getValue().transitions
							.entrySet()) {
						State<Character> tranny;
						if (transitions.getKey().equals("null")) {
							tranny = std.getGuard();
						} else {
							tranny = std.getState(Character.valueOf(transitions
									.getKey().charAt(0)));
						}

						state.addTransition(tranny.getValue(),
								transitions.getValue());
					}
				}
			}
			if (graphMode) {
				if (endNode) {
					System.out.println(std.toString());
				} else {
					System.out.println(std.removeEndGuards().toString());
				}
				return;
			}
			if (jsonMode) {
				Gson gson = new GsonBuilder()
						.excludeFieldsWithoutExposeAnnotation().create();

				String partialJson;
				if (endNode) {
					partialJson = gson.toJson(std);
				} else {
					partialJson = gson.toJson(std.removeEndGuards());
				}
				GsonStub gstub = new Gson().fromJson(partialJson,
						GsonStub.class);
				gstub.meta = new Meta();
				gstub.meta.n = n;
				gstub.meta.sum = sum;
				gstub.meta.sumOfSqr = sumOfSqr;

				System.out.println(gson.toJson(gstub));
				return;
			}

			Generator<Character> generator;
			if (endNode) {
				generator = new EndTagGenerator<Character>(std);
			} else {
				double sd = ((double) sumOfSqr - (double) (sum * sum)
						/ (double) n)
						/ (double) (n - 1);
				double mean = (double) sum / (double) n;
				log(String.format("mean: %.4f sd: %.4f", mean, sd));
				NormalDistributionImpl dist = new NormalDistributionImpl(mean,
						sd);
				generator = new NormalizedGenerator<Character>(
						std.removeEndGuards(), dist);
			}
			if (count >= 0) {
				for (int c = 0; c < count; c++) {
					output(generator);
				}
			} else {
				while (true) {
					output(generator);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void log(String string) {
		if (verbose) {
			System.err.println(string);
		}
	}

	private static void output(Generator<Character> generator) {
		String item = list2String(generator.generate());
		System.out.println(item);
	}

	private static List<Character> string2List(String s) {
		LinkedList<Character> list = new LinkedList<Character>();

		for (int c = 0; c < s.length(); c++) {
			list.add(s.charAt(c));
		}

		return list;
	}

	private static String list2String(List<Character> list) {
		StringBuffer sb = new StringBuffer();
		for (Character c : list) {
			sb.append(c);
		}
		return sb.toString();
	}

}
