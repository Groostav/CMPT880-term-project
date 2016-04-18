# Atomicity detection and regression test generation through Feedback driven random test generation #

## Abstract ##
Writing concurrency tests is enormously difficult: aside from the lack of expressiveness around parallelism and concurrency in popular testing frameworks, finding and laying-out the setup required to push a program into a state where it may violate atomicity rules is difficult. This makes automated generation of such tests very attractive. [punchline: we need to drive intruder with randoop!]

[problems with writing concurrent code. Need for automated test generation]

[randoop]

[intruder -- mention that we didn't really run down the quality of the atomicity violations themselves. Ultimately we took their number of 'violations' as final even if they didn't result in a bug.]

[plugging randoop into intruder; what could go wrong! Key point: how well will randoop mimmick real access patterns to showcase potential atomicity violations]

[scripting randoop to drive intruder; 'porter']

[import problems, library problems, build problems. Build system wet-work. Ultimately correcting broken import headers was beyond the scope of this project]

[manually running tests]

[results table]

[code coverage figures?]

[some general guidelines i've learned to avoid atomicity violations. Namely, ref-transparent programming, private locks.]

[conclusion: randoop's coverage strategy doesn't necessarily map to atomicity discovery. Inherently it finds some, possibly the most common, violations but it does not find all of them.]

