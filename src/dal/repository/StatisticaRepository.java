package dal.repository;

import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONObject;

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
				new Document("uuid", uuid)
				)
				.sort(Sorts.ascending("date"))
				.first();
		
		StatisticaDTO stat = new StatisticaDTO();
		
		stat._id = document.getObjectId("_id");
		stat.uuid = document.getString("uuid");
		stat.date = document.getDate("date");
		
		return stat;
	}
	
	public void insertStatistica(JSONObject statistica) {
		Logger log = Logger.getLogger("StatisticaRepository::Insert");
		Document stat = new Document();
		stat.put("uuid", statistica.get("uuid"));	
		stat.put("date", statistica.get("date"));
		Document document = Document.parse( statistica.get("stat").toString() );
		stat.put("stat", document);
		
		try{
			coll.insertOne(stat);
		} catch(Exception e) {
			log.warning("Problemi nell'inserimento del documento");
			throw e;
		}
	}
}

	
