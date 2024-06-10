@echo off
echo Stopping MongoDB...
taskkill /IM mongod.exe /F

echo Stopping admin.js...
taskkill /IM node.exe /FI "WINDOWTITLE eq admin.js*"

echo Stopping api.js...
taskkill /IM node.exe /FI "WINDOWTITLE eq api.js*"

echo All processes stopped.
pause
