FROM maven:3.6.3-jdk-11
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY ./ $PROJECT_DIR
RUN mvn --projects DomainModel,facade-gateway -am install -DskipTests=true

FROM openjdk:11-jdk
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY --from=0 $PROJECT_DIR/facade-gateway/target/facade-gateway* $PROJECT_DIR/
EXPOSE 8080
ENTRYPOINT ["java"]
CMD ["-jar", "/opt/project/facade-gateway.jar"]