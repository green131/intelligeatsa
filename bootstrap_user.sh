#!/usr/bin/env bash
set -e
cd ~
# play framework
if [ ! -d "$HOME/activator-1.3.7-minimal" ]; then
    wget http://downloads.typesafe.com/typesafe-activator/1.3.7/typesafe-activator-1.3.7-minimal.zip
    unzip typesafe-activator-1.3.7-minimal.zip
    rm typesafe-activator-1.3.7-minimal.zip
fi

output=$(grep -Fqo "$PLAY_HOME" .bashrc)
# env vars
if [ ${#output} -eq 0 ]; then
    echo "# intelligeatsa env vars" >> .bashrc
    echo "export JAVA_HOME=/opt/java/jdk1.8.0_72/" >> .bashrc
    echo "export PLAY_HOME=\$HOME/activator-1.3.7-minimal" >> .bashrc
    echo "export PATH=\$PLAY_HOME:\$PATH" >> .bashrc
fi

# link /vagrant to ~/app
if [ ! -L app ]; then
    ln -fs /vagrant app
fi
