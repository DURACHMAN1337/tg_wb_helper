FROM openjdk:17-slim

EXPOSE 8080

COPY . /opt/telegram_helper

CMD java -Dapp.home=/opt/telegram_helper_home -jar /opt/telegram_helper/telegram_helper.jar