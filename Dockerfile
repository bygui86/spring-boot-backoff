
FROM openjdk:8-jre-alpine

ADD target/ /opt/
WORKDIR /opt

EXPOSE 8081

ENTRYPOINT exec java $JAVA_OPTS -jar spring-boot-backoff.jar
