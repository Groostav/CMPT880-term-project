java -ea  -cp $INTRUDER_CLASSPATH:./benchs/:./benchs/colt:./classes intruder.wrapper.Main --user-specified-test ColtTest -intruder-output-dir ./output/C1/ > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $INTRUDER_HOME/output/C1/"
echo "-------------------------------------------------------"
