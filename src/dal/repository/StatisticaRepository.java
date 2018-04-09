package dal.repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import model.MyConfiguration;
import model.StatisticaDTO;

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

		// Questo blocco qui sotto va messo per forza senn� non funziona - BUG della
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
			String new_uuid = newstat.getString("uuid");
			boolean match = false;

			for (StatisticaDTO oldstat : oldstats) {
				String old_uuid = oldstat.uuid;

				if (new_uuid.equals(old_uuid)) {
					// match
					Date new_date = (Date) newstat.get("date");
					Date old_date = oldstat.date;
					
					System.out.println();
					System.out.println("uuid: " + new_uuid);
					System.out.println("FS: " + new_date);
					System.out.println("DB: " + old_date);
					match = true;
					
					if(new_date.before(old_date)) {
						System.out.println("Il DB � pi� aggiornato del FS (x)");
					}
					if(new_date.equals(old_date)) {
						System.out.println("La data su DB � uguale a quella su FS (no-update)");
						
					}
					if(new_date.after(old_date)) {
						System.out.println("Il DB deve essere aggiornato (v)");
						// Inserimento su DB nuove statistiche
//						insertStatistica(newstat);
					}
					break;
				}
			}
			
			if(!match) {
				// Ho trovato un nuovo uuid che non era presente nel DB
				System.out.println("Trovato nuovo uuid: " + new_uuid);
			}
		}
	}
}
