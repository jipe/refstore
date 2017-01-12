FROM maven:3-jdk-8

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get -qq update

RUN apt-get -yqq install --no-install-recommends tzdata locales && \
    echo 'Europe/Copenhagen' > /etc/timezone && \
    dpkg-reconfigure tzdata && \
    echo 'en_DK.UTF-8 UTF-8' >> /etc/locale.gen && \
    dpkg-reconfigure locales && \
    update-locale LC_ALL=en_DK.UTF-8 && \
    locale-gen

ENV LC_ALL en_DK.UTF-8
ENV LANG en_DK.UTF-8

COPY docker/mvn/entrypoint.sh /

ENTRYPOINT ["/entrypoint.sh"]
CMD ["clean", "verify"]
