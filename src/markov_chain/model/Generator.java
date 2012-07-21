package markov_chain.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

public class Generator<T> {
	
	private StateTransitionDiagram<T> std;

	public Generator(StateTransitionDiagram<T> std ) {
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
		for (Entry<State<T>, Integer> entry : state.transitions.entrySet()) {
			count -= entry.getValue();
			if (count <= 0) {
				nextState = entry.getKey();
				break;
			}
		}
		return nextState;
	}
	
}
