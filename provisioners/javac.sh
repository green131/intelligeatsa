# oracle java
if [ ! -d "/opt/java" ]; then
  echo "Getting java..."
  mkdir /opt/java && cd /opt/java
  wget --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u72-b15/jdk-8u72-linux-x64.tar.gz -nv
  tar -zxf jdk-8u72-linux-x64.tar.gz
  rm jdk-8u72-linux-x64.tar.gz
  update-alternatives --install /usr/bin/java java /opt/java/jdk1.8.0_72/bin/java 100
  update-alternatives --install /usr/bin/javac javac /opt/java/jdk1.8.0_72/bin/javac 100
  echo "Got java"
fi
