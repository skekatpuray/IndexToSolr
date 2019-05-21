# IngestSolr
Spark code to index dataset into HDP Search (Solr/Kerberized)

##### Script to execute

```sh
spark-submit \
--class SolrClientMain
--jars spark-solr-3.2.2-shaded.jar \
--files /home/someUserId/someUserId.keytab,/home/someUserId/Solr_Client.jaas \
--driver-java-options "-Djava.security.auth.login.config=/home/someUserId/Solr_Client.jaas" \
--conf "spark.executor.extraJavaOptions=-Djava.security.auth.login.config=/home/someUserId/Solr_Client.jaas"
--principal someUserId@EXAMPLE.DOMAIN.CORP \
--keytab /home/someUserId/copyOfSomeUserId.keytab
--solrclient_2.12-0.1.jar \
hdfs://DEV-CLUSTER/user/someUserId/yellowCab.csv COLLECTION_NAME zk1:2181,zk2:2181,zk3:2181/solr
```

##### Solr_Client.jaas
```sh
Client{
  com.sun.security.auth.module.Krb5LoginModule required
  useKeyTab=true
  keyTab=/home/someUserId/someUserId.keytab
  storeKey=true
  useTicketCache=false
  debug=true
  principal="someUserId@EXAMPLE.DOMAIN.CORP";
};
```

[Dataset link](https://github.com/lucidworks/spark-solr/blob/master/src/test/resources/test-data/nyc_yellow_taxi_sample_1k.csv)
