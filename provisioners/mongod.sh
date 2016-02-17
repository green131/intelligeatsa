# mongodb
if [ ! -e "/etc/mongod.conf" ]; then
  echo "Installing mongo..."
  apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
  echo "deb http://repo.mongodb.org/apt/ubuntu precise/mongodb-org/3.2 multiverse" | tee /etc/apt/sources.list.d/mongodb-org-3.2.list
  apt-get update
  apt-get install -y mongodb-org
  echo "Installed mongo"
fi

# setup user
setupDb=$(mongo --quiet --eval "print(db.getMongo().getDBNames().indexOf('intelligeatsa') != -1);")
if [ "$setupDb" = "false" ]; then
  echo "setting up db..."
  mongo intelligeatsa --eval "db.createUser({\"user\": \"intelligeatsaUser\", \"pwd\": \"12345678\", \"roles\": [\"readWrite\"]});"
  wget "https://gist.githubusercontent.com/mgottein/9daea9a81864f50ba360/raw/9f5d97446880d8fee2004f36f855b56c3574196c/recipes.json" -nv
  mongoimport --db intelligeatsa --collection recipes --drop --file recipes.json
  rm recipes.json 
  echo "set up db"
fi
