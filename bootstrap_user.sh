#!/usr/bin/env bash
set -e
cd ~
# play framework
if [ ! -d "$HOME/activator-1.3.7-minimal" ]; then
    wget http://downloads.typesafe.com/typesafe-activator/1.3.7/typesafe-activator-1.3.7-minimal.zip
    unzip typesafe-activator-1.3.7-minimal.zip
    rm typesafe-activator-1.3.7-minimal.zip
fi

# env vars
if ! grep -Fq "$PLAY_HOME" .bashrc ; then
    echo -e "# intelligeatsa env vars\nexport JAVA_HOME=/opt/java/jdk1.8.0_72/\nexport PLAY_HOME=\$HOME/activator-1.3.7-minimal\nexport PATH=\$PLAY_HOME:\$PATH\n" >> .bashrc
fi

# link /vagrant to ~/app
if [ ! -L app ]; then
    ln -fs /vagrant app
fi
