javac test/tests/test9/*.java
rm ./classes/test9/*.class
mv test/tests/test9/*.class ./classes/test9/
java -ea  -cp $INTRUDER_CLASSPATH:./classes intruder.wrapper.Main --user-specified-test test9.Test -intruder-output-dir "./output/test9/" > tmp.txt
sh ./scripts/cleanup.sh
echo "-------------------------------------------------------"
echo "Output of analysis placed in $AMEN_HOME/output/test9/"
echo "-------------------------------------------------------"

