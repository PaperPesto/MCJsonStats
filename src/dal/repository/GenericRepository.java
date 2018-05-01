package dal.repository;

import static com.mongodb.client.model.Aggregates.out;
import static com.mongodb.client.model.Aggregates.sort;
import static com.mongodb.client.model.Sorts.ascending;
import static com.mongodb.client.model.Sorts.orderBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;

import dal.MongoClientConnection;
import model.MyConfiguration;

public class GenericRepository extends AbstractRepository implements IGenericRepository{

	public GenericRepository(String dbName, String collName, MyConfiguration config) {
		this.config = config;
		db = MongoClientConnection.getInstance(this.config).getDatabase(dbName);
		coll = db.getCollection(collName);
	}
	
	public void insertDocument(Document document) {
		Logger log = Logger.getLogger("GenericRepository::INsert");
		try{
			coll.insertOne(document);
		} catch(Exception e) {
			log.warning("Problemi nell'inserimento del documento");
			throw e;
		}
	}

	public Document readDocument() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Document readDocumentByKey(String key, int value) {
		Document filter = new Document();
		filter.put(key, value);
		return coll.find(filter).first();
	}

	public Document readFirstDocumentByKey(String key, int value) {
		Document filter = new Document();
		filter.put(key, value);
		
		return coll.find(filter).sort(Sorts.ascending("ISOdate")).first();
	}

	public Document readFirstDocument() {
		
		return coll.find().sort(Sorts.descending("ISOdate")).first();
	}

	
	public List<Document> readAllDocuments() {
		
		List<Document> docs = new ArrayList<Document>();
		MongoCursor<Document> cursor = coll.find().iterator();

		while (cursor.hasNext()) {
			Document document = cursor.next();
			docs.add(document);
		}
		cursor.close();
		
		return docs;
	}

	public void readDocumentsByGroup() {
		// Il pipeline ha un verso di percorrenza quando si fa il group dei documenti
		// In particolare: prima si filtra, poi si grouppa, poi si fanno aggregazioni
		AggregateIterable<Document> docs = coll.aggregate(Arrays.asList(
				sort(orderBy(ascending("ISOdate"))),
				new BasicDBObject("$group", 
						new BasicDBObject("_id", "$id")
						.append("lastUpdate", new BasicDBObject("$last", "$ISOdate"))
						.append("count", new BasicDBObject("$sum", 1))),
                out("aggregationFromJava")

				));
		
		for(Document d : docs) {
			System.out.println(d.toString());
		}
		
		boolean ferma = true;
	}


}
