package dal.repository;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bson.Document;

import dal.MongoClientConnection;
import model.MyConfiguration;

public class CollectionTesterRepository extends AbstractRepository{

	public CollectionTesterRepository(MyConfiguration config) {
		db = MongoClientConnection.getInstance(config).getDatabase("javaTest");
		coll = null;
	}
	
	public void generateDateTimeCollection() {
		coll = db.getCollection("dateTimeCollectionTest");
		
		int i;
		long max = 1513728000000L;
		
		for(i = 0; i < 1000; i++) {			
			Document testdoc = new Document();
			long randomDay = ThreadLocalRandom.current().nextLong(max);
			
			testdoc.append("n", i)
			.append("LONGdate", randomDay)
			.append("perc", (double)randomDay/max*100)
			.append("ISOdate", new Date(randomDay));
			
			coll.insertOne(testdoc);
			System.out.println(i);
		}
	}
	
	public void generateDemoStatCollection() {
		coll = db.getCollection("demoStatCollection");
		
		int i;
		long max = 1513728000000L;
		
		for(i = 0; i < 1000; i++) {			
			Document testdoc = new Document();
			long randomDay = ThreadLocalRandom.current().nextLong(max);
			int ran = ThreadLocalRandom.current().nextInt(12);
			
			testdoc.append("random_uuid", UUID.randomUUID().toString())
			.append("ISOdate", new Date(randomDay))
			.append("testStat", i)
			.append("id", ran);
			
			coll.insertOne(testdoc);
			System.out.println(i);
		}
	}
}
