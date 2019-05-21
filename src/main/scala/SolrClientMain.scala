import org.apache.spark.sql.SparkSession
import com.lucidworks.spark.util.SolrSupport
import org.apache.solr.common.SolrInputDocument
import org.apache.spark.sql.functions._

object SolrClientMain {

  def main(args : Array[String]) : Unit = {

    val CSV_HDFS_PATH = args(0)
    val COLLECTION = args(1)
    val ZKHOST = args(2)
    val BATCH_SIZE = 10

    val spark = SparkSession
      .builder()
      .appName("Solr ingester")
      .config("config.key.here", "")
      .enableHiveSupport()
      .getOrCreate()


    var csvDF = spark
      .read
      .format("com.databricks.spark.csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load(CSV_HDFS_PATH)

    // Filter out invalid lat/lon cols
    csvDF = csvDF.filter("pickup_latitude >= -90 AND pickup_latitude <= 90 AND pickup_longitude >= -180 AND pickup_longitude <= 180")
    csvDF = csvDF.filter("dropoff_latitude >= -90 AND dropoff_latitude <= 90 AND dropoff_longitude >= -180 AND dropoff_longitude <= 180")

    // concat the lat/lon cols into a single value expected by solr location fields
    csvDF = csvDF.withColumn("pickup", concat_ws(",", col("pickup_latitude"),col("pickup_longitude"))).drop("pickup_latitude").drop("pickup_longitude")
    csvDF = csvDF.withColumn("dropoff", concat_ws(",", col("dropoff_latitude"),col("dropoff_longitude"))).drop("dropoff_latitude").drop("dropoff_longitude")

    val csvDF1 = csvDF.withColumn("id", monotonically_increasing_id())

    //Select just a few columns for indexing
    val csvDF2 = csvDF1.select("id", "passenger_count", "fare_amount", "payment_type", "trip_distance")

    val someRdd = csvDF2.rdd

    val solrDocs = someRdd.map ( item => {
      val solrDoc = new SolrInputDocument
      solrDoc.setField("id", item(0).asInstanceOf[Number].longValue.toInt)
      solrDoc.setField("PassengerCount", item(1).asInstanceOf[Number].longValue.toInt)
      solrDoc.setField("FareAmount", item(2).asInstanceOf[Double].toDouble)
      solrDoc.setField("PaymentType", item(3).asInstanceOf[Number].longValue.toInt)
      solrDoc.setField("TripDistance", item(4).asInstanceOf[Double].toDouble)
      solrDoc
    })

    SolrSupport.indexDocs(ZKHOST, COLLECTION, BATCH_SIZE, solrDocs)

    val solrServer = SolrSupport.getCachedCloudClient(ZKHOST)

    solrServer.setDefaultCollection(COLLECTION)

    solrServer.commit(false, false)
  }
}
