javac test/tests/test7/*.java
rm ./classes/test7/*.class
mv test/tests/test7/*.class ./classes/test7/
java -ea -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test7.Test -intruder-output-dir "./output/test7/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test7/"
echo "-------------------------------------------------------"
