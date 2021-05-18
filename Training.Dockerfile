FROM maven:3.6.3-jdk-11
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY ./ $PROJECT_DIR
RUN mvn --projects DomainModel,TrainingData -am install -DskipTests=true

FROM openjdk:11-jdk
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY --from=0 $PROJECT_DIR/TrainingData/target/TrainingData* $PROJECT_DIR/
EXPOSE 8086
ENTRYPOINT ["java"]
CMD ["-jar", "-Dspring.profiles.active=node2", "/opt/project/TrainingData-1.0-SNAPSHOT.jar"]