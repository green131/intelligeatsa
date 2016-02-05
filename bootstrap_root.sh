#!/usr/bin/env bash
set -e
# updates, build essential, unzip
apt-get update
apt-get install -y build-essential
apt-get install -y unzip

# oracle java
if [ ! -d "/opt/java" ]; then
    mkdir /opt/java && cd /opt/java
    wget --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u72-b15/jdk-8u72-linux-x64.tar.gz
    tar -zxvf jdk-8u72-linux-x64.tar.gz
    rm jdk-8u72-linux-x64.tar.gz
    update-alternatives --install /usr/bin/java java /opt/java/jdk1.8.0_72/bin/java 100
    update-alternatives --install /usr/bin/javac javac /opt/java/jdk1.8.0_72/bin/javac 100
fi

# mongodb
if [ ! -e "/etc/mongod.conf" ]; then
    apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
    echo "deb http://repo.mongodb.org/apt/ubuntu precise/mongodb-org/3.2 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-3.2.list
    apt-get update
    apt-get install -y mongodb-org
fi
