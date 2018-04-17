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

package it.luigibennardis.iot.ml;

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
import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
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
 



public final class JavaDirectKafkaKmeansApplyModel {
  
	//private static final Pattern SPACE = Pattern.compile(" ");

  public static void main(String[] args) throws Exception {
    
	  /*
	  if (args.length < 2) {
      System.err.println("Usage: JavaDirectKafkaWordCount <brokers> <topics>\n" +
          "  <brokers> is a list of one or more Kafka brokers\n" +
          "  <topics> is a list of one or more kafka topics to consume from\n\n");
      System.exit(1);
    }*/

	//todo INSERIRE LETTURA DA FILE  
    //StreamingExamples.setStreamingLogLevels();

    String brokers = "localhost:9092"; // args[0];
    String topics = "test"; // args[1];

    // Create context with a 5 seconds batch interval
    SparkConf sparkConf = new SparkConf().setAppName("JavaDirectKafkaCassandraPrepare4Kmeans");
    
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
             
			//CustomDateAdapter sqlAdapter = new CustomDateAdapter();
	        
			/*
			 * 
			 */
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


	 */
    
    String modelFile = "/bennar11/input/model";
 	
	 
	//LOAD MODEL
	KMeansModel sameModel = KMeansModel.load(modelFile);
	 
		
	
    nonFilteredIotDataStream.foreachRDD(rdd ->
    {
    	 
    	 
    	System.out.println("+++++++++++++++"); 
    	System.out.println("+++++++++++++++ NUMERO DI RDD:    " + rdd.count()); 
    	System.out.println("+++++++++++++++"); 
    	 
    	
    	 
    	 rdd.foreachPartition(
 				items -> {
 					while (items.hasNext()) {
 						
 						IoTDataNoDate appo = new IoTDataNoDate();
 						appo = items.next();
 						
 						System.out.println("-->" + appo.getLatitude() + " -- " +  appo.getLongitude());
 						
 						 						
 						
 						System.out.println("prediction: " + 
 								sameModel.predict(
 								Vectors.dense(
 										appo.getLatitude(), 
 										appo.getLongitude()
 				    			))); 
 						 		
 					}
 					System.out.println(" ");
 					System.out.println("++++END RDD FETCH++++++");
 					System.out.println("+++++++++++++++++++++++");
 					
 				}); 
        
        
    	
     	
       	 
    	    	
    });
      
    // Start the computation
    jssc.start();
    jssc.awaitTermination();
  }
  
  
 
}
