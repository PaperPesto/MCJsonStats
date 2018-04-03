package dal;

import java.util.logging.Logger;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import model.MyConfiguration;

public class MongoClientConnection {
	
	private static MongoClientConnection instance;
	private MongoClient mongoClient;
	private String connectionString;
	
	private MongoClientConnection(MyConfiguration config) {
		connectionString = config.connectionString;
		initialize();
	}
	
	
	public static MongoClientConnection getInstance(MyConfiguration config) {
		
		if(instance == null) {
			return new MongoClientConnection(config);
		}
		return instance;
	}
	
	
	private void initialize(){
		
		Logger log = Logger.getLogger("MongoClientConnection");
		
		MongoClientURI uri = new MongoClientURI(connectionString);
		mongoClient = new MongoClient(uri);
		
		log.info("Connessione stabilita con il database all'indirizzo " + mongoClient.getServerAddressList().toString());
	}
	
	public MongoDatabase getDatabase(String dbName) {
		return mongoClient.getDatabase(dbName);
	}
}
