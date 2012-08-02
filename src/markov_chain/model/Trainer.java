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

import java.util.List;

public class Trainer<T> {

	private StateTransitionDiagram<T> std = new StateTransitionDiagram<T>();

	public StateTransitionDiagram<T> getTransitionDiagram() {
		return std;
	}

	public void train(List<T> sequence) {
		internalTrain(sequence, std.getGuard());
	}

	private void internalTrain(List<T> sequence, State<T> state) {
		if (sequence.size() > 0) {
			State<T> nextState = std.getState(sequence.get(0));
			state.addTransition(nextState.getValue());
			internalTrain(sequence.subList(1, sequence.size()), nextState);
		} else {
			state.addTransition(std.getGuard().getValue());
		}
	}

}
