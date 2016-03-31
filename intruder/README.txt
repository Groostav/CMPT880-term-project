INTRUDER: Atomicity violation test synthesis tool.
      
SETUP:
1. The current directory is “artifact270”. In the VM, go to Desktop/artifact270. 
2. Set the environment variables by executing command "source ./scripts/init.sh".
3. By default, the atomicity violation detection (Our implementation of CTrigger ASPLOS’09) is off. 
   Use this default setting initially so that the synthesis times will be similar to the reported numbers.
   In the paper we reported bare metal numbers. There maybe slowdown here due to running on VM. 
   Atomicity violation will not be reported, BUT, the process will be over quickly. Subsequently, you can 
   turn on detection and find the atomicity violations also. 

*****************************************
Important Note: We have improved our implementation and the number of tests synthesized currently varies 
from the numbers reported in the submitted version (for the better). Similarly, with the number of detected 
defects. More details present in CURRENT.txt. 
*****************************************

DETECTING ATOMICITY VIOLATIONS: 

Skip this section if you want to just synthesize atomicity violation tests and not detect atomicity violations 
on them (Faster). 

Read further, if you want to detect atomicity violations (Slower). 
Upto now, only the atomicity violation tests are synthesized. You can detect the atomicity violation using CTRIGGER 
by setting the environment variable "CTRIGGER" to "ON". 
       e.g., export CTRIGGER=ON 

When CTRIGGER is on and the above scripts are run again, tests will be synthesized 
and subsequently atomicity violations will be detected. This can take SUBSTANTIALLY more time 
(because CTRIGGER, will be trying to detect the atomicity violations). 

RUNNING A SIMPLE EXAMPLE:
   1. To test the tool, execute a simple example provided by us.
   2. Execute “sh ./scripts/test2.sh” from current directory.
   3. The source files is contained in tests/test2
   4. The library contains one atomicity violation which will be detected using one test case.
   5. See README in the tests/test2 directory to see what the test case should look like. 
   6. See the generated output: 
      a. The generated test cases are placed in ./output/test2 folder. 
         Each test case will be named “TestDriver<testNo>.java”, where 
         <testNo> is the index of each test case.
      b. The output of atomicity violation detection on test case “TestDriver<testNo>.java” 
         will be placed in “TestCase_<testNo>_output.txt”. Generated when CTRIGGER=ON. 
      c. The total number of generated tests and the total number of 
         detected atomicity violations (when CTRIGGER=ON) will be present as part of summary.txt. 
      d. The time taken for test synthesis is provided. Atomicity violation detection time 
         using CTRIGGER is not specified as it is not a focus of the current work. 

EXPLANATION OF THE TEST CASE for test2: 
  0. See ./output/test2/TestDriver0.java 
  1. Firstly, in main, the atomicity violating parameters are collected. 
  2. The comments above the collection mentions the run, the line number and 
     the sequential test that needs to be run to collect it. 
  3. Then imposeConstraints imposes the necessary constraints for setting up a
     atomicity violation. 
  4. The parameters are passed to two different threads and the threads are 
     started. 
  5. The parameters (that includes the receiver) are used appropriately to 
     invoke the atomicity violation inducing methods from different threads (foo from two
     threads). 

RUNNING ALL TESTS :
    1. To execute other tests that are provided execute “sh ./scripts/test<id>.sh” from the current directory, where <id>
       stands for the test case index.
    2. To execute all the test cases execute “sh ./scripts/testall.sh” from the current directory.

RUNNING BENCHMARKS : 
 1. To test a benchmark, execute “sh ./scripts/C<id>.sh” from the current directory.
 2. The output will be placed in ./output/C<id>
 3. To execute all the benchmarks execute “sh ./scripts/benchmark.sh” from the current directory.
 4. See CURRENT.txt for current data on the benchmarks. 
 5. The number of atomicity violations detected (when CTRIGGER=ON) is dependent on schedule that is followed
    during the detection and can vary from the numbers reported in the paper due to this. 
    However, number of tests synthesized should remain the same across runs as reported in CURRENT.txt. 
 6. Thread safety violations can be witnessed and lead to an exception 
    which can show up in the test output and can be safely discarded for 
    evaluation purposes. 

 7. The saved output of running the benchmarks from our experiments are placed in ./saved_output. The output of running the benchmarks on  
    the VM is placed in ./saved_output_vm for reference purposes.
  	
OPTIONS (not needed for AEC, useful for tools): We support the following run time options (see the script files for usage): 
	
	 1. intruder-output-dir     : The directory in which the output from the tool should be placed. By default it is set to
                                  “./output”. 
 	 2. user-specified-test : The fully qualified sequential test case class name. The compiled classes must be
                                  available in the classpath.


********* IMPORTANT NOTE************

Any file whose name contains keyword “Test” will be treated as input test case.

************************************

