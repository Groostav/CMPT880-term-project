javac test/tests/test2/*.java
rm ./classes/test2/*.class
mv test/tests/test2/*.class ./classes/test2/
echo "Atomicity violation here"
java -ea -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test2.Test -amen.runwolf "true" -amen.kappa "1" -intruder-output-dir "./output/test2/" > tmp.txt
#sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test2/"
echo "-------------------------------------------------------"
