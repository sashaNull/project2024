@echo off
echo Stopping MongoDB...
taskkill /IM mongod.exe /F

echo Stopping node
taskkill /IM node.exe /F

echo All processes stopped.
pause
