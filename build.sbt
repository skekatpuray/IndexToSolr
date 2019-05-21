name := "SolrClient"

version := "0.1"

scalaVersion := "2.12.8"

resolvers += "Restlet Repositories" at "http://maven.restlet.org"

libraryDependencies += "org.apache.spark" %% "spark-core" % "2.3.1"
libraryDependencies += "com.lucidworks.spark" % "spark-solr" % "3.5.8"