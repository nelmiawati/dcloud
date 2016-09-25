#!/bin/bash

##
## SET THE CLASSPATH DYNAMICALLY
##

## Java Home that is compatible to run binary compiled under Java $java.version
## export JAVA_HOME=

export CLASSPATH=
for i in `ls ../lib/*.jar`
do
  CLASSPATH=${CLASSPATH}:${i}
done
CLASSPATH=${CLASSPATH}:../conf

$JAVA_HOME/bin/java -cp "${CLASSPATH}" id.ac.polibatam.mj.dcloud.main.Disperse $1 $2 $3