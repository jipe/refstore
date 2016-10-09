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

COPY docker/lib/postgresql-9.4.1208.jar /usr/share/tomcat8/lib/
COPY docker/controller/context.xml /var/lib/tomcat8/conf/Catalina/localhost/ROOT.xml
COPY target/refstore-1.0 /refstore

RUN chown -R tomcat8:tomcat8 /refstore

USER tomcat8

ENTRYPOINT ["$CATALINA_HOME/bin/catalina.sh"]

CMD ["run"] 