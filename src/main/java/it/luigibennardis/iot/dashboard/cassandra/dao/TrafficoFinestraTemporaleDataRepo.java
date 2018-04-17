package it.luigibennardis.iot.dashboard.cassandra.dao;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoFinestraTemporaleEntity;

/**
 * DAO class for window_traffic 
 * 
 *
 */
@Repository
public interface TrafficoFinestraTemporaleDataRepo extends CassandraRepository<TrafficoFinestraTemporaleEntity>{
	
	@Query("SELECT * FROM traffickeyspace.traffico_finestra WHERE datainserimento = ?0 ALLOW FILTERING")
	 Iterable<TrafficoFinestraTemporaleEntity> findTrafficDataByDate(String date);

}
