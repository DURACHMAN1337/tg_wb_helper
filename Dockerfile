FROM openjdk:17-slim

EXPOSE ${TELEGRAM_HELPER_PORT}

COPY . /opt/telegram_helper

CMD java -Dapp.home=/opt/telegram_helper_home -jar /opt/telegram_helper/telegram_helper.jar