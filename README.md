# markov-chain

## Overview
A trainable markov chain generator

## Whats a Markov chain?

A mathematical system that undergoes transitions from one state to another, between a finite or countable number of possible states. It is a random process usually characterized as memoryless: the next state depends only on the current state and not on the sequence of events that preceded it. --[Wikipedia][wikipedia]

## So, what do you do with it?
When trained on a corpus of values it can then generate similar patterns. Or if trained with a bunch of words it will make new words that sound like the original words.

It makes a really good random name generator for old school RPGs. 

[It can also make you look for a new job.][wtf]

## Usage 
```
Usage: markov [OPTION]

  -c[K]
		generate K lines of output
  -e
		include an 'end' symbol when training sequences. if an 'end' is
		encountered during generation it will terminate the current chain
		and start a new one
  -g
		outputs state transition diagram in Graphviz compliant notation
  -h
		prints this message
  -J
		stdin is to be parsed as json not as a training corpus
  -j
		outputs state transition diagram as json
  -v
		outputs debug information on stderr

If no -e, it uses the normal distribution of the trained input to determine chain length.
If no -j, -g or, -c, if will continuously output chains until stopped.
If no -J, data on stdin is processed as a training corpus.
```
## Contributing

If you want to contribute, just fork and submit a pull request!

## Changelog

- v1.0
    - Export and import of transition state diagram in json format.
- v0.1 
    - Initial Release

[wtf]: http://thedailywtf.com/Articles/The-Automated-Curse-Generator.aspx
[wikipedia]: http://en.wikipedia.org/wiki/Markov_chain