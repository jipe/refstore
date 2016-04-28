FROM debian:jessie
MAINTAINER docker@defiant.dk

RUN apt-get -q update && \
    apt-get -yq --no-install-recommends install postgresql-9.4

USER postgres

RUN pg_ctlcluster 9.4 main start && \
    createuser --createdb metastore && \
    echo "alter role metastore with password 'metastore'" | psql && \
    export PGHOST=localhost PGUSER=metastore PGPASSWORD=metastore && \
    createdb metastore-jobstore && \
    createdb metastore-recordstore-shard1 && \
    createdb metastore-recordstore-shard2 && \
    unset PGHOST PGUSER PGPASSWORD && \
    pg_ctlcluster 9.4 main stop && \
    echo "host all metastore 0.0.0.0/0 md5" >> /etc/postgresql/9.4/main/pg_hba.conf && \
    echo "listen_addresses = '*'" >> /etc/postgresql/9.4/main/postgresql.conf

VOLUME /var/lib/postgresql

EXPOSE 5432

CMD exec pg_ctlcluster --foreground 9.4 main start
