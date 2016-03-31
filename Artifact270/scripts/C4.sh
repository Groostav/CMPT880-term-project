java -ea  -cp $INTRUDER_CLASSPATH:./benchs/carbonado:./benchs/carbonado/carbonado-1.2.3:./lib/cojen-2.2.1.jar:./classes intruder.wrapper.Main --user-specified-test CarbonadoTest -random-sleep false -intruder-output-dir "./output/C4/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $OMEN_HOME/output/C4/"
echo "-------------------------------------------------------"

