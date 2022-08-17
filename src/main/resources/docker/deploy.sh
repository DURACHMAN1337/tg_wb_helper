#!/bin/bash

git fetch origin

cd "../../../.." && chmod +x gradlew && ./gradlew bootJar && cp build/libs/telegram_helper.jar ./src/main/resources/docker/telegram_helper.jar

cd "src/main/resources/docker" && docker build -t telegram_helper . && docker-compose up