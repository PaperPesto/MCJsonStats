package ui;

import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;

import bl.JsonBusiness;
import dal.fs.FileSystemReader;
import dal.fs.FileSystemWriter;
import dal.repository.CollectionTesterRepository;
import dal.repository.GenericRepository;
import dal.repository.StatisticaRepository;
import model.StatisticaFS;
import model.MyConfiguration;
import model.StatisticaDTO;
import utility.ConfigurationManager;

public class Program {
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		Logger log = Logger.getLogger("MainLogger");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT | %4$-7s | %5$s %n");

		// Lettura del file di configurazione
		ConfigurationManager.readConfigFile();
		MyConfiguration config = ConfigurationManager.getConfiguration();
		
		// Prova su DemoStatCollection
//		GenericRepository repo = new GenericRepository("javaTest", "demoStatCollection", config);
//		repo.readDocumentsByGroup();
//		CollectionTesterRepository reppo = new CollectionTesterRepository(config);
//		reppo.generateDemoStatCollection();

		// Lettura DB
		// hnjb - codice di pica
		StatisticaRepository statrepo = new StatisticaRepository(config);
		statrepo.makeLastStatsCollection();
		List<StatisticaDTO> oldstats = statrepo.getLastStatistics();

		// Lettura da FS
		FileSystemReader reader = new FileSystemReader(config);
		reader.readFileList();
		reader.read();
		List<StatisticaFS> newstats = reader.getPayload();

		// Core business
		JsonBusiness business = new JsonBusiness(newstats, config);
		business.execute();
		List<JSONObject> jsonlist = business.getOutputJson();
		
		// Prova matching vecchie-nuove statistiche
		statrepo.insertOnlyNewStats(oldstats, jsonlist);

		// Scrittura su DB - dead code
		if (false) {
			for (JSONObject js : jsonlist) {
				statrepo.insertStatistica(js);
			}
		}

		// Scrittura su FS
		if (config.writeOnFs) {
			FileSystemWriter writer = new FileSystemWriter(jsonlist, config);
			writer.writeFiles();
		}
	}
}
