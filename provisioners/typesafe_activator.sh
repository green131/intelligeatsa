cd ~
# play framework
if [ ! -d "$HOME/activator-1.3.7-minimal" ]; then
  echo "getting typesafe activator..."
  wget http://downloads.typesafe.com/typesafe-activator/1.3.7/typesafe-activator-1.3.7-minimal.zip -nv
  unzip typesafe-activator-1.3.7-minimal.zip
  rm typesafe-activator-1.3.7-minimal.zip
  echo "got typesafe activator"
fi
