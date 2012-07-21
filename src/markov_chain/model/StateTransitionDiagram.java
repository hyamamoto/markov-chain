package markov_chain.model;

import java.util.HashMap;
import java.util.Map.Entry;


public class StateTransitionDiagram<T> {

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
			std.states.put(entry.getKey(), entry.getValue().removeGuards(guard.value));
		}
		
		return std;
	}

}
