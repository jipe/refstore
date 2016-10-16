# RefStore [![Build Status](https://travis-ci.org/jipe/refstore.svg?branch=master)](https://travis-ci.org/jipe/refstore) [![Coverage Status](https://coveralls.io/repos/github/jipe/refstore/badge.svg?branch=master)](https://coveralls.io/github/jipe/refstore?branch=master)
A web application for harvesting, processing and indexing publication references.

This is currently a work in progress.

## Building the WAR file

Build environment requirements:

* Java 8
* Maven 3

Build the RefStore WAR file with
    
    $ ./build.sh

This also runs the unit tests.

## Integration testing and running the application

Integration tests currently require Docker and Docker Compose in order to run,
as starting and stopping the containers is integrated into the Maven POM file.

### Using Docker Compose

Requirements:

* Docker 1.12
* Docker Compose 1.8

Then you can launch the Docker containers with

	$ ./run.sh
	
Then you can run the integration tests against the container setup with

	$ ./test.sh
	
### On local machine

* RabbitMQ 3
* PostgreSQL 9.4
* Tomcat 8

The default Tomcat context file (located at src/main/webapp/META-INF/context.xml) expects the following:

* RabbitMQ accessible with the URI "ampq://guest:guest@localhost:5672" (the default RabbitMQ location).
* PostgreSQL accessible with the URL "postgresql://refstore:refstore@localhost:5432".

It's also expected that the following databases are available to the refstore user:

* refstore.job-store
* refstore.configuration-store
* refstore.record-store-shard1

You can add more shards for the record store if you like just by adding another database increasing 
the shard number and creating a corresponding entry in the context file.