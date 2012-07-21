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

public class EndTagGenerator<T> implements Generator<T>{
	
	private StateTransitionDiagram<T> std;

	public EndTagGenerator(StateTransitionDiagram<T> std ) {
		this.std = std;
	}
	
	public List<T> generate() {
		return internalGenerate(chooseNextState(std.getGuard()));
	}

	private List<T> internalGenerate(State<T> state) {
		List<T> result;
		if (state == std.getGuard()) {
			result = new LinkedList<T>();
		} else {
			result = internalGenerate(chooseNextState(state));
			result.add(0, state.value);
		}
		return result;
	}
	
	private State<T> chooseNextState(State<T> state) {
		Random rand = new Random();
		int count = rand.nextInt(state.transitionCount + 1);
		State<T> nextState = null;
		for (Entry<T, Integer> entry : state.transitions.entrySet()) {
			count -= entry.getValue();
			if (count <= 0) {
				nextState = std.getState(entry.getKey());
				break;
			}
		}
		return nextState;
	}
	
}
