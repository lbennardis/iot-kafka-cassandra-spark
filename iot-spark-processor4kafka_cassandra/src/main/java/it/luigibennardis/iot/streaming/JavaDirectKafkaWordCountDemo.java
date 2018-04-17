package it.luigibennardis.iot.streaming;

//import com.datastax.spark.connector.japi.CassandraStreamingJavaUtil;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
//import org.apache.spark.streaming.kafka.KafkaUtils;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//import static com.datastax.spark.connector.japi.CassandraJavaUtil.mapToRow;

/**
 * Created by jonas on 10/10/16.
 */
public class JavaDirectKafkaWordCountDemo implements Serializable{
        public static void main(String[] args) throws Exception{
        SparkConf conf = new SparkConf(true)
                .setAppName("TwitterToCassandra")
                .setMaster("local[*]")
                .set("spark.cassandra.connection.host", "127.0.0.1")
                .set("spark.cassandra.connection.port", "9042");
;
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaStreamingContext ssc = new JavaStreamingContext(sc, new Duration(5000));

        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "localhost:9092");
		
		
		
		
		
        Set<String> topics = Collections.singleton("test");

        /*JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(
                ssc,
                String.class,
                String.class,
                kafka.serializer.StringDecoder.class,
                kafka.serializer.StringDecoder.class,
                kafkaParams,
                topics
        );*/

        //JavaDStream<Tweet> createTweet = directKafkaStream.map(s -> createTweet(s._2));


        //CassandraStreamingJavaUtil.javaFunctions(createTweet)
                //.writerBuilder("mykeyspace", "rawtweet", mapToRow(Tweet.class))
                //.saveToCassandra();

        ssc.start();
        ssc.awaitTermination();

    }


    /*public static Tweet createTweet(String rawKafka){
        String[] splitted = rawKafka.split("\\|");
        Tweet t = new Tweet(splitted[0], splitted[1], splitted[2], splitted[3]);
        return t;
    }*/
}
