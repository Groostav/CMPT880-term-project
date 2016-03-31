java -ea  -cp $INTRUDER_CLASSPATH:./benchs/Vector:./classes intruder.wrapper.Main --user-specified-test VectorTest  -intruder-output-dir "./output/C3/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $OMEN_HOME/output/C3/"
echo "-------------------------------------------------------"
