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
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;

 


import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.State;
import org.apache.spark.streaming.StateSpec;

 

//import com.google.common.base.Optional;
import org.apache.spark.api.java.Optional;

 
import com.google.gson.*;
import com.google.gson.internal.bind.SqlDateTypeAdapter;

import it.luigibennardis.iot.streaming.processor.IoTComputeDatiTraffico;
import it.luigibennardis.iot.streaming.util.CustomDateAdapter;
import it.luigibennardis.iot.streaming.util.ImprovedDateAdapter;
import it.luigibennardis.iot.streaming.vo.IoTData;
import it.luigibennardis.iot.streaming.vo.POIData;

import org.apache.spark.api.java.function.Function;



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

public final class JavaDirectKafkaWordCount {
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
    JavaDStream<IoTData> nonFilteredIotDataStream = messages.map(new Function<ConsumerRecord<String,String>, IoTData>() {
        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
        public IoTData call(ConsumerRecord<String, String> kafkaRecord) throws Exception {
            
			
			// Creates the json object which will manage the information received 
			// GsonBuilder builder = new GsonBuilder(); 

			// Register an adapter to manage the date types as long values 
			// builder.registerTypeAdapter(Date.class, new ImprovedDateAdapter());

			// Gson gson = builder.create();
			
			/*
			Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                        throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            }).create();
			*/
			
			
			
			
			
			
			 
			CustomDateAdapter sqlAdapter = new CustomDateAdapter();
	        Gson gson = new GsonBuilder()
	            .registerTypeAdapter(java.util.Date.class, sqlAdapter )
	            .setDateFormat("yyyy-MM-dd HH:mm:ss")
	            .create();
	        
	        
			
			IoTData scheduledPeriod = gson.fromJson(kafkaRecord.value(), IoTData.class);
			
			
			return scheduledPeriod;
			
				
			
			
        }
    });
    
       
	 	
    
	//JavaDStream<StratioStreamingMessage> parsedDataDstream = messages.map(new SerializerFunction());

	
    // Map incoming JSON
    
	/* 
	messages.foreachRDD(rdd ->
    {
    	System.out.println("+++++++++++++++"); 
    	System.out.println("+++++++++++++++ NUMERO DI RDD:    " + rdd.count()); 
    	System.out.println("+++++++++++++++"); 
    	
    	
    	rdd.foreachPartition(
				items -> {
					while (items.hasNext()) {
						System.out.println(items.next() + System.lineSeparator());
					}
				
				}); 
    });
    */    
   
    //We need non filtered stream for poi traffic data calculation
    //JavaDStream<IoTData> nonFilteredIotDataStream = messages.map(ConsumerRecord::value);
	    
    //We need filtered stream for total and traffic data calculation, the filter is on iot.getVehicleId()
    JavaPairDStream<String,IoTData> iotDataPairStream = 
    			nonFilteredIotDataStream.mapToPair(
    					iot -> 
    						new Tuple2<String,IoTData>
    						(iot.getIdVeicolo(),iot)).reduceByKey((a, b) -> a );
       
    
    // Check vehicle Id is already processed
	JavaMapWithStateDStream<String, IoTData, Boolean, Tuple2<IoTData,Boolean>> iotDStreamWithStatePairs = 
			iotDataPairStream.mapWithState(StateSpec.function(processedVehicleFunc).timeout(Durations.seconds(3600)));
	
	 
	
	 // Filter processed vehicle ids and keep un-processed
	JavaDStream<Tuple2<IoTData,Boolean>> filteredIotDStreams = iotDStreamWithStatePairs.map(tuple2 -> tuple2)
						.filter(tuple -> tuple._2.equals(Boolean.FALSE));

    
	// Get stream of IoTdata
	 JavaDStream<IoTData> filteredIotDataStream = filteredIotDStreams.map(tuple -> tuple._1);
	 
	 //cache stream as it is used in total and window based computation
	 filteredIotDataStream.cache();
     	
	 
	//process data
	 IoTComputeDatiTraffico iotTrafficProcessor = new IoTComputeDatiTraffico();
	 iotTrafficProcessor.computeDatiTrafficoTotali(filteredIotDataStream);
	 iotTrafficProcessor.computeDatiTrafficoFinestraTemporale(filteredIotDataStream);

	 //poi data
	 POIData poiData = new POIData();
	 poiData.setLatitude(33.877495);
	 poiData.setLongitude(-95.50238);
	 poiData.setRadius(30);//30 km
	 
	 //broadcast variables. We will monitor vehicles on Route 37 which are of type Truck
	 Broadcast<Tuple3<POIData, String, String>> broadcastPOIValues = jssc.sparkContext().broadcast(new Tuple3<>(poiData,"Route-37","Truck"));
	 //call method  to process stream
	 iotTrafficProcessor.processPOIData(nonFilteredIotDataStream,broadcastPOIValues);
	 
	 
    // Start the computation
    jssc.start();
    jssc.awaitTermination();
  }
  
  
  
  
  
  //***FUNZIONE CHE CONTROLLA EVENTUALI VEICOLI GIA' PROCESSATI 
  
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
