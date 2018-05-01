package utility;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bson.Document;

import dal.repository.GenericRepository;
import model.MyConfiguration;

public class CollectionTester {

	public static void generateDateTimeCollection(MyConfiguration config) {
		GenericRepository genrepo = new GenericRepository("testFromJava", "datetimeCollection", config);
		
		int i;
		long max = 1513728000000L;
		
		for(i = 0; i < 1000; i++) {			
			Document testdoc = new Document();
			long randomDay = ThreadLocalRandom.current().nextLong(max);
			
			testdoc.append("n", i)
			.append("LONGdate", randomDay)
			.append("perc", (double)randomDay/max*100)
			.append("ISOdate", new Date(randomDay));
			
			genrepo.insertDocument(testdoc);
			System.out.println(i);
		}
	}
	
	public static void generateDemoStatCollection(MyConfiguration config) {
		GenericRepository genrepo = new GenericRepository("testFromJava", "demoStatCollection", config);
		
		int i;
		long max = 1513728000000L;
		
		for(i = 0; i < 1000; i++) {			
			Document testdoc = new Document();
			long randomDay = ThreadLocalRandom.current().nextLong(max);
			
			testdoc.append("minecraft_id", UUID.randomUUID().toString())
			.append("ISOdate", new Date(randomDay))
			.append("testStat", i);
			
			genrepo.insertDocument(testdoc);
			System.out.println(i);
		}
	}
}
