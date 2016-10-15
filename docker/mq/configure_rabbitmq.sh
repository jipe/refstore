#!/bin/sh
server='rabbitmq-server'
ctl='rabbitmqctl'
admin='/usr/bin/rabbitmqadmin'
admin_src='http://guest:guest@localhost:15672/cli/rabbitmqadmin'

echo '*** Starting detached RabbitMQ server for configuring ***'
$server -detached

until $(wget -qO $admin $admin_src); do sleep 1; done
chmod 755 $admin

echo '*** Creating users ***'
$admin declare user name=refstore password=refstore tags=administrator

echo '*** Setting virtual host permissions ***'
$admin declare permission vhost=/ user=refstore configure='.*' write='.*' read='.*'

$admin export /etc/rabbitmq/definitions.json

echo 'Stopping detached RabbitMQ server.'
$ctl stop
