#!/usr/bin/env bash
cd /home/ec2-user/server
sudo java -jar e-commerce-0.0.1-SNAPSHOT.jar\
    *.jar > /dev/null 2> /dev/null < /dev/null &