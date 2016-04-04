#ook, so, I wrote porter, it looks ok, likely has a few bugs I'll iron out during the week
#so we need to write a script thats something like this
#term-project.sh:

#step 1 run randoop
java -classpath ./* randoop.main.Main gentests --testclass=java.util.TreeSet --timelimit=10 --junit-output-dir=./randoop-gen

#step 2 run porter
java -jar porter.jar --source-file ./randoop-gen/RegressionTest0.java --source-method test001 --output-dir ./porter-gen

#step 3.1 compile for intruder
javac randoop-gen/RegressionTest0.java, porter-gen/RegressionTest0Driver.java --output ./intruder-ready

#step 3.0 run intruder
java -ea -cp $INTRUDER_CLASSPATH:./intruder-ready intruder.wrapper.Main --user-specified-test RegressionTest0Driver.java -amen.runwolf "true" -amen.kappa "1" -intruder-output-dir "./intruder-report/"

