package markov_chain.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.NormalDistributionImpl;

public class MarkovChain<T> {

	private HashMap<T, State<T>> states = new HashMap<T, State<T>>();

	private State<T> guard = getState(null);
	
	private boolean includeEndState = true;
	
	public MarkovChain() {
	}
	
	public MarkovChain(Boolean includeEndState){
		this.includeEndState = includeEndState;
	}

	private static class State<T> {

		private T value;
		public int transitionCount = 0;
		public HashMap<State<T>, Integer> transitions = new HashMap<State<T>, Integer>();

		private State(T value) {
			this.value = value;
		}

		public void addTransition(State<T> state) {
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
			for ( Entry<State<T>, Integer> entry: transitions.entrySet() ) {
				State<T> state = entry.getKey();
				String end;
				if (state.value != null) {
					end = state.value.toString();
				} else {
					end = "END";
				}
				sb.append(String.format("%s -> %s [label = \"%.4f\"];\n", orig, end, ((float)entry.getValue().intValue() / (float)transitionCount)  ));
			}
			return sb.toString();
		}
	}

	public State<T> getState(T value) {
		State<T> result = states.get(value);
		if (result == null) {
			result = new State<T>(value);
			states.put(value, result);
		}
		return result;
	}

	public void train(List<T> sequence) {
		internalTrain(sequence, guard);
	}

	private void internalTrain(List<T> sequence, State<T> state) {
		if (sequence.size() > 0) {
			State<T> nextState = getState(sequence.get(0));
			state.addTransition(nextState);
			internalTrain(sequence.subList(1, sequence.size()), nextState);
		} else {
			if ( includeEndState ){
				state.addTransition(guard);
			}
		}
	}

	public List<T> generate() {
		return internalGenerate(chooseNextState(guard));
	}

	private List<T> internalGenerate(State<T> state) {
		List<T> result;
		if (state == guard) {
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
		for (Entry<State<T>, Integer> entry : state.transitions.entrySet()) {
			count -= entry.getValue();
			if (count <= 0) {
				nextState = entry.getKey();
				break;
			}
		}
		return nextState;
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
	
	
	public List<T> generate( NormalDistributionImpl dist ) throws MathException {
		return internalGenerate(chooseNextState(guard), dist, 0);
	}

	private List<T> internalGenerate(State<T> state, NormalDistributionImpl dist, int position) throws MathException {
		List<T> result;
		if (state == guard) {
			result = new LinkedList<T>();
		} else {
			result = internalGenerate(chooseNextState(state, dist, position+1), dist, position+1);
			result.add(0, state.value);
		}
		return result;
	}

	private State<T> chooseNextState(State<T> state, NormalDistributionImpl dist, int position) throws MathException {
		Random rand = new Random();
		double probabilityOfCompleation =  dist.cumulativeProbability(position); 
		
		if ( probabilityOfCompleation > rand.nextDouble() ){
			return guard;
		} 
		
		int count = rand.nextInt(state.transitionCount + 1);
		State<T> nextState = null;
		for (Entry<State<T>, Integer> entry : state.transitions.entrySet()) {
			count -= entry.getValue();
			if (count <= 0) {
				nextState = entry.getKey();
				break;
			}
		}
		if ( nextState == null ){
			//current state has no transitions so we force an end
			nextState = guard;
		}
		return nextState;
	}

}
