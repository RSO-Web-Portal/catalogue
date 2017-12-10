FROM openjdk:8

RUN mkdir /catalogue

WORKDIR /catalogue

COPY . ./catalogue
ADD . /catalogue

EXPOSE 8082

CMD ["java", "-jar", "target/catalogue-service-2.5.0-SNAPSHOT.jar"]