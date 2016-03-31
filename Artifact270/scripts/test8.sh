javac test/tests/test8/*.java
rm ./classes/test8/*.class
mv test/tests/test8/*.class ./classes/test8/
java -ea -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test8.Test -intruder-output-dir "./output/test8/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test8/"
echo "-------------------------------------------------------"
