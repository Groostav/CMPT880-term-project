java -ea  -cp $INTRUDER_CLASSPATH:./benchs/batik:./benchs/batik/batik-all:./classes intruder.wrapper.Main --user-specified-test BatikCGNTest -intruder-output-dir "./output/C8/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $OMEN_HOME/output/C8/"
echo "-------------------------------------------------------"
