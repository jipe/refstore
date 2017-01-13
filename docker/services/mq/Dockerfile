FROM debian:jessie

RUN apt-get -q update && \
    apt-get install -yq --no-install-recommends wget ca-certificates python

RUN echo 'deb http://www.rabbitmq.com/debian/ testing main' > /etc/apt/sources.list.d/rabbitmq.list && \
    wget -O- https://www.rabbitmq.com/rabbitmq-release-signing-key.asc | apt-key add -

RUN apt-get -q update && \
    apt-get install -yq --no-install-recommends rabbitmq-server

ENV TINI_VERSION v0.13.2
ADD https://github.com/krallin/tini/releases/download/${TINI_VERSION}/tini /tini
RUN chmod +x /tini

EXPOSE 5672 15672

COPY enabled_plugins /etc/rabbitmq/enabled_plugins
COPY configure_rabbitmq.sh /root/configure_rabbitmq.sh

RUN /bin/bash -c /root/configure_rabbitmq.sh

COPY rabbitmq.config /etc/rabbitmq/rabbitmq.config

ENTRYPOINT ["/tini", "--", "rabbitmq-server"]
