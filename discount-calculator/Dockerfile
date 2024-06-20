FROM openjdk:16-jdk-alpine
LABEL MAINTAINER="https://github.com/senoritadeveloper01"
LABEL APPLICATION="Discount Calculator"
WORKDIR /usr/app
COPY target/discount-calculator-0.0.1-SNAPSHOT.jar discount-calculator-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","discount-calculator-0.0.1-SNAPSHOT.jar"]
EXPOSE 8000