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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import markov_chain.model.Generator;
import markov_chain.model.Trainer;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class markov {
	
	static boolean verbose = false;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		hack: eclipse don't support IO redirection worth a shit
//		try {
//			System.setIn(new FileInputStream("./testfile"));
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

		boolean graphMode = false;
		boolean endNode = true;
		
		int count = -1;

		long n = 0;
		long sumOfSqr = 0;
		long sum = 0;

		for (String s : args) {
			
			if ( !s.matches("^-[vegj]*(c[0-9]*)?$")){
				System.out.println("invalid argument");
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
			if (s.matches("^-.*e.*")) {
				endNode = true;
				log("include end node");
			}
			if (s.matches("^-.*c[0-9]*$")) {
				log("counted output mode");
				count = Integer.parseInt(s.replaceAll("^-.*c", ""));
			}

		}

		Trainer<Character> trainer = new Trainer<Character>();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		try {
			String s = br.readLine();
			while (s != null) {
				trainer.train(string2List(s));
				n++;
				sumOfSqr += s.length() * s.length();
				sum += s.length();
				s = br.readLine();
			}
			if ( n == 0 ){
				System.err.println("Invalid corpus: At least one sample is required, two to make it interesting");
				return;
			}
			if (graphMode) {
				System.out.println(trainer.getTransitionDiagram().toString());
				return;
			}
			Generator<Character> generator = new Generator<Character>(trainer.getTransitionDiagram());
			if (endNode) {
				if (count >= 0) {
					for (int c = 0; c < count; c++) {
						output(generator);
					}
				} else {
					while (true) {
						output(generator);
					}
				}
			} else {
				double sd = ((double) sumOfSqr - (double) (sum * sum) / (double) n) / (double) (n - 1);
				double mean = (double) sum / (double) n;
				NormalDistributionImpl dist = new NormalDistributionImpl(mean, sd);
				
				log(String.format("mean: %.4f sd: %.4f", mean, sd));
				
				try {
					if (count >= 0) {
						for (int c = 0; c < count; c++) {
							output(generator, dist);
						}
					} else {
						while (true) {
							output(generator, dist);
						}
					}
				} catch (MathException e) {
					e.printStackTrace();
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void log(String string) {
		if ( verbose ){
			System.err.println(string);
		}
	}

	private static void output(Generator<Character> generator) {
		String item = list2String(generator.generate());
		System.out.println(item);
	}

	private static void output(Generator<Character> generator, NormalDistributionImpl dist) throws MathException {
		throw new NotImplementedException();
//		String item = list2String(generator.generate(dist));
//		System.out.println(item);
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
