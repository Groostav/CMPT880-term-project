java -ea  -cp $INTRUDER_CLASSPATH:./benchs/cometd:./benchs/cometd/cometd-java-client-2.7.0-RC1:./lib/cometd-java-common-2.7.0.jar:./lib/bayeux-api-2.8.0.jar:./lib/jetty-util-7.0.0.M2.jar:./classes intruder.wrapper.Main --user-specified-test CometdTest -intruder-output-dir "./output/C5/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $OMEN_HOME/output/C5/"
echo "-------------------------------------------------------"
