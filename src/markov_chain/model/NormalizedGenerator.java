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

package markov_chain.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class NormalizedGenerator<T> implements Generator<T> {

	private StateTransitionDiagram<T> std;
	private NormalDistributionImpl dist;

	public NormalizedGenerator(StateTransitionDiagram<T> std,
			NormalDistributionImpl dist) {
		this.std = std;
		this.dist = dist;
	}

	public List<T> generate() {
		try {
			return internalGenerate(chooseNextState(std.getGuard(), 0), 0);
		} catch (MathException e) {
			throw new RuntimeException(e);
		}
	}

	private List<T> internalGenerate(State<T> state, int position)
			throws MathException {
		List<T> result;
		if (state == std.getGuard()) {
			result = new LinkedList<T>();
		} else {
			result = internalGenerate(chooseNextState(state, position + 1),
					position + 1);
			result.add(0, state.getValue());
		}
		return result;
	}

	private State<T> chooseNextState(State<T> state, int position)
			throws MathException {
		Random rand = new Random();
		double probabilityOfCompleation = dist.cumulativeProbability(position);

		if (probabilityOfCompleation > rand.nextDouble()) {
			return std.getGuard();
		}

		int count = rand.nextInt(state.transitionCount + 1);
		State<T> nextState = null;
		for (Entry<T, Integer> entry : state.transitions.entrySet()) {
			count -= entry.getValue();
			if (count <= 0) {
				nextState = std.getState(entry.getKey());
				break;
			}
		}
		if (nextState == null) {
			// current state has no transitions so we force an end
			nextState = std.getGuard();
		}
		return nextState;
	}

}
