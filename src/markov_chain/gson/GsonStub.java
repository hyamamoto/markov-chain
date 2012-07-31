package markov_chain.gson;

import java.util.HashMap;

public class GsonStub {

	public static class StateStub {
		public HashMap<String, Integer> transitions;

		@Override
		public String toString() {
			return transitions.toString();
		}
	}

	public HashMap<String, StateStub> states;


}
