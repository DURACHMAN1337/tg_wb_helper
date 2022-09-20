FROM openjdk:17-slim

ARG TELEGRAM_HELPER_PORT

ENV TELEGRAM_HELPER_PORT ${TELEGRAM_HELPER_PORT?8080}

EXPOSE $TELEGRAM_HELPER_PORT

COPY . /opt/telegram_helper

CMD java -Dapp.home=/opt/telegram_helper_home -jar /opt/telegram_helper/telegram_helper.jar