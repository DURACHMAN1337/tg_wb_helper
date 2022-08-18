#!/bin/bash

# !!!! WARNING !!!! need to install 'sshpass' before run this file

# current local dir path
currentLocalDirPath="${PWD}"

# remote machine credentials
remoteMachineAddress="158.160.9.88"
remoteMachineSshUser="telegramhelper"
remoteMachineSshPassword="123"

# local path to jar
localPathToJar="$currentLocalDirPath/build/libs/telegram_helper.jar"

# remote app folder
remoteAppFolderName="app"

# remote path to jar
remotePathAppFolder="/home/$remoteMachineSshUser/$remoteAppFolderName"

# update current project
git fetch origin

# build actual jar file via gradlew
chmod +x gradlew && ./gradlew bootJar

sshpass -p "$remoteMachineSshPassword" ssh "$remoteMachineSshUser"@"$remoteMachineAddress" mkdir "$remotePathAppFolder"

#copy jar file on remote machine via sshpass and scp
sshpass -p "$remoteMachineSshPassword" scp "$localPathToJar" "$remoteMachineSshUser"@"$remoteMachineAddress"":$remotePathAppFolder/telegram_helper.jar"

# copy Dockerfile
sshpass -p "$remoteMachineSshPassword" scp "$currentLocalDirPath/Dockerfile" "$remoteMachineSshUser"@"$remoteMachineAddress"":$remotePathAppFolder/Dockerfile"

# copy .env
sshpass -p "$remoteMachineSshPassword" scp "$currentLocalDirPath/.env" "$remoteMachineSshUser"@"$remoteMachineAddress"":$remotePathAppFolder/.env"

# copy docker-compose.yaml
sshpass -p $remoteMachineSshPassword scp "$currentLocalDirPath/docker-compose.yaml" "$remoteMachineSshUser"@"$remoteMachineAddress"":$remotePathAppFolder/docker-compose.yaml"

# run docker build
sshpass -p "$remoteMachineSshPassword" ssh "$remoteMachineSshUser"@"$remoteMachineAddress" docker build -t telegram_helper "$remotePathAppFolder"

# execute 'docker-compose up' manually on remote machine [OR FIX IT BITCH]