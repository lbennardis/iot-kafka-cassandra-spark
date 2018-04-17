package it.luigibennardis.iot.dashboard.cassandra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * SPRING BOOT APPLICATION CLASS
 */

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"it.luigibennardis.iot.dashboard.cassandra", "it.luigibennardis.iot.dashboard.cassandra.dao"})
public class IoTDataDashboardCassandra {
	  public static void main(String[] args) {
		  
	        SpringApplication.run(IoTDataDashboardCassandra.class, args);
	    }
	}

