package ui;

import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONObject;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import bl.JsonBusiness;
import dal.PlayerDTO;
import dal.fs.FileSystemReader;
import dal.fs.FileSystemWriter;
import dal.repository.GenericRepository;
import dal.repository.PlayerRepository;
import model.MetaJson;
import model.MyConfiguration;
import utility.ConfigurationManager;

public class Program {
	public static void main(String[] args) throws Exception {
		
		Logger log = Logger.getLogger("MainLogger");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT | %4$-7s | %5$s %n");

		// Lettura del file di configurazione
		ConfigurationManager.readConfigFile();
		MyConfiguration config = ConfigurationManager.getConfiguration();
		
		// Lettura Player da DB
		PlayerRepository pgrepo = new PlayerRepository(config);
		PlayerDTO lippo = pgrepo.getPlayerByNickname("Leep");
		List<PlayerDTO> players = pgrepo.getAllPlayers();

		// Lettura da FS
		FileSystemReader reader = new FileSystemReader(config);
		reader.readFileList();
		reader.read();
		List<MetaJson> metajsonlist = reader.getPayload();

		// Core business
		JsonBusiness business = new JsonBusiness(metajsonlist, config);
		business.execute();
		List<JSONObject> jsonlist = business.getOutputJson();
	
		// Scrittura su FS
		if(config.writeOnFs) {
			FileSystemWriter writer = new FileSystemWriter(jsonlist, config);
			writer.writeFiles();			
		}
		
		// Mongo
		GenericRepository repo = new GenericRepository("Minecraft", "AnanasWorld.PlayerStatsTest", config);
		Document doc = Document.parse(jsonlist.get(3).toString());
		repo.insertDocument(doc);
	}
}
