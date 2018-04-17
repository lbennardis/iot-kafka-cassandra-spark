package it.luigibennardis.iot.dashboard.cassandra.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.config.java.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

/**
 * Spring bean configuration for Cassandra db.
 */
@Configuration
@PropertySource(value = {"classpath:iot-control-dashboard-cassandra.properties"})
@EnableCassandraRepositories(basePackages = {"it.luigibennardis.iot.dashboard.cassandra.dao"})
public class CassandraConfig extends AbstractCassandraConfiguration{
	
    @Autowired
    private Environment env;
    
    @Bean
    public CassandraClusterFactoryBean cluster() {
        
    	//*** DEFINISCE IL PUNTAMENTO AL CLUSTER DI CASSANDRA
    	CassandraClusterFactoryBean cassandraCluster = new CassandraClusterFactoryBean();
       
       
    	cassandraCluster.setContactPoints(env.getProperty("it.luigibennardis.iot.dashboard.cassandra.host"));
    	cassandraCluster.setPort(Integer.parseInt(env.getProperty("it.luigibennardis.iot.dashboard.cassandra.port")));
        
        
        return cassandraCluster;
    }
  
    @Bean
    public CassandraMappingContext cassandraMapping(){
         return new BasicCassandraMappingContext();
    }
    
	@Override
	@Bean
	protected String getKeyspaceName() {
		return env.getProperty("it.luigibennardis.iot.dashboard.cassandra.keyspace");
	}
}
