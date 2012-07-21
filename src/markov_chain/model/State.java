package markov_chain.model;

import java.util.HashMap;
import java.util.Map.Entry;

class State<T> {

	T value;
	
	public int transitionCount = 0;
	
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
}






