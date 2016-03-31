javac test/tests/test13/*.java
rm ./classes/test13/*.class
mv test/tests/test13/*.class ./classes/test13/
java -ea  -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test13.Test -intruder-output-dir ./output/test13/ >tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test13/"
echo "-------------------------------------------------------"

