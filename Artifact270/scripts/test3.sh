javac test/tests/test3/*.java
rm ./classes/test3/*.class
mv test/tests/test3/*.class ./classes/test3/
echo "No atomicity violation here"
java -ea -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main -user-specified-test test3.Test -intruder-output-dir "./output/test3/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test3/"
echo "-------------------------------------------------------"
