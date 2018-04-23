package ui;

import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;
import org.json.JSONObject;

import bl.JsonBusiness;
import dal.fs.FileSystemReader;
import dal.fs.FileSystemWriter;
import dal.repository.GenericRepository;
import dal.repository.StatisticaRepository;
import model.MyConfiguration;
import model.StatisticaDTO;
import model.StatisticaFS;
import utility.ConfigurationManager;

public class Program {

	public static void main(String[] args) throws Exception {

		Logger log = Logger.getLogger("MainLogger");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT | %4$-7s | %5$s %n");
		
		// Lettura del file di configurazione - args[0] è l'indirizzo del file di configurazione
		ConfigurationManager.readConfigFile(args[0]);
		MyConfiguration config = ConfigurationManager.getConfiguration();

		// Lettura da FS
		FileSystemReader reader = new FileSystemReader(config);
		reader.readFileList();
		reader.read();
		List<StatisticaFS> newstats = reader.getPayload();

		// Riorganizzazione JSON
		JsonBusiness business = new JsonBusiness(newstats, config);
		business.execute();
		List<JSONObject> jsonlist = business.getOutputJson();

		// Lettura DB
		StatisticaRepository statrepo = new StatisticaRepository(config);
		statrepo.makeLastStatsCollection();
		List<StatisticaDTO> oldstats = statrepo.getLastStatistics();

		// Operazioni DAL con controllo
		statrepo.insertOnlyNewStats(oldstats, jsonlist);

		// Scrittura su FS
		if (config.writeOnFs) {
			FileSystemWriter writer = new FileSystemWriter(jsonlist, config);
			writer.writeFiles();
		}
	}
}
