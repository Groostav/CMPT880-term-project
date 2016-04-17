#!/bin/bash

#Created by Rafael on Apr 08 2016

#TODO --help

#************** Command line examples ************
#sh ./runner.sh --randoop-testclass=java.util.TreeSet --source-method=test001 --package-name=java.util --CTRIGGER=ON

#sh ./runner.sh -rtc=java.util.TreeSet -sm=test001 -pn=java.util
#*************************************************

#************** Enable debug mode ****************
#set -x 
#*************************************************
for i in "$@"
do
case $i in
	-rtc=*|--randoop-testclass=*)
    randoop_testclass="${i#*=}"
    shift # past argument=value
    ;;        
    -sm=*|--source-method=*)
    source_method="${i#*=}"
    shift # past argument=value
    ;;
    -pn=*|--package-name=*)
    CTRIGGER=="${i#*=}"
    shift # past argument=value
    ;;
    -ct=*|--CTRIGGER=*)
    C=="${i#*=}"
    shift # past argument=value
    ;;    -rod=*|--randoop-output-dir=*)
    randoop_output_dir="${i#*=}"
    shift # past argument=value
    ;;
    -pod=*|--porter-output-dir=*)
    porter_output_dir="${i#*=}"
    shift # past argument=value
    ;;
    -iid=*|--intruder-input-dir=*)
    intruder_input_dir="${i#*=}"
    shift # past argument=value
    ;;        
    -iod=*|--intruder-output-dir=*)
    intruder_output_dir="${i#*=}"
    shift # past argument=value
    ;;        
    *)
    ;;
esac
done


#** Validating arguments and Setting defaults ***

timelimit=10;
RegressionTest="RegressionTest0";

#export CTRIGGER=OFF;

export INTRUDER_CLASSPATH=./lib/sootall-2.3.0.jar:./lib/concurrent.jar:./lib/randoop.jar:./lib/tools.jar:./lib/collections.jar:./lib/jsr305-1.3.9.jar:./lib/junit-4.11.jar:./lib/amen.jar:./lib/geotools_r.jar:./lib/jts-1.8.jar

#flag=1;
#echo "source method: " $source_method;

#TODO: implement loop to run several tests 



if [ ! "$randoop_output_dir" ]; then
  randoop_output_dir="randoop_gen";
fi

if [ ! "$porter_output_dir" ]; then
  porter_output_dir="porter_gen";
fi

if [ ! "$intruder_input_dir" ]; then
  intruder_input_dir="intruder_input";
fi

if [ ! "$intruder_output_dir" ]; then
  intruder_output_dir="intruder_report";
fi

#TODO: validate the rest of the arguments

#*************************************************



#********* Reset output directories *********

rm -rf $randoop_output_dir;
rm -rf $porter_output_dir;
rm -rf $intruder_input_dir;
rm -rf intruder/classes/"$porter_output_dir";

mkdir $randoop_output_dir;
mkdir $porter_output_dir;
mkdir $intruder_input_dir;
mkdir $intruder_output_dir;

#*************************************************



echo "**********************************************************"
echo "*                  Running randoop                       *"
echo "**********************************************************"

java -classpath ./plume.jar:./porter.jar:./randoop.jar:lib_to_test/* randoop.main.Main gentests --testclass=$randoop_testclass --timelimit=60 --junit-output-dir=$randoop_output_dir

echo "**********************************************************"
echo "*                   Running porter                       *"
echo "**********************************************************"

java -jar porter.jar --source-file $randoop_output_dir/$RegressionTest.java --source-method $source_method --output-dir $porter_output_dir #--package-name $package_name


echo "**********************************************************"
echo "*                Adding Package Name                     *"
echo "**********************************************************"

#adding package name

echo 'package '"$porter_output_dir"';'

 echo 'package '"$porter_output_dir"';' | cat - './'"$randoop_output_dir"'/'"$RegressionTest"'.java' > temp && mv temp './'"$randoop_output_dir"'/'"$RegressionTest"'.java'

 echo 'package '"$porter_output_dir"';' | cat - './'"$porter_output_dir"'/'"$RegressionTest"'Driver.java' > temp && mv temp './'"$porter_output_dir"'/'"$RegressionTest"'Driver.java'


echo "**********************************************************"
echo "*                      Compiling                         *"
echo "**********************************************************"


javac -cp .:intruder/lib/junit-4.11.jar:lib_to_test/* $porter_output_dir/"$RegressionTest"Driver.java $randoop_output_dir/"$RegressionTest".java -d $intruder_input_dir

cp -rf "$intruder_input_dir"/"$porter_output_dir" intruder/classes/"$porter_output_dir"

echo "**********************************************************"
echo "*                      Indruding                         *"
echo "**********************************************************"



cd intruder

java -ea -cp $INTRUDER_CLASSPATH:./classes:../lib_to_test/* intruder.wrapper.Main --user-specified-test "$porter_output_dir"."$RegressionTest"Driver -amen.runwolf true -amen.kappa 1 -intruder-output-dir ../intruder-report


cd ..







#java -classpath ./plume.jar:./porter.jar:./randoop.jar randoop.main.Main gentests --testclass=$randoop_testclass --timelimit=$timelimit --junit-output-dir=$randoop_output_dir

#javac -cp .:intruder/lib/junit-4.11.jar $porter_output_dir/"$RegressionTest".java $porter_output_dir/"$RegressionTest"Driver.java -d $intruder_input_dir


#javac -cp .:intruder/lib/junit-4.11.jar randoop-gen/"$RegressionTest".java -d $intruder_input_dir
#exit 0;

#javac -cp .:$intruder_input_dir/"$RegressionTest".class $randoop_output_dir/"$RegressionTest"Driver.java -d $intruder_input_dir


#rm -rf ./intruder/intruder-ready
#mv ./intruder-ready ./intruder/intruder-ready/.
#rm -rf ./intruder/output_porter
#mv output ./intruder/intruder-input
#mv output_porter intruder/output_porter



#java -ea -cp $INTRUDER_CLASSPATH:./intruder_input intruder.wrapper.Main --user-specified-test ../porter-gen/RegressionTest0Driver.java -amen.runwolf "true" -amen.kappa "1" -intruder-output-dir ./intruder_output
