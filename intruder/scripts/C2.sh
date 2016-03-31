java -ea  -cp $INTRUDER_CLASSPATH:./benchs/StringBuffer:./classes intruder.wrapper.Main --user-specified-test StringBufferTest  -intruder-output-dir "./output/C2/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/C2/"
echo "-------------------------------------------------------"
