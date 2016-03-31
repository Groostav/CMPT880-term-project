javac test/tests/test10/*.java
rm ./classes/test10/*.class
mv test/tests/test10/*.class ./classes/test10/
java -ea -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test10.Test -intruder-output-dir "./output/test10/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test10/"
echo "-------------------------------------------------------"
