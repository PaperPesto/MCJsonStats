package dal.repository;

// Importazione metodi statici
import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Accumulators.*;
import static com.mongodb.client.model.Sorts.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.model.Field;
import com.mongodb.client.model.Sorts;

import dal.MongoClientConnection;
import dal.StatisticaDTO;
import model.MyConfiguration;

public class StatisticaRepository extends AbstractRepository implements IStatisticaRepository{
	
	public StatisticaRepository(MyConfiguration config) {
		db = MongoClientConnection.getInstance(config).getDatabase(config.nameDB);
		coll = db.getCollection(config.statsCollection);
	}


	public StatisticaDTO getLastStatisticaById(String uuid) {
		
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
	
	
	public List<StatisticaDTO> getLastStatistiche() {
		
		Logger log = Logger.getLogger("StatisticaRepository::getAllStatistiche");

		List<StatisticaDTO> statistiche = new ArrayList<StatisticaDTO>();

		// Il pipeline ha un verso di percorrenza quando si fa il group dei documenti
		// In particolare: prima si filtra, poi si grouppa, poi si fanno aggregazioni
		AggregateIterable<Document> docs = coll.aggregate(Arrays.asList(
				sort(orderBy(ascending("date"))),
				new BasicDBObject("$group", 
						new BasicDBObject("_id", "$uuid")
						.append("lastUpdate", new BasicDBObject("$last", "$date"))
						.append("count", new BasicDBObject("$sum", 1))),
                out("statsFromJava")

				));
		
		for(Document d : docs) {
			System.out.println(d.toString());
		}
		
		return statistiche;
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

	
