output=$(grep -Fqo "$PLAY_HOME" .bashrc)
# env vars
if [ ${#output} -eq 0 ]; then
    echo "# intelligeatsa env vars" >> .bashrc
    echo "export JAVA_HOME=/opt/java/jdk1.8.0_72/" >> .bashrc
    echo "export PLAY_HOME=\$HOME/activator-1.3.7-minimal" >> .bashrc
    echo "export PATH=\$PLAY_HOME:\$PATH" >> .bashrc
fi
output=$(grep -Fqo "$PIO_HOME" .bashrc)
if [ ${#ouptut} -eq 0 ]; then
    echo "export PIO_HOME=\$HOME/PredictionIO-0.9.5" >> .bashrc
fi

# link /vagrant to ~/app
if [ ! -L app ]; then
    ln -fs /vagrant app
fi
