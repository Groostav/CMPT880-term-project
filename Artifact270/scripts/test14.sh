javac test/tests/test14/*.java
rm ./classes/test14/*.class
mv test/tests/test14/*.class ./classes/test14/
java -ea  -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test14.Test -intruder-output-dir ./output/test14/ -violation-depth 2 >tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test14/"
echo "-------------------------------------------------------"
