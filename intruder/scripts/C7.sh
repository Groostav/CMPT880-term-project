java -ea  -cp $INTRUDER_CLASSPATH:./benchs/exo:./benchs/exo/exo.portal.webui.portal-3.8.2.Final:./benchs/exo/common-common-2.2.2.Final.jar:./classes intruder.wrapper.Main --user-specified-test ExoPortalTest -intruder-output-dir "./output/C7/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $OMEN_HOME/output/C7/"
echo "-------------------------------------------------------"
