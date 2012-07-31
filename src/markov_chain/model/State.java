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

public class State<T> {

	private T value;
	
	public int transitionCount = 0;
	
	@Expose
	public HashMap<T, Integer> transitions = new HashMap<T, Integer>();

	State(T value) {
		this.value = value;
	}

	public void addTransition(T state) {
		Integer i = transitions.get(state);
		if (i == null) {
			i = Integer.valueOf(0);
		}
		transitions.put(state, Integer.valueOf(i + 1));
		transitionCount++;
	}
	
	public void addTransition(T state, int count) {
		Integer i = Integer.valueOf(count);
		transitions.put(state, i);
		transitionCount += count;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		String orig;
		if( value == null ){
			orig = "START";
		} else {
			orig = value.toString();
		}
		for ( Entry<T, Integer> entry: transitions.entrySet() ) {
			T state = entry.getKey();
			String end;
			if (state != null) {
				end = state.toString();
			} else {
				end = "END";
			}
			sb.append(String.format("%s -> %s [label = \"%.4f\"];\n", orig, end, ((float)entry.getValue() / (float)transitionCount)  ));
		}
		return sb.toString();
	}

	public State<T> removeGuards(T guard) {
		State<T> state = new State<T>(value);
		
		for ( Entry<T, Integer> entry : transitions.entrySet() ){
			if ( entry.getKey() != guard ){
				state.transitions.put(entry.getKey(), entry.getValue());
				state.transitionCount += entry.getValue();
			}
		}
		
		return state;
	}
	
	public T getValue() {
		return value;
	}
}






