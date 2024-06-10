@echo off
echo Starting MongoDB...
start "" "C:\Program Files\MongoDB\Server\7.0\bin\mongod" --dbpath ./db_directory
timeout /t 10

echo Changing directory to admin...
cd /d %~dp0admin

echo Starting admin.js...
start "" "node" admin.js
timeout /t 6

echo Starting api.js...
start "" "node" api.js

echo Deployment started successfully.
pause
