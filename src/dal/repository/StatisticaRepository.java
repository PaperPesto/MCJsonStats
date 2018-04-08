package dal.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;

import dal.MongoClientConnection;
import dal.StatisticaDTO;
import model.MyConfiguration;

public class StatisticaRepository extends AbstractRepository implements IStatisticaRepository {

	public StatisticaRepository(MyConfiguration config) {
		this.config = config;
		db = MongoClientConnection.getInstance(this.config).getDatabase(this.config.nameDB);
		coll = db.getCollection(this.config.statsCollection);
	}

	public StatisticaDTO getLastStatisticaById(String uuid) {

		Document document = coll.find(new Document("uuid", uuid)).sort(Sorts.ascending("date")).first();

		StatisticaDTO stat = new StatisticaDTO();

		stat._id = document.getObjectId("_id");
		stat.uuid = document.getString("uuid");
		stat.date = document.getDate("date");

		return stat;
	}

	public void makeLastStatsCollection() {

		AggregateIterable<Document> docs = coll.aggregate(Arrays.asList(
				new BasicDBObject("$sort", new BasicDBObject("date", 1)),
				new BasicDBObject("$group",
						new BasicDBObject("_id", "$uuid").append("lastUpdate", new BasicDBObject("$last", "$date"))
								.append("count", new BasicDBObject("$sum", 1))),
				new BasicDBObject("$out", config.lastStatsCollection)));

		// Questo blocco qui sotto va messo per forza sennò non funziona - BUG della
		// libreria?
		for (@SuppressWarnings("unused")
		Document d : docs) {

		}
		// --------------------------------------------------------------------
	}

	public List<StatisticaDTO> getLastStatistics() {

		coll = db.getCollection(config.lastStatsCollection);
		List<Document> docs = new ArrayList<Document>();
		MongoCursor<Document> cursor = coll.find().iterator();

		while (cursor.hasNext()) {
			Document document = cursor.next();
			docs.add(document);
		}
		cursor.close();

		List<StatisticaDTO> statistiche = statisticheBuilder(docs);

		return statistiche;
	}

	// builder di StatisticaDTO, non dovrebbe stare nel repository
	private List<StatisticaDTO> statisticheBuilder(List<Document> documents) {

		List<StatisticaDTO> statistiche = new ArrayList<StatisticaDTO>();

		for (Document d : documents) {
			StatisticaDTO stat = new StatisticaDTO();
			stat.uuid = d.getString("_id");
			stat.date = d.getDate("lastUpdate");
			stat.count = d.getInteger("count");
			statistiche.add(stat);
		}

		return statistiche;
	}

	public void insertStatistica(JSONObject statistica) {
		Logger log = Logger.getLogger("StatisticaRepository::Insert");
		Document stat = new Document();
		stat.put("uuid", statistica.get("uuid"));
		stat.put("date", statistica.get("date"));
		Document document = Document.parse(statistica.get("stat").toString());
		stat.put("stat", document);

		try {
			coll.insertOne(stat);
		} catch (Exception e) {
			log.warning("Problemi nell'inserimento del documento");
			throw e;
		}
	}

	public void insertOnlyNewStats(List<StatisticaDTO> oldstats, List<JSONObject> newstats) {

		for (JSONObject newstat : newstats) {
			String newuuid = newstat.getString("uuid");

			StatisticaDTO matchedstat = oldstats
					.stream()
					.filter(x -> x.uuid.equals(newuuid))
					.findFirst()
					.orElse(null);
			
			System.out.println("vecchia: " + matchedstat.date.toString());
			System.out.println("nuova: " + newstat.get("date"));
		}
	}
}
