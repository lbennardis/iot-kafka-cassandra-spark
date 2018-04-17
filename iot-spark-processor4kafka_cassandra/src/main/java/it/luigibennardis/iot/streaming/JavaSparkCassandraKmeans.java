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

import static com.datastax.spark.connector.japi.CassandraStreamingJavaUtil.javaFunctions;

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
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.ml.feature.VectorAssembler;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
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

import com.datastax.spark.connector.japi.CassandraJavaUtil;
import com.google.gson.*;
import com.google.gson.internal.bind.SqlDateTypeAdapter;

import it.luigibennardis.iot.streaming.entity.TrafficoVeicolareTotale;
import it.luigibennardis.iot.streaming.util.CustomDateAdapter;
import it.luigibennardis.iot.streaming.util.ImprovedDateAdapter;
import it.luigibennardis.iot.streaming.vo.IoTData;
import it.luigibennardis.iot.streaming.vo.IoTDataNoDate;
import it.luigibennardis.iot.streaming.vo.POIData;

import org.apache.spark.api.java.function.Function;




import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;



import java.util.ArrayList;
import java.util.List;

import org.apache.spark.api.java.function.ForeachFunction;



import org.apache.spark.ml.clustering.KMeansModel;
import org.apache.spark.ml.clustering.KMeans;
import org.apache.spark.ml.linalg.Vector;



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

public final class JavaSparkCassandraKmeans {
  private static final Pattern SPACE = Pattern.compile(" ");

  public static void main(String[] args) throws Exception {
    
	  /*
	  if (args.length < 2) {
      System.err.println("Usage: JavaDirectKafkaWordCount <brokers> <topics>\n" +
          "  <brokers> is a list of one or more Kafka brokers\n" +
          "  <topics> is a list of one or more kafka topics to consume from\n\n");
      System.exit(1);
    }*/

    //StreamingExamples.setStreamingLogLevels();

    String brokers = "localhost:9092"; // args[0];
    String topics = "test"; // args[1];

    // Create context with a 5 seconds batch interval
    SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaWordCount");
    
    //SparkConf conf = new SparkConf()
	//		 .setAppName(prop.getProperty("com.iot.app.spark.app.name"))
	//		 .setMaster(prop.getProperty("com.iot.app.spark.master"))
			 
	 sparkConf.set("spark.cassandra.connection.host", "localhost");
	 sparkConf.set("spark.cassandra.connection.port", "9042");
	 sparkConf.set("spark.cassandra.connection.keep_alive_ms","10000");		 
	     
    
	 
	 
	  
	 
	 
	 
	JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, Durations.seconds(5));
	jssc.checkpoint("file:///D:/checkpointSpark");
	 
	  
	
	
    Set<String> topicsSet = new HashSet<>(Arrays.asList(topics.split(",")));
    Map<String, Object> kafkaParams = new HashMap<>();
    
	//kafkaParams.put("metadata.broker.list", brokers);
	kafkaParams.put("bootstrap.servers", brokers);
	
	kafkaParams.put("group.id", "test");
	//props.put("enable.auto.commit", "true");
	//props.put("auto.commit.interval.ms", "1000");
	//props.put("session.timeout.ms", "30000");
	kafkaParams.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");  
	kafkaParams.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
	//props.put("partition.assignment.strategy", "range");
	
	
	
		
    // Create direct kafka stream with brokers and topics
    JavaInputDStream<ConsumerRecord<String, String>> messages = KafkaUtils.createDirectStream(
        jssc,
        LocationStrategies.PreferConsistent(),
        ConsumerStrategies.<String,String>Subscribe(topicsSet, kafkaParams));
      
	
	
	// Read value of each message from Kafka and return it
    JavaDStream<IoTDataNoDate> nonFilteredIotDataStream = messages.map(new Function<ConsumerRecord<String,String>, IoTDataNoDate>() {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
        public IoTDataNoDate call(ConsumerRecord<String, String> kafkaRecord) throws Exception {
             
			CustomDateAdapter sqlAdapter = new CustomDateAdapter();
	        
			/*
			Gson gson = new GsonBuilder()
	            .registerTypeAdapter(java.util.Date.class, sqlAdapter )
	            .setDateFormat("yyyy-MM-dd HH:mm:ss")
	            .create();
			*/
			
			Gson gson = new GsonBuilder().create();
		        
			
			IoTDataNoDate scheduledPeriod = gson.fromJson(kafkaRecord.value(), IoTDataNoDate.class);
			
			return scheduledPeriod;
			
        }
    });
    
       
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



root
 |-- fuelLevel: double (nullable = false)
 |-- latitude: string (nullable = true)
 |-- longitude: string (nullable = true)
 |-- routeId: string (nullable = true)
 |-- speed: double (nullable = false)
 |-- timestamp: struct (nullable = true)
 |    |-- date: integer (nullable = false)
 |    |-- day: integer (nullable = false)
 |    |-- hours: integer (nullable = false)
 |    |-- minutes: integer (nullable = false)
 |    |-- month: integer (nullable = false)
 |    |-- seconds: integer (nullable = false)
 |    |-- time: long (nullable = false)
 |    |-- timezoneOffset: integer (nullable = false)
 |    |-- year: integer (nullable = false)
 |-- vehicleId: string (nullable = true)
 |-- vehicleType: string (nullable = true)
 
 
	 */
    
    nonFilteredIotDataStream.foreachRDD(rdd ->
    {
    	System.out.println("+++++++++++++++"); 
    	System.out.println("+++++++++++++++ NUMERO DI RDD:    " + rdd.count()); 
    	System.out.println("+++++++++++++++"); 
    	
    	
    	
    	/*
    	// Generate the schema based on the string of schema
    	List<StructField> fields = new ArrayList<>();
    	
    	
    	StructField field = DataTypes.createStructField("fuelLevel", DataTypes.DoubleType, false);
    	fields.add(field);
    	
    	field = DataTypes.createStructField("latitude", DataTypes.StringType, true);
    	fields.add(field);
    	
    	field = DataTypes.createStructField("longitude", DataTypes.StringType, true);
    	fields.add(field);
    	
    	field = DataTypes.createStructField("routeId", DataTypes.StringType, true);
    	fields.add(field);
    	
    	field = DataTypes.createStructField("speed", DataTypes.DoubleType, false);
    	fields.add(field);
    	
    	field = DataTypes.createStructField("speed", DataTypes.DoubleType, false);
    	fields.add(field);
    	
    	   			
    	
    	StructType schema = DataTypes.createStructType(fields);

    	// Convert records of the RDD (people) to Rows
    	JavaRDD<Row> rowRDD = peopleRDD.map((Function<String, Row>) record -> {
    	  String[] attributes = record.split(",");
    	  return RowFactory.create(attributes[0], attributes[1].trim());
    	});

    	    	
    	// Apply the schema to the RDD
    	Dataset<Row> peopleDataFrame = spark.createDataFrame(rowRDD, schema);
    	 
    	
    	*/
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	
    	SparkSession session = SparkSession.builder().getOrCreate();
    	
    	Dataset<Row> dataSetData = session.createDataFrame(rdd, IoTDataNoDate.class);
    	
    	dataSetData.createOrReplaceTempView("TRAFFIC");
    	
    	Dataset<Row> trafficDF = session.sql("SELECT * FROM TRAFFIC ");
    	
    	
    	trafficDF.printSchema();
    	trafficDF.cache();
    	trafficDF.show();
    	
    	
    	VectorAssembler assembler1 = new VectorAssembler()
    	        .setInputCols(new String[]{"latitude", "longitude"})
    	        .setOutputCol("features");
    			
    	Dataset<Row> assembled1 = assembler1.transform(trafficDF);
    	
    	   	
    	assembled1.printSchema();
    	assembled1.cache();
    	assembled1.show();
    	
    	
    	Map<String, String> columnNameMappings = new HashMap<String, String>();
		columnNameMappings.put("routeId", "routeid");
		columnNameMappings.put("vehicleType", "vehicletype");
		columnNameMappings.put("timeStamp", "timestamp");
		columnNameMappings.put("vehicleId", "vehicleid");
		columnNameMappings.put("longitude", "longitude");
		columnNameMappings.put("latitude", "latitude");
		columnNameMappings.put("speed", "speed");
		columnNameMappings.put("fuelLevel", "fuellevel");
		
		    
    
		CassandraJavaUtil.javaFunctions(rdd).writerBuilder(
				"traffickeyspace", "event_store", CassandraJavaUtil.mapToRow(IoTDataNoDate.class, columnNameMappings
						)).saveToCassandra();
    	
    	
    	
    	
    	
    	
    	// Trains a k-means model.
    	KMeans kmeans = new KMeans().setK(4).setSeed(1L);
    	KMeansModel model = kmeans.fit(assembled1);
    	
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
    	
    	
    	
    	/*
    	Dataset<Row> trafficDFCol = dataSetData.select(
    			dataSetData.col("timestamp.date"),
    			dataSetData.col("timestamp.day"),
    			dataSetData.col("timestamp.hours"),
    			dataSetData.col("timestamp.minutes"),
    			dataSetData.col("timestamp.month"),
    			dataSetData.col("timestamp.seconds"),
    			dataSetData.col("timestamp.time"),
    			dataSetData.col("timestamp.timezoneOffset"),
    			dataSetData.col("timestamp.year")    			
    			);
    	
    	
    	Dataset<Row> trafficDFCol = dataSetData.select(
    			dataSetData.col("fuelLevel"),
    			dataSetData.col("latitude")   			
    			);
    	
    	*/
    	
    	//trafficDFCol.printSchema();
    	//trafficDFCol.cache();
    	  
    	
    	//trafficDFCol.foreach((ForeachFunction<Row>) row -> System.out.println(row));
    	//trafficDFCol.foreach((ForeachFunction<Row>) row -> System.out.println("  #####    ######   "));
    	
    	
    	
    	//trafficDFCol.show();
    	
    	
    	//Dataset<Row> trafficDF = session.sql("SELECT * FROM TRAFFIC ");
    	
    	
    	// val eventJDf = df.select(dataSetData.col("timestamp.date"), df.col("eventJ.code"), df.col("eventJ.process_id"), df.col("eventJ.system_id") /*...*/)
    	
    	
    	//trafficDF.printSchema();
    	
    	/*
    	 
    	root
    	 |-- fuelLevel: double (nullable = false)
    	 |-- latitude: string (nullable = true)
    	 |-- longitude: string (nullable = true)
    	 |-- routeId: string (nullable = true)
    	 |-- speed: double (nullable = false)
    	 |-- timestamp: struct (nullable = true)
    	 |    |-- date: integer (nullable = false)
    	 |    |-- day: integer (nullable = false)
    	 |    |-- hours: integer (nullable = false)
    	 |    |-- minutes: integer (nullable = false)
    	 |    |-- month: integer (nullable = false)
    	 |    |-- seconds: integer (nullable = false)
    	 |    |-- time: long (nullable = false)
    	 |    |-- timezoneOffset: integer (nullable = false)
    	 |    |-- year: integer (nullable = false)
    	 |-- vehicleId: string (nullable = true)
    	 |-- vehicleType: string (nullable = true)
    	 
    	 */
    	 
    	 
    	
    	// The columns of a row in the result can be accessed by field index
    	Encoder<String> stringEncoder = Encoders.STRING();
    	
    	//Dataset<String> trafficSelDF = trafficDF.map(
    	//  (MapFunction<Row, String>) row -> "Name: " + row.getString(0),
    	//    stringEncoder);
    	
    	
    	
    	//trafficSelDF.printSchema();
    	//trafficSelDF.cache();
    	//trafficSelDF.show();
    			
    	/*
    	 
    	 

    	
    	teenagerNamesByIndexDF.show();
    	*/
    	
    	
    	
    });
    
    	 
	 //poi data
	 /*
	 POIData poiData = new POIData();
	 poiData.setLatitude(33.877495);
	 poiData.setLongitude(-95.50238);
	 poiData.setRadius(30);//30 km
	 */
	 
	 
	 
	 //broadcast variables. We will monitor vehicles on Route 37 which are of type Truck
	 /*
	  * Broadcast<Tuple3<POIData, String, String>> broadcastPOIValues = jssc.sparkContext().broadcast(new Tuple3<>(poiData,"Route-37","Truck"));
	  */
	 //call method  to process stream
	 /*
	  * iotTrafficProcessor.processPOIData(nonFilteredIotDataStream,broadcastPOIValues);
	  */
	 
	 
    // Start the computation
    jssc.start();
    jssc.awaitTermination();
  }
  
  
  
  
  
  //Funtion to check processed vehicles.
 	private static final Function3<String, Optional<IoTData>, State<Boolean>, Tuple2<IoTData,Boolean>> 
 		processedVehicleFunc = (String, iot, state) -> {
 			Tuple2<IoTData,Boolean> vehicle = new Tuple2<>(iot.get(),false);
 			if(state.exists()){
 				vehicle = new Tuple2<>(iot.get(),true);
 			}else{
 				state.update(Boolean.TRUE);
 			}			
 			return vehicle;
 		};
 		
 		 
}
