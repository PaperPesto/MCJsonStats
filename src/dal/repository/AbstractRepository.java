package dal.repository;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import model.MyConfiguration;

public abstract class AbstractRepository {

	protected MongoDatabase db;
	protected MongoCollection<Document> coll;
	protected MyConfiguration config;
}
