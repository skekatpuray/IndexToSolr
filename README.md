# IngestSolr
Spark code to index dataset into Solr
Script to execute

```sh
spark-submit \
--class SolrClientMain
--jars spark-solr-3.2.2-shaded.jar \
--files /home/someUserId/someUserId.keytab,/home/someUserId/Solr_Client.jaas \
--driver-java-options "-Djava.security.auth.login.config=/home/someUserId/Solr_Client.jaas" \
--conf "spark.executor.extraJavaOptions=-Djava.security.auth.login.config=/home/someUserId/Solr_Client.jaas"
--principal someUserId@EXAMPLE.DOMAIN.CORP \
--keytab /home/someUserId/copyOfSomeUserId.keytab
--myjar \
hdfs://DEV-CLUSTER/user/someUserId/yellowCab.csv COLLECTION_NAME zk1:2181,zk2:2181,zk3:2181/solr
```



[Dataset link](https://github.com/lucidworks/spark-solr/blob/master/src/test/resources/test-data/nyc_yellow_taxi_sample_1k.csv)
