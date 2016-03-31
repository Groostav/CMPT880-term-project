javac test/tests/test12/*.java
rm ./classes/test12/*.class
mv test/tests/test12/*.class ./classes/test12/
java -ea  -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test12.Test -intruder-output-dir ./output/test12/ >tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test12/"
echo "-------------------------------------------------------"
