package utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import model.MyConfiguration;

public class ConfigurationManager {
		
	private static File configfile= new File("demo.config");
	
	private static MyConfiguration configuration;
	
	public static MyConfiguration getConfiguration() {
		return configuration;
	}
	
	public static void readConfigFile() {
		
		Logger log = Logger.getLogger("ConfigurationManagerLogger");
		
		try {
			FileReader reader = new FileReader(configfile);
			Properties props = new Properties();
			props.load(reader);
			
			configuration = new MyConfiguration();
			configuration.inputStatsDirectory = new File(props.getProperty("inputStatsDirectory"));
			configuration.outputStatsDirectory = new File(props.getProperty("outputStatsDirectory"));
			configuration.writeOnFs = new Boolean(props.getProperty("writeOnFs"));
//			configuration.logDirectory = new File(props.getProperty("logDirectory"));
			configuration.defaultNameKey = new String(props.getProperty("defaultNameKey"));
			configuration.nameKeyToAvoid = new String(props.getProperty("nameKeyToAvoid"));
			configuration.connectionString = new String(props.getProperty("connectionString"));
			configuration.nameDB = new String(props.getProperty("nameDB"));
			configuration.playersCollection = new String(props.getProperty("playersCollection"));
			configuration.statsCollection = new String(props.getProperty("statsCollection"));
			
			reader.close();
			
			log.info("Letto correttamente il file di configurazione " + configfile.getAbsolutePath());
			
		} catch (FileNotFoundException ex) {
			log.warning("File non trovato.");
		} catch (IOException ex) {
			log.warning("Errore in fase di lettura.");
		} catch (Exception ex) {
			log.warning("Errore generico. Probabilmente mancano dei campi sul file di configurazione");
		}
	}
}
