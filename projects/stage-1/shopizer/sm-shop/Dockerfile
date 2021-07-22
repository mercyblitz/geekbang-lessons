FROM openjdk:8
COPY ./target/ROOT.war /usr/src/project/
WORKDIR /usr/src/project

FROM tomcat:9.0.10-jre8

RUN rm -rf /usr/local/tomcat/webapps/*
RUN mkdir -p /usr/local/tomcat/files

COPY --from=0 /usr/src/project/ROOT.war /usr/local/tomcat/webapps/ROOT.war

ENV JAVA_OPTS="-Xmx1024m"

CMD ["catalina.sh", "run"]

EXPOSE 8080