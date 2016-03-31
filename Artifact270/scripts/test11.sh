javac test/tests/test11/*.java
rm ./classes/test11/*.class
mv test/tests/test11/*.class ./classes/test11/
java -ea  -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test11.Test -intruder-output-dir "./output/test11/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test11/"
echo "-------------------------------------------------------"

