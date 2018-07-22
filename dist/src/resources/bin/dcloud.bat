::
:: SET THE CLASSPATH DYNAMICALLY
::
@echo off

SETLOCAL 
cd /D "%~dp0"

:: Java Home that is compatible to run binary compiled under Java $java.version
:: set JAVA_HOME=

SET JARS=
SET CLASSPATH=
FOR %%i IN (..\lib\*.jar) DO CALL cappend.bat %%i
SET CLASSPATH=%JARS%;..\conf

"%JAVA_HOME%\bin\java" -cp %CLASSPATH% id.ac.polibatam.mj.dcloud.main.DcloudMain %*

ENDLOCAL