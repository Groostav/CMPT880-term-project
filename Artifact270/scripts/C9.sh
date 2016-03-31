java -ea  -cp $INTRUDER_CLASSPATH:./benchs/opennlp:./benchs/opennlp/opennlp-tools-1.5.3:./classes intruder.wrapper.Main --user-specified-test OpenNlpTest -intruder-output-dir "./output/C9/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $OMEN_HOME/output/C9/"
echo "-------------------------------------------------------"
