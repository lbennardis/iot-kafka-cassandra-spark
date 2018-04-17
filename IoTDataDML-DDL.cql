CREATE KEYSPACE IF NOT EXISTS TrafficKeySpace WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};
 
CREATE TABLE TrafficKeySpace.Traffico_Totale (idStrada text ,  tipoVeicolo text,numeroVeicoli bigint, timeStamp timestamp,dataInserimento text,PRIMARY KEY (idStrada,dataInserimento,tipoVeicolo));

CREATE TABLE TrafficKeySpace.Traffico_Finestra (idStrada text , tipoVeicolo text, numeroVeicoli bigint, timeStamp timestamp,dataInserimento text,PRIMARY KEY (idStrada,dataInserimento,tipoVeicolo));
	
CREATE TABLE TrafficKeySpace.Traffico_Poi (idveicolo text , tipoVeicolo text ,distanza bigint,  timeStamp timestamp,PRIMARY KEY (idveicolo));

 
SELECT * FROM TrafficKeySpace.Traffico_Totale;
SELECT * FROM TrafficKeySpace.Traffico_Finestra;
SELECT * FROM TrafficKeySpace.Traffico_Poi;

 
TRUNCATE TABLE TrafficKeySpace.Traffico_Totale;
TRUNCATE TABLE TrafficKeySpace.Traffico_Finestra;
TRUNCATE TABLE TrafficKeySpace.Traffico_Poi;

 
DROP TABLE TrafficKeySpace.Traffico_Totale;
DROP TABLE TrafficKeySpace.Traffico_Finestra;
DROP TABLE TrafficKeySpace.Traffico_Poi;
