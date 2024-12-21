@echo off
set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-11.0.25.9-hotspot
set PATH=%JAVA_HOME%\bin;%PATH%
mvn clean install
