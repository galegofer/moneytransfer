FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.15_10
RUN apk add --update \
    curl \
    && rm -rf /var/cache/apk/*
HEALTHCHECK --interval=30s --timeout=4s CMD curl -f http://localhost:8080/actuator/health || exit 1
RUN mkdir /opt/application
COPY target/moneytransfer-*-SNAPSHOT.jar /opt/application/application.jar
WORKDIR /opt/application
ENTRYPOINT ["java", "-jar", "application.jar"]
