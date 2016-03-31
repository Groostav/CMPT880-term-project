javac test/tests/test6/*.java
rm ./classes/test6/*.class
mv test/tests/test6/*.class ./classes/test6/
java -ea -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test6.Test -intruder-output-dir "./output/test6/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test6/"
echo "-------------------------------------------------------"
