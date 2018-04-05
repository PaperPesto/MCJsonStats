package dal.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;

import dal.MongoClientConnection;
import model.MyConfiguration;

public class GenericRepository extends AbstractRepository implements IGenericRepository{

	public GenericRepository(String dbName, String collName, MyConfiguration config) {
		db = MongoClientConnection.getInstance(config).getDatabase(dbName);
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



}
