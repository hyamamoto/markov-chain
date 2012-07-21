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
			state.addTransition(nextState);
			internalTrain(sequence.subList(1, sequence.size()), nextState);
		} else {
			state.addTransition(std.getGuard());
		}
	}

}
