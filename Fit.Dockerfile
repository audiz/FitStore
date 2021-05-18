FROM maven:3.6.3-jdk-11
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY ./ $PROJECT_DIR
RUN mvn --projects DomainModel,GarminFitReader -am install -DskipTests=true

FROM openjdk:11-jdk
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY --from=0 $PROJECT_DIR/GarminFitReader/target/GarminFitReader* $PROJECT_DIR/
EXPOSE 8081
ENTRYPOINT ["java"]
CMD ["-jar", "-Dspring.profiles.active=node1", "/opt/project/GarminFitReader-1.0-SNAPSHOT.jar"]