FROM debian:stretch
MAINTAINER docker@defiant.dk

RUN apt-get -q update && \
    apt-get -yq --no-install-recommends install \
            default-jre-headless \
            tomcat8

ENV CATALINA_HOME /usr/share/tomcat8
ENV CATALINA_BASE /var/lib/tomcat8
ENV CATALINA_TMPDIR /tmp

EXPOSE 8080

COPY docker/lib/postgresql-9.4.1208.jar $CATALINA_HOME/lib/
COPY docker/controller/context.xml $CATALINA_BASE/conf/Catalina/localhost/ROOT.xml
COPY target/refstore /refstore

RUN chown -R tomcat8:tomcat8 /refstore

USER tomcat8

ENTRYPOINT exec $CATALINA_HOME/bin/catalina.sh run
