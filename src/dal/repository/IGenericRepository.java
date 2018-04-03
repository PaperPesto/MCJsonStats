package dal.repository;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public interface IGenericRepository {

	// Insert
	void insertDocument(Document document);
	// Read
	Document readDocument();
	Document readFirstDocument();
	Document readDocumentByKey(String key, int value);
	Document readFirstDocumentByKey(String key, int value);
	// Update
	// Delete
}
