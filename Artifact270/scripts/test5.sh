javac test/tests/test5/*.java
rm ./classes/test5/*.class
mv test/tests/test5/*.class ./classes/test5/
java -ea -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test5.Test -intruder-output-dir "./output/test5/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test5/"
echo "-------------------------------------------------------"
