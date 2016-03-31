java -ea  -cp $INTRUDER_CLASSPATH:./benchs/exo:./benchs/exo/exo.portal.webui.portal-3.8.2.Final:./benchs/exo/common-common-2.2.2.Final.jar:./classes intruder.wrapper.Main --user-specified-test ExoApplTest -intruder-output-dir "./output/C6/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $OMEN_HOME/output/C6/"
echo "-------------------------------------------------------"
