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

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gson.annotations.Expose;


public class StateTransitionDiagram<T> {

	@Expose
	private HashMap<T, State<T>> states = new HashMap<T, State<T>>();

	private State<T> guard = getState(null);

	public State<T> getState(T value) {
		State<T> result = states.get(value);
		if (result == null) {
			result = new State<T>(value);
			states.put(value, result);
		}
		return result;
	}

	public State<T> getGuard() {
		return guard;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("digraph G {\n");
		for (State<T> state : states.values()) {
			sb.append(state.toString());
		}
		sb.append("}");
		return sb.toString();
	}
	
	public StateTransitionDiagram<T> removeEndGuards(){
		StateTransitionDiagram<T> std = new StateTransitionDiagram<T>();
		
		for ( Entry<T, State<T>> entry : states.entrySet() ){
			std.states.put(entry.getKey(), entry.getValue().removeGuards(guard.getValue()));
		}
		
		return std;
	}

	

}
