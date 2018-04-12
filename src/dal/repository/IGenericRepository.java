package dal.repository;

import java.util.List;

import org.bson.Document;

public interface IGenericRepository {

	// Insert
	void insertDocument(Document document);
	// Read
	Document readDocument();
	List<Document> readAllDocuments();
	Document readFirstDocument();
	Document readDocumentByKey(String key, int value);
	Document readFirstDocumentByKey(String key, int value);
	// Update
	// Delete
}
