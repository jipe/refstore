# RefStore [![Build Status](https://travis-ci.org/jipe/refstore.svg?branch=master)](https://travis-ci.org/jipe/refstore) [![Coverage Status](https://coveralls.io/repos/github/jipe/refstore/badge.svg?branch=master)](https://coveralls.io/github/jipe/refstore?branch=master)
A web application for harvesting, processing and indexing publication references.

This is currently a work in progress.

## Up and running in no time with Docker

Make sure you have Docker 1.12+ installed, and that you're in the `docker` group.

Before you can do anything Maven-wise, you need to build the `mvn-runner`. This is
a Docker container, that can compile, test and package the application without you
even needing to have Java or Maven installed on your local machine. In fact it's
a direct link to the Maven command inside the Docker container, so you can invoke
any Maven build you like.

    $ ./scripts/build.sh

builds the Docker image for `mvn-runner`. This image is used by a lot of the other
scripts, and you can't really get off the ground without this.

Now you can run the application unit and integration tests either by

    $ ./test.sh

which is really just a small convenience script doing

    $ ./scripts/mvn.sh verify

Please note that if you do a `./scripts/mvn.sh install` which is normal when using Maven,
the Maven artifact will not be installed in your `$USER/.m2/repository` but rather in a 
Docker managed named volume, that normally resides in `/var/lib/docker/volumes/maven-repo`
and you need root permissions to get at that location.

The Maven `verify` goal also packages the application WAR file and a directory containing
the exploded WAR file. These are both available in the `target` directory subsequently.
