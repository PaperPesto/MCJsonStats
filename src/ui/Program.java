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
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {

		Logger log = Logger.getLogger("MainLogger");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT | %4$-7s | %5$s %n");

		// Lettura del file di configurazione
		ConfigurationManager.readConfigFile();
		MyConfiguration config = ConfigurationManager.getConfiguration();

		// Prova ancora
		// GenericRepository filippo = new GenericRepository("test",
		// "collezioneDiProva", config);
		// Document morandi = new Document("carrozziera", "Renault");
		// filippo.insertDocument(morandi);

		// Prova su DemoStatCollection
		// GenericRepository repo = new GenericRepository("javaTest",
		// "demoStatCollection", config);
		// repo.readDocumentsByGroup();
		// CollectionTesterRepository reppo = new CollectionTesterRepository(config);
		// reppo.generateDemoStatCollection();

		// Lettura da FS
		FileSystemReader reader = new FileSystemReader(config);
		reader.readFileList();
		reader.read();
		List<StatisticaFS> newstats = reader.getPayload();

		// Riorganizzazione JSON
		JsonBusiness business = new JsonBusiness(newstats, config); // Problemea qui
		business.execute();
		List<JSONObject> jsonlist = business.getOutputJson();

		// Finto inserimento di statistica
		// for(JSONObject o : jsonlist) {
		// statrepo.insertStatistica(o);
		// }

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
