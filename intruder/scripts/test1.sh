javac test/tests/test1/*.java
rm ./classes/test1/*.class
mv test/tests/test1/*.class ./classes/test1/
echo "No atomicity violation here"
java -ea  -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test1.Test -amen.runwolf "true" -amen.kappa "5"  -intruder-output-dir "./output/test1/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test1/"
echo "-------------------------------------------------------"
