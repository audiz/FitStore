FROM maven:3.6.3-jdk-11
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY ./pom.xml $PROJECT_DIR
RUN mvn dependency:resolve
COPY ./src/ $PROJECT_DIR/src
RUN mvn install

FROM openjdk:11-jdk
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY --from=0 $PROJECT_DIR/target/service-discovery-server* $PROJECT_DIR/
EXPOSE 8761
ENTRYPOINT ["java"]
CMD ["-jar", "/opt/project/service-discovery-server.jar"]