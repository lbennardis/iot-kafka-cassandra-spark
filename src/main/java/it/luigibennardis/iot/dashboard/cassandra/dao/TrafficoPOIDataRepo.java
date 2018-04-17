package it.luigibennardis.iot.dashboard.cassandra.dao;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoPOIEntity;
import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoTotaleEntity;
import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoFinestraTemporaleEntity;

/**
 * CLASSE DAO PER LA TABELLA traffico_poi  
 * 
 */
@Repository
public interface TrafficoPOIDataRepo extends CassandraRepository<TrafficoPOIEntity>{
		 
	@Query("SELECT * FROM traffickeyspace.traffico_poi")
	 Iterable<TrafficoPOIEntity> findAllPoi();
	
}
