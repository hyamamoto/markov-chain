package markov_chain.model;

import java.util.List;

public interface Generator<T> {
	public List<T> generate();
}
