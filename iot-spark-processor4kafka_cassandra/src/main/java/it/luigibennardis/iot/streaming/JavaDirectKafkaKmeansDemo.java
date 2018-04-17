/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.luigibennardis.iot.streaming;

import java.util.HashMap;
import java.util.HashSet;
import java.io.File;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

 
import scala.Tuple2;
import scala.Tuple3;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import org.apache.spark.SparkConf;
 
import org.apache.spark.api.java.function.Function3;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

 


import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
//import com.google.common.base.Optional;
import org.apache.spark.api.java.Optional;

import com.datastax.spark.connector.cql.CassandraConnector;
import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.google.gson.*;
import com.google.gson.internal.bind.SqlDateTypeAdapter;

import it.luigibennardis.iot.streaming.util.CustomDateAdapter;
import it.luigibennardis.iot.streaming.util.ImprovedDateAdapter;
import it.luigibennardis.iot.streaming.vo.IoTData;
import it.luigibennardis.iot.streaming.vo.IoTDataNoDate;
import it.luigibennardis.iot.streaming.vo.IoTDataNoDatePrediction;
import it.luigibennardis.iot.streaming.vo.POIData;

import org.apache.spark.api.java.function.Function;



import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.net.URI;





/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 * Usage: JavaDirectKafkaWordCount <brokers> <topics>
 *   <brokers> is a list of one or more Kafka brokers
 *   <topics> is a list of one or more kafka topics to consume from
 *
 * Example:
 *    $ bin/run-example streaming.JavaDirectKafkaWordCount broker1-host:port,broker2-host:port \
 *      topic1,topic2 
 *      
 *      org.apache.spark.examples.streaming.JavaDirectKafkaWordCount
 */

public final class JavaDirectKafkaKmeansDemo {
  private static final Pattern SPACE = Pattern.compile(" ");

  public static void main(String[] args) throws Exception {
    	  
	  
	    
	    
	    
	    SparkSession spark = SparkSession
	              .builder()
	              .appName("SparkCassandraApp")
	              .config("spark.cassandra.connection.host", "localhost")
	              .config("spark.cassandra.connection.port", "9042")
	              .master("local[*]")
	              .getOrCreate();

	
	    String table = "event_store";
	    String keyspace = "traffickeyspace";
	    
	    
	    Map<String, String> columnNameMappings = new HashMap<String, String>();
		columnNameMappings.put("table", table);
		columnNameMappings.put("keyspace", keyspace);
		
		
		//***LEGGE IN UN DATASET DO OGGETTI ROW I RECORD DELLA TABELLA 
		Dataset<Row> trafficDF = spark.read()
			.format("org.apache.spark.sql.cassandra")
			.options(columnNameMappings)
			.load();
		
		String[] nomeColonne = trafficDF.columns();
		
		
		System.out.println("****************************************");
     	
		
		for (int i =0; i< nomeColonne.length; i++)
		
		{
			System.out.println("");
			
			System.out.println("COLONNA : " + i + " :"+ nomeColonne[i] ); 
			System.out.println("");
		}
		
		System.out.println("****************************************");
     	
		trafficDF.show();    
	    
		//***PREPARA IL VECTOR CON LE FEATURE 
    	VectorAssembler assembler1 = new VectorAssembler()
    	        .setInputCols(new String[]{"latitude", "longitude"})
    	        .setOutputCol("features");
    			
    	Dataset<Row> assembled1 = assembler1.transform(trafficDF);
    	
    	String[] nomeColonneA = assembled1.columns();
		
		
		System.out.println("****************************************");
     	
		
		for (int i =0; i< nomeColonneA.length; i++)
		
		{
			System.out.println("");
			
			System.out.println("COLONNA : " + i + " :"+ nomeColonneA[i] ); 
			System.out.println("");
		}
		
		
		
    	assembled1.printSchema();
    	assembled1.cache();
    	assembled1.show();
    	
    	
    	Dataset<Row>[] splits = assembled1.randomSplit(new double[] {0.7, 0.3},5043L);
    	
    	Dataset<Row> train = splits[0];
    	
    	Dataset<Row> test = splits[1]; //30% of data
    	
    	
    	KMeans kmeans = new KMeans().setK(8).setFeaturesCol("features").setPredictionCol("prediction");
    	
    	KMeansModel model = kmeans.fit(assembled1);
    	
    	//PERSISTE IL MODELLO SUL filesystem
    	 String modelFile = "/bennar11/input/model";
    	 
    	model.write().overwrite().save(modelFile);
    	  
    	// model can be  re-loaded like this
        // val sameModel = KMeansModel.load("/bennar11/input/model")
    	
    	
    	//LOAD MODEL
    	KMeansModel sameModel = KMeansModel.load(modelFile);
    	
    	 
    	/*
    	 * value = {
			"vehicleId":"e3e14ba6-eb17-4742-9a12-7c264092d593",
			"vehicleType":"Bus",
			"routeId":"Route-43",
			"longitude":"-97.52017",
			"latitude":"35.476772",
			"timestamp":"2018-01-22 08:20:49",
			"speed":86.0,
			"fuelLevel":31.0}
    	 */
    	
    	
    	// Trains a k-means model.
    	//KMeans kmeans = new KMeans().setK(4).setSeed(1L);
    	//KMeansModel model = kmeans.fit(assembled1);
    	
    	double WSSSE = model.computeCost(assembled1);
    	System.out.println("");
    	System.out.println("");
    	System.out.println("");
     	System.out.println("");
     	System.out.println("****************************************");
     	System.out.println("****************************************");
     	
     	System.out.println("Within Set Sum of Squared Errors = " + WSSSE);

 	    
    	Vector[] centers = model.clusterCenters();
    	System.out.println("");
    	System.out.println("");
    	System.out.println("****************************************");
    	System.out.println("****************************************");
    	System.out.println("Cluster Centers: ");
    	for (Vector center: centers) {
    	  System.out.println(center);
    	}
    	System.out.println("****************************************");
    	System.out.println("****************************************");
    	System.out.println("");
    	System.out.println("");
    	System.out.println("");
    	System.out.println("");
    	    	
    	Dataset<Row> categ = model.transform(test);
    	
    	String[] nomeColonneB = assembled1.columns();
		
		
		System.out.println("********** nomeColonneB ****************");
     	
		
		for (int i =0; i< nomeColonneB.length; i++)
		
		{
			System.out.println("");
			
			System.out.println("COLONNA : " + i + " :"+ nomeColonneB[i] ); 
			System.out.println("");
		}
		
		System.out.println("********** categ.printSchema() ****************");
     	
		
		categ.drop("features").printSchema();
		categ.drop("features").show();
    	
    	/*
    	 * categ.coalesce(1).drop("features").write()
    	 * .format("com.databricks.spark.csv")
			.option("header","true")
			.save("/bennar11/input/output");
    	 */
			
		 
    	
    	/*
    	//persist rusults on cassandra
    	Map<String, String> columnNameMappingDbs = new HashMap<String, String>();
    	columnNameMappingDbs.put("routeId", "routeid");
    	columnNameMappingDbs.put("vehicleType", "vehicletype");
    	columnNameMappingDbs.put("timeStamp", "timestamp");
    	columnNameMappingDbs.put("vehicleId", "vehicleid");
    	columnNameMappingDbs.put("longitude", "longitude");
    	columnNameMappingDbs.put("latitude", "latitude");
    	columnNameMappingDbs.put("speed", "speed");
		columnNameMappingDbs.put("fuelLevel", "fuellevel");
		columnNameMappingDbs.put("prediction", "prediction");
		
		//JavaRDD<IoTDataNoDatePrediction> appo = categ.javaRDD();
		
		  CassandraJavaUtil.javaFunctions(categ.drop("features").toJavaRDD()).writerBuilder(
		 			"traffickeyspace", "event_store_prediction", CassandraJavaUtil.mapToRow(Row.class, columnNameMappingDbs
						)).saveToCassandra();
		 */
		  
    	System.out.println("****************************************");
    	System.out.println("****************************************");
    	System.out.println(""); 
    	System.out.println("");
    	System.out.println("GROUP BY prediction: ");
    	
    	categ.groupBy("prediction").count().show();
    	
    	System.out.println("");
    	System.out.println("");
    	System.out.println("****************************************");
    	System.out.println("****************************************");
    	
    	
    	//categ.groupBy("prediction","vehicletype").count().orderBy("prediction","vehicletype").show();
    	
    	
    	/*
****************************************
****************************************
+----------+-----------+-----+
|prediction|vehicletype|count|
+----------+-----------+-----+
|         0|        Bus|   26|
|         0|Large Truck|   28|
|         0|Private Car|   15|
|         0|Small Truck|   24|
|         0|       Taxi|   26|
|         1|        Bus|    8|
|         1|Large Truck|   16|
|         1|Private Car|    9|
|         1|Small Truck|    9|
|         1|       Taxi|   10|
|         2|        Bus|    6|
|         2|Large Truck|    7|
|         2|Private Car|    7|
|         2|Small Truck|    9|
|         2|       Taxi|    4|
|         3|        Bus|    1|
|         3|Large Truck|    5|
|         3|Private Car|    5|
|         3|Small Truck|   11|
|         3|       Taxi|    6|
+----------+-----------+-----+
                                                                                                                                                                                                                                                                                                                                                                                                      

*/
    	
    	categ.select("routeid","prediction","vehicletype").groupBy("prediction","vehicletype").count().orderBy("prediction","vehicletype").show();
    	
    	
    	
    	
    	//accede al file system hadoop e rinomina il file di output
    	FileSystem hdfs = FileSystem.get(new URI("hdfs://localhost:54310"),new Configuration());
        //Path newPath = new Path("/bennar11/input/output");
        
        String filePath = "/bennar11/input/output/";
    	
        Path output = new Path(filePath);
 

		// delete existing directory
		if (hdfs.exists(output)) {
		    hdfs.delete(output, true);
		}



        
    	
    	categ.select("routeid","prediction","vehicletype").groupBy("prediction","vehicletype").count().orderBy("prediction","vehicletype")
    		.coalesce(1).drop("features").write() //elimina il campo fatures 
   	  		.format("com.databricks.spark.csv")
			.option("header","true")
			.save("/bennar11/input/output");
    	
    	File file = new File("c:\\output.csv");
    	
    	if (file.exists()) {
    		  file.delete();
    	    	   	    	
    	}
    	
    	
        		
        //final FileStatus[] fileStatuses = hdfs.globStatus(newPath);
    	FileStatus[] filestatus = hdfs.globStatus(new Path(filePath+"part*"));
        
        String filename = filestatus[0].getPath().getName();
        
        hdfs.rename(new Path(filePath+filename), new Path(filePath+"output.csv"));
        
           	/*
    	 * 	[33.48512097471909,-95.47480714887632]
			[34.74013967716535,-96.69269777952752]
			[35.72858186868686,-97.22141613131315]
			[35.24546155056179,-97.26142048314607]
			[35.77705045652175,-97.75847139130434]
			[34.443628659574486,-96.17994275177308]
			[35.236996927083325,-97.76742644791668]
			[34.2293840638298,-96.7244704787234]


+----------+-----------+-----+
|prediction|vehicletype|count|
+----------+-----------+-----+
|         2|Small Truck|    6|
|         0|Large Truck|   27|
|         3|Small Truck|    4|
|         6|       Taxi|    7|
|         5|Small Truck|   10|
|         7|Small Truck|    4|
|         0|       Taxi|   29|
|         2|       Taxi|    1|
|         4|Small Truck|   13|
|         1|Private Car|    7|
|         6|Large Truck|    2|
|         5|        Bus|    7|
|         2|Private Car|    9|
|         4|        Bus|    3|
|         2|Large Truck|    8|
|         3|       Taxi|    4|
|         6|Small Truck|    3|
|         4|       Taxi|    7|
|         0|Small Truck|   28|
|         7|       Taxi|    2|
+----------+-----------+-----+

			+----------+-----+
			|prediction|count| NUMERO VEICOLI 
			+----------+-----+
			|         1|   84|
			|         6|   66|
			|         3|   67|
			|         5|  102|
			|         4|   55|
			|         7|   62|
			|         2|   74|
			|         0|  226|
			+----------+-----+

    	 */
	    spark.stop();

	    	 	
  }
  
  
   
 		
 		 
}
