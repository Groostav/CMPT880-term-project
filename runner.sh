#!/bin/bash

#Created by Rafael on Apr 08 2016

#TODO --help

#************** Command line examples ************
#sh ./runner.sh --randoop-testclass=java.util.TreeSet --source-method=test001 --package-name=java.util

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
    package_name=="${i#*=}"
    shift # past argument=value
    ;;
    -rod=*|--randoop-output-dir=*)
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
#TODO: implement loop to run several tests 

if [ ! "$randoop_output_dir" ]; then
  randoop_output_dir="./randoop-gen";
fi

if [ ! "$porter_output_dir" ]; then
  porter_output_dir="./porter-gen";
fi

if [ ! "$intruder_input_dir" ]; then
  intruder_input_dir="./indruder-input";
fi

if [ ! "$intruder_output_dir" ]; then
  intruder_output_dir="./indruder-report";
fi

#TODO: validate the rest of the arguments

#*************************************************



#********* Validating output directories *********
if [ ! -d "$randoop_output_dir" ]; then
	mkdir $randoop_output_dir;
fi

if [ ! -d "$porter_output_dir" ]; then
  mkdir $porter_output_dir;
fi

if [ ! -d "$intruder_input_dir" ]; then
  mkdir $intruder_input_dir;
fi

if [ ! -d "$intruder_output_dir" ]; then
  mkdir $intruder_output_dir;
fi
#*************************************************


echo "**********************************************************"
echo "*                  Running randoop                       *"
echo "**********************************************************"

java -classpath ./plume.jar:./porter.jar:./randoop.jar randoop.main.Main gentests --testclass=$randoop_testclass --timelimit=$timelimit --junit-output-dir=$randoop_output_dir

echo "**********************************************************"
echo "*                   Running porter                       *"
echo "**********************************************************"

java -jar porter.jar --source-file $randoop_output_dir/$RegressionTest.java --source-method $source_method --package-name $package_name --output-dir $porter_output_dir

echo "**********************************************************"
echo "*                      Compiling                         *"
echo "**********************************************************"


javac -cp .:intruder/lib/junit-4.11.jar randoop-gen/"$RegressionTest".java -d $intruder_input_dir

javac $randoop_output_dir/"$RegressionTest"Driver.java -d $intruder_input_dir

echo "**********************************************************"
echo "*                      Indruding                         *"
echo "**********************************************************"

#step 3.0 run intruder
java -ea -cp $INTRUDER_CLASSPATH:./intruder-ready intruder.wrapper.Main --user-specified-test "$RegressionTest"Driver.java -amen.runwolf "true" -amen.kappa "1" -intruder-output-dir $intruder_output_dir


