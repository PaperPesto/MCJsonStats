package ui;

import java.util.logging.Logger;

import model.MyConfiguration;
import utility.ConfigurationManager;

public class Program {
	
	private static MyConfiguration config;

	public static void main(String[] args) throws Exception {

		Logger log = Logger.getLogger("MainLogger");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT | %4$-7s | %5$s %n");
		log.info("### Start applicazione");

		// 0 - Lettura del file di configurazione - args[0] è l'indirizzo del file di configurazione
		ConfigurationManager.readConfigFile(args[0]);
		config = ConfigurationManager.getConfiguration();

		// 1 - Lettura da FS
		FileSystemReaderController filesystemreadercontroller = new FileSystemReaderController(config);
		filesystemreadercontroller.execute();

		// 2 - Riorganizzazione JSON
		JsonReorganizationController jsonreorganizationcontroller = new JsonReorganizationController(config, filesystemreadercontroller.get());
		jsonreorganizationcontroller.execute();

		// 3 - Lettura DB
		LastStatisticsController laststatisticscontroller = new LastStatisticsController(config);
		laststatisticscontroller.execute();

		// 4 - Inserimento su mongodb
		InsertOnlyNewStatsController insertonlynewstatscontroller = new InsertOnlyNewStatsController(config, jsonreorganizationcontroller.get(), laststatisticscontroller.get());
		insertonlynewstatscontroller.execute();

		// 5 - Scrittura su FS
		FileSystemWriterController filesystemwritercontroller = new FileSystemWriterController(config, jsonreorganizationcontroller.get());
		filesystemwritercontroller.execute();
		
		log.info("### Stop applicazione");
	}
}
