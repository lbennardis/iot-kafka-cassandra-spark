package it.luigibennardis.iot.dashboard.cassandra.dao;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import it.luigibennardis.iot.dashboard.cassandra.dao.entity.TrafficoTotaleEntity;

/**
 * DAO class for total_traffic 
 * 
 *
 */
@Repository
public interface TrafficoTotaleDataRepo extends CassandraRepository<TrafficoTotaleEntity>{

	 @Query("SELECT * FROM traffickeyspace.traffico_totale WHERE datainserimento = ?0 ALLOW FILTERING")
	 Iterable<TrafficoTotaleEntity> findTrafficDataByDate(String date);	 
}
