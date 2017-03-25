::
:: SET THE CLASSPATH DYNAMICALLY
::
@echo off

:: Java Home that is compatible to run binary compiled under Java $java.version
:: set JAVA_HOME=

set JARS=
set CLASSPATH=
for %%i in (..\lib\*.jar) do call cappend.bat %%i
set CLASSPATH=%JARS%;..\conf

"%JAVA_HOME%\bin\java" -cp %CLASSPATH% id.ac.polibatam.mj.dcloud.main.Disperse %1 %2 %3