@echo off
title Enterprise Order System - Monolith Launcher
color 0B

set MVN=C:\Users\pprat\.m2\wrapper\dists\apache-maven-3.9.16\0daed3be3ebd1c706f0e69e8b07c6b73f5cc4ea3dfce72a8d0ec2e849ca2ddb0\bin\mvn.cmd
set JAVA_HOME=C:\Program Files\Java\jdk-19
set PROJECT=C:\Users\pprat\eclipse-workspace\enterprise-order-system-monolith

echo.
echo =====================================================
echo   ENTERPRISE ORDER SYSTEM - MONOLITH LAUNCHER
echo =====================================================
echo.
echo Starting Monolithic Application on port 8080...
echo (No Kafka, ZooKeeper, or API Gateway needed!)
echo.
cd /d %PROJECT%
"%MVN%" spring-boot:run
