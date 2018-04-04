package dal.repository;

import org.bson.Document;

import com.mongodb.client.model.Sorts;

import dal.MongoClientConnection;
import dal.StatisticaDTO;
import model.MyConfiguration;

public class StatisticaRepository extends AbstractRepository implements IStatisticaRepository{
	
	public StatisticaRepository(MyConfiguration config) {
		db = MongoClientConnection.getInstance(config).getDatabase(config.nameDB);
		coll = db.getCollection(config.statsCollection);
	}


	public StatisticaDTO getFirstStatisticaById(String uuid) {
		
		Document document = coll.find( 
				new Document("minecraft_uuid", uuid)
				)
				.sort(Sorts.ascending("ISOdate"))
				.first();
		
		StatisticaDTO stat = new StatisticaDTO();
		stat.minecraft_uuid = document.getString("minecraft_uuid");
		stat.lastUpdate = document.getDate("ISOdate");
		
		return stat;
	}
}

	
