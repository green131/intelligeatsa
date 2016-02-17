set -e
cd ~
# main binary
if [ ! -d "PredictionIO-0.9.5" ]; then
  if [ ! -f "PredicitionIO-0.9.5.tar.gz" ]; then
    echo "Getting prediction IO"
    wget https://d8k1yxp8elc6b.cloudfront.net/PredictionIO-0.9.5.tar.gz -nv
  fi
  tar zxf PredictionIO-0.9.5.tar.gz
fi
echo "Got PredictionIO"
if [ ! -d "PredictionIO-0.9.5/vendors" ]; then
  mkdir PredictionIO-0.9.5/vendors
fi
# apache spark
if [ ! -d "PredictionIO-0.9.5/vendors/spark-1.5.1-bin-hadoop2.6" ]; then
  if [ ! -f "spark-1.5.1-bin-hadoop2.6.tgz" ]; then
    echo "Getting apache spark"
    wget http://d3kbcqa49mib13.cloudfront.net/spark-1.5.1-bin-hadoop2.6.tgz -nv
  fi
  tar zxfC spark-1.5.1-bin-hadoop2.6.tgz PredictionIO-0.9.5/vendors
  rm spark-1.5.1-bin-hadoop2.6.tgz  
fi
echo "Got Apache Spark"
# elasticsearch
if [ ! -d "PredictionIO-0.9.5/vendors/elasticsearch-1.4.4" ]; then
  if [ ! -f "elasticsearch-1.4.4.tar.gz" ]; then
    echo "Getting elastic search"
    wget https://download.elasticsearch.org/elasticsearch/elasticsearch/elasticsearch-1.4.4.tar.gz -nv
  fi
  tar zxfC elasticsearch-1.4.4.tar.gz PredictionIO-0.9.5/vendors
  rm elasticsearch-1.4.4.tar.gz
fi
echo "Got ElasticSearch"
# hbase
if [ ! -d "PredictionIO-0.9.5/vendors/hbase-1.0.0" ]; then
  if [ ! -f "hbase-1.0.0-bin.tar.gz" ]; then
    echo "Getting hbase"
    wget http://archive.apache.org/dist/hbase/hbase-1.0.0/hbase-1.0.0-bin.tar.gz -nv
  fi
  tar zxfC hbase-1.0.0-bin.tar.gz PredictionIO-0.9.5/vendors
  rm hbase-1.0.0-bin.tar.gz
fi
echo "Got HBase. Configuring..."
cat << EOF > PredictionIO-0.9.5/vendors/hbase-1.0.0/conf/hbase-site.xml
<configuration>
  <property>
    <name>hbase.rootdir</name>
    <value>file://$HOME/PredictionIO-0.9.5/vendors/hbase-1.0.0/data</value>
  </property>
  <property>
    <name>hbase.zookeeper.property.dataDir</name>
    <value>$HOME/PredictionIO-0.9.5/vendors/hbase-1.0.0/zookeeper</value>
  </property>
</configuration>
EOF
cat << EOF > PredictionIO-0.9.5/vendors/hbase-1.0.0/conf/hbase-env.sh
export JAVA_HOME=/opt/java/jdk1.8.0_72
EOF
echo "HBase config done"
echo "Configuring Prediction IO..."
cat << EOF > PredictionIO-0.9.5/conf/pio-env.sh
PIO_STORAGE_SOURCES_HBASE_HOME=\$HOME/PredictionIO-0.9.5/vendors/hbase-1.0.0
SPARK_HOME=\$HOME/PredictionIO-0.9.5/vendors/spark-1.5.1-bin-hadoop2.6
PIO_STORAGE_SOURCES_ELASTICSEARCH_HOME=\$HOME/PredictionIO-0.9.5/vendors/elasticsearch-1.4.4
PIO_STORAGE_SOURCES_ELASTICSEARCH_TYPE=elasticsearch
PIO_STORAGE_SOURCES_ELASTICSEARCH_HOSTS=localhost
PIO_STORAGE_SOURCES_ELASTICSEARCH_PORTS=9300
EOF
echo "Prediction IO config done"
