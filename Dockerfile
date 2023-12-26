FROM eclipse-temurin:17-jre


RUN mkdir /app

WORKDIR /app

ADD ./api/target/ape-routing-api-1.0.0-SNAPSHOT.jar /app

EXPOSE 8080

CMD ["java", "-jar", "ape-routing-api-1.0.0-SNAPSHOT.jar"]
#ENTRYPOINT ["java", "-jar", "ape-routing-api-1.0.0-SNAPSHOT.jar"]
#CMD java -jar ape-routing-api-1.0.0-SNAPSHOT.jar
