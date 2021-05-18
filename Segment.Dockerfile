FROM maven:3.6.3-jdk-11
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY ./ $PROJECT_DIR
RUN mvn --projects DomainModel,SegmentFinder -am install -DskipTests=true

FROM openjdk:11-jdk
ENV PROJECT_DIR=/opt/project
RUN mkdir -p $PROJECT_DIR
WORKDIR $PROJECT_DIR
COPY --from=0 $PROJECT_DIR/SegmentFinder/target/SegmentFinder* $PROJECT_DIR/
EXPOSE 8087
ENTRYPOINT ["java"]
CMD ["-jar", "-Dspring.profiles.active=node1", "/opt/project/SegmentFinder-1.0-SNAPSHOT.jar"]