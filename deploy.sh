#!/bin/bash

#                                      [ WARNING ]
#                Need to install 'sshpass' tool before run this file locally!
#

#
#                                       - VARIABLES -
#

# current local dir path
currentLocalDirPath="${PWD}"

# local path to jar
localPathToJar="$currentLocalDirPath/build/libs/telegram_helper.jar"

# remote app folder
remoteAppFolderName="wellcum"

# .env keys (will be written in deploy process)
databasePortEnv="TELEGRAM_HELPER_DB_PORT"
applicationPortEnv="TELEGRAM_HELPER_PORT"
telegramBotTokenEnv="TELEGRAM_HELPER_BOT_TOKEN"
telegramAdminUsernameEnv="TELEGRAM_HELPER_ADMIN_USERNAME"
feedbackChannelIdEnv="TELEGRAM_HELPER_FEEDBACK_CHANNEL_ID"
feedbackChannelChatIdEnv="TELEGRAM_HELPER_FEEDBACK_CHANNEL_CHAT_ID"

#
#                                        - METHODS -
#

# return exit code '1' (ERROR_CODE) log exception message in console
throwException() {
  echo "[EXCEPTION]: Wrong credentials parameters count"
  exit 1
}

# read and save remote machine public ipv4 from console
readRemoteMachinePublicIpV4() {
  read -r -p "Enter public ipV4: " value
  echo "$value"
}

# read and save remote machine username for ssh connection from console
readRemoteMachineSshUsername() {
  read -r -p "Enter the username for ssh connection: " value
  echo "$value"
}

# read and save remote machine password for ssh connection from console
readRemoteMachineSshPassword() {
  read -r -s -p "Enter the password for ssh connection: " value
  echo "$value"
}

# read and save database container port
readDatabasePort() {
  local defaultValue="5432"
  read -r -s -p "Database port bindings (docker container) (press <ENTER> for use default value '$defaultValue'):" value
  if [ -z "$value" ]; then
    echo "$defaultValue"
  else
    echo "$value"
  fi
}

# read and save application container port
readApplicationPort() {
  local defaultValue="8080"
  read -r -s -p "Application port bindings (docker container) (press <ENTER> for use default value '$defaultValue'):" value
  if [ -z "$value" ]; then
    echo "$defaultValue"
  else
    echo "$value"
  fi
}

# read and save telegram bot token
readTelegramBotToken() {
  read -r -s -p "Enter the telegram bot token: " value
  echo "$value"
}

# read and save telegram bot admin username
readTelegramBotAdminUsername() {
  read -r -s -p "Enter the telegram bot admin username (must start with @): " value
  echo "$value"
}

# read and save channel ID
readTelegramBotChannelId() {
  read -r -s -p "Enter the telegram bot channel ID: " value
  echo "$value"
}

# read and save channel chat ID
readTelegramBotChannelChatId() {
  read -r -s -p "Enter the telegram bot channel chat ID: " value
  echo "$value"
}

# initialize parameters from console if executed locally
configureParametersFromConsole() {
  remoteMachineAddress=$(readRemoteMachinePublicIpV4)
  remoteMachineSshUser=$(readRemoteMachineSshUsername)
  remoteMachineSshPassword=$(readRemoteMachineSshPassword)

  databasePort=$(readDatabasePort)
  applicationPort=$(readApplicationPort)

  telegramBotToken=$(readTelegramBotToken)
  telegramAdminUsername=$(readTelegramBotAdminUsername)
  feedbackChannelId=$(readTelegramBotChannelId)
  feedbackChannelChatId=$(readTelegramBotChannelChatId)
}

# update current project
updateProject() {
  # git checkout master && git pull
  git pull
}

# build actual jar file via gradlew
buildJar() {
  chmod +x gradlew && ./gradlew bootJar
}

# base method for execute command via ssh [pass your command as function argument]
executeCommandViaSsh() {
  if [ "$#" -ne 1 ] || [ -z "$1" ]; then
    echo "Please, enter the command (command cannot be empty)"
    throwException
  else
    sshpass -p "$remoteMachineSshPassword" ssh -tt -o 'StrictHostKeyChecking=no' "$remoteMachineSshUser"@"$remoteMachineAddress" "$1"
  fi
}

# check application dir on remote machine
checkApplicationDirOnRemoteMachine() {
  echo "Checking application folder on remote machine..."
  executeCommandViaSsh "sudo mkdir -p $remotePathAppFolder"
  executeCommandViaSsh "sudo chmod 777 $remotePathAppFolder"
}

# copy jar file on remote machine via sshpass and scp
copyJarFile() {
  echo "Copying JAR file..."
  sshpass -p "$remoteMachineSshPassword" scp "$localPathToJar" "$remoteMachineSshUser"@"$remoteMachineAddress"":$remotePathAppFolder/telegram_helper.jar"
}

# copy Dockerfile
copyDockerFile() {
  echo "Copying Dockerfile..."
  sshpass -p "$remoteMachineSshPassword" scp "$currentLocalDirPath/Dockerfile" "$remoteMachineSshUser"@"$remoteMachineAddress"":$remotePathAppFolder/Dockerfile"
}

# copy .env
copyDotEnvFile() {
  echo "Copying .env file..."
  sshpass -p "$remoteMachineSshPassword" scp "$currentLocalDirPath/.env" "$remoteMachineSshUser"@"$remoteMachineAddress"":$remotePathAppFolder/.env"
}

# copy docker-compose.yaml
copyDockerComposeFile() {
  echo "Copying docker-compose.yaml file...."
  sshpass -p "$remoteMachineSshPassword" scp "$currentLocalDirPath/docker-compose.yaml" "$remoteMachineSshUser"@"$remoteMachineAddress"":$remotePathAppFolder/docker-compose.yaml"
}

# process and write environment variables to '.env' file
prepareFiles() {
  echo "Preparing files..."
  writeEnvironmentVariables
}

writeEnvironmentVariables() {
  echo "Writing variables to '.env' file..."
  {
    # port binding:
    printf "\n%s=%s" "$databasePortEnv" "$databasePort"
    printf "\n%s=%s" "$applicationPortEnv" "$applicationPort"

    # telegram info:
    printf "\n%s=%s" "$telegramBotTokenEnv" "$telegramBotToken"
    printf "\n%s=%s" "$telegramAdminUsernameEnv" "$telegramAdminUsername"
    printf "\n%s=%s" "$feedbackChannelIdEnv" "$feedbackChannelId"
    printf "\n%s=%s" "$feedbackChannelChatIdEnv" "$feedbackChannelChatId"

  } >> "$currentLocalDirPath/.env"
}

copyFilesToRemoteMachine() {
  copyJarFile
  copyDockerFile
  copyDotEnvFile
  copyDockerComposeFile
}

# run docker build
dockerBuild() {
  echo "Running docker build..."
  executeCommandViaSsh "docker build -t telegram_helper_wellcum $remotePathAppFolder"
}

# execute 'docker-compose up' command on remote machine in app folder
dockerComposeUp() {
  executeCommandViaSsh "cd $remotePathAppFolder && docker-compose up -d"
}

#
#                                            - DEPLOY -
#

# main method that collect all method in right order (execution below)
deploy() {
  echo "Starting deploy..."

  if [ "$#" -eq 0 ]; then # if parameters not passed (e.g. executed from local machine)
    configureParametersFromConsole
  elif [ "$#" -eq 9 ]; then # if parameters passed, initialize bash environments
    # remote machine:
    remoteMachineAddress="$1"
    remoteMachineSshUser="$2"
    remoteMachineSshPassword="$3"

    databasePort="$4"
    applicationPort="$5"

    telegramBotToken="$6"
    telegramAdminUsername="$7"
    feedbackChannelId="$8"
    feedbackChannelChatId="$9"

  # wrong count of passed parameters. Throw exception. Stop deploy process.
  else
    echo "[EXCEPTION]: Wrong parameters count was found"
    throwException
  fi

  # remote path to jar
  remotePathAppFolder="/home/$remoteMachineSshUser/$remoteAppFolderName"

  updateProject

  buildJar

  prepareFiles

  checkApplicationDirOnRemoteMachine
  copyFilesToRemoteMachine

  # dockerBuild - now we use 'build' section in docker-compose.yaml file
  dockerComposeUp
}

# deploy method execution
if [ "$#" -eq 0 ]; then # script has been executed without parameters.
  deploy                # execute deploy method without params [parameters will be configured from console].

elif [ "$#" -eq 9 ]; then # if parameters passed.
  deploy "$1" "$2" "$3" "$4" "$5" "$6" "$7" "$8" "$9"

else
  throwException # wrong count of passed parameters. Throw exception. Stop deploy process.
fi
