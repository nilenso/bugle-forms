FROM openjdk:11-jre-bullseye

RUN mkdir -p bugle-forms-app
WORKDIR /bugle-forms-app

COPY target/uberjar/bugle-forms-latest-standalone.jar ./app-standalone.jar

EXPOSE 80

CMD java -jar ./app-standalone.jar
