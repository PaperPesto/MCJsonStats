package dal.repository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
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

		coll = db.getCollection(config.statsCollection);
		Document document = coll.find(new Document("uuid", uuid)).sort(Sorts.ascending("date")).first();

		StatisticaDTO stat = new StatisticaDTO();

		stat._id = document.getObjectId("_id");
		stat.uuid = document.getString("uuid");
		stat.date = document.getDate("date");

		return stat;
	}

	
	public void makeLastStatsCollection() {
		
		lastStatsCollectionDrop();	// Droppa la collection
		
		coll = db.getCollection(config.statsCollection);
		
		AggregateIterable<Document> docs = coll.aggregate(Arrays.asList(
				new BasicDBObject("$sort", new BasicDBObject("date", 1)),
				new BasicDBObject("$group",
						new BasicDBObject("_id", "$uuid").append("lastUpdate", new BasicDBObject("$last", "$date"))
								.append("count", new BasicDBObject("$sum", 1))),
				new BasicDBObject("$out", config.lastStatsCollection)));	// Ricrea la collecion

		// Questo blocco qui sotto va messo per forza sennò non funziona - BUG della
		// libreria?
		for (@SuppressWarnings("unused")
			Document d : docs) {

		}
		// --------------------
	}
	
	
	private void lastStatsCollectionDrop() {
		Logger log = Logger.getLogger("StatisticaRepository::lastStatsCollectionDrop");
		try {
			db.getCollection(config.lastStatsCollection).drop(); // Elimino la collection con le utlime statistiche, verrà poi ricreata poche righe sotto nell'out
		} catch(Exception e) {
			log.warning("Errore nel drop della collection");
		}
	}

	
	public List<StatisticaDTO> getLastStatistics() {
		
		Logger log = Logger.getLogger("StatisticaRepository::getLastStatistica");
		MongoCollection<Document> coll2 = null;
		try {
			coll2 = db.getCollection(config.lastStatsCollection);
		} catch(Exception e) {
			log.warning("Collection " + config.lastStatsCollection + " non trovata.");
			throw e;
		}
		
		List<Document> docs = new ArrayList<Document>();
		MongoCursor<Document> cursor = coll2.find().iterator();

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

		DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
		
		for (Document d : documents) {
			StatisticaDTO stat = new StatisticaDTO();
			stat.uuid = d.getString("_id");
			Date result = null;
			try {
				result =  d.getDate("lastUpdate");
			} catch (Exception e) {
				e.printStackTrace();
			}
			stat.date = result;	// Attualmente lastUpdate non è una data in BSON, mi tocca parsarlo a merda
			stat.count = d.getInteger("count");
			statistiche.add(stat);
		}

		return statistiche;
	}

	
	public void insertStatistica(JSONObject statistica) {
		coll = db.getCollection(config.statsCollection);
		Logger log = Logger.getLogger("StatisticaRepository::Insert");
		log.info("Inserimento in " + config.statsCollection + " dell'uuid " + statistica.get("uuid"));
		Document stat = Document.parse(statistica.toString());
		
		stat.append("date", new Date(statistica.getLong("date")));	// Questo sovrascrive la date in stringa e la mette in datetime di mongo
		
		try {
			coll.insertOne(stat);
		} catch (Exception e) {
			log.warning("Problemi nell'inserimento del documento");
			throw e;
		}
	}
	

	public void insertOnlyNewStats(List<StatisticaDTO> oldstats, List<JSONObject> newstats) {
		int newplayer = 0, oldercounter = 0, youngercounter = 0, samecounter = 0;

		for (JSONObject newstat : newstats) {
			String new_uuid = newstat.getString("uuid");
			boolean match = false;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			for (StatisticaDTO oldstat : oldstats) {
				String old_uuid = oldstat.uuid;

				if (new_uuid.equals(old_uuid)) {
					// match
					Date new_date = new Date(newstat.getLong("date"));
					Date old_date = oldstat.date;
					
					System.out.println();
					System.out.println("uuid: " + new_uuid);
					System.out.println("FS: " + sdf.format(new_date));
					System.out.println("DB: " + sdf.format(old_date));
					match = true;
					
					if(new_date.getTime()/1000 < old_date.getTime()/1000) {
						System.out.println("Il DB è più aggiornato del FS ( X )");
						// Sta leggendo statistiche più vecchie, forse è cambiata la cartella
						oldercounter++;
					}
					if(new_date.getTime()/1000 == old_date.getTime()/1000) {
						System.out.println("La data su DB è uguale a quella su FS ( no-update )");
						// Non fare niente
						samecounter++;
					}
					if(new_date.getTime()/1000 > old_date.getTime()/1000) {
						System.out.println("Il DB deve essere aggiornato ( V )");
						// Inserimento su DB nuove statistiche
						insertStatistica(newstat);
						youngercounter++;
					}
					break;
				}
			}
			
			if(!match) {
				// Ho trovato un nuovo uuid che non era presente nel DB
				System.out.println();
				System.out.println("uuid: " + new_uuid);
				System.out.println("FS: " + sdf.format(new Date(newstat.getLong("date"))));
				insertStatistica(newstat);
				newplayer++;
			}
		}
		// Riepilogo
		System.out.println();
		System.out.println("Vecchie statistiche=" + oldercounter);
		System.out.println("Stesse statistiche=" + samecounter);
		System.out.println("Nuove statistiche=" + youngercounter);
		System.out.println("Nuovo giocatore=" + newplayer);
	}
}
