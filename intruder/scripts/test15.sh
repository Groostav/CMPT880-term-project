javac test/tests/test15/*.java
rm ./classes/test15/*.class
mv test/tests/test15/*.class ./classes/test15/
java -ea  -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test15.Test -intruder-output-dir ./output/test15/ >tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test15/"
echo "-------------------------------------------------------"
