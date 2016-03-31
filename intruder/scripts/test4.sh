javac test/tests/test4/*.java
rm ./classes/test4/*.class
mv test/tests/test4/*.class ./classes/test4/
echo "Atomicity violation here"
java -ea -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test4.Test -intruder-output-dir "./output/test4/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test4/"
echo "-------------------------------------------------------"
