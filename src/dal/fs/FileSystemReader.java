package dal.fs;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import model.JsonStringStat;
import model.MyConfiguration;

public class FileSystemReader {

	private MyConfiguration config;
	private List<JsonStringStat> metaJsonList;
	private List<File> fileList;

	// Costruttore
	public FileSystemReader(MyConfiguration config) {
		this.config = config;
		metaJsonList = new ArrayList<JsonStringStat>();
	}

	public void readFileList() {
		Logger log = Logger.getLogger("myLogger"); // si può far di meglio?
		
		File folder = new File(config.inputStatsDirectory.getAbsolutePath());
		fileList = Arrays.asList(folder.listFiles());

		log.info("Trovati " + fileList.size() + " file di statistiche nella cartella " + folder.getAbsolutePath());
	}
	
	public void read() {
		Logger log = Logger.getLogger("FileSystemReader::read()");
		
		for(File f : fileList) {
			byte[] encoded = null;

			try {
				encoded = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
			} catch (IOException e1) {
				log.warning("Problemi nella lettura del file");
				e1.printStackTrace();
			}
			String jsonString = new String(encoded, StandardCharsets.UTF_8);
			
			JsonStringStat jsonstat = new JsonStringStat();
			jsonstat.jsonString = jsonString;
			jsonstat.name = f.getName();
			jsonstat.sourceFile = f;
			
			metaJsonList.add(jsonstat);
		}
	}

	public List<JsonStringStat> getPayload() {
		return metaJsonList;
	}

	// File jsonConAchievement = inputFiles.stream()
	// .filter(x ->
	// x.getName().equals("412bdbfd-f2cc-3489-a757-cd1b2bf48278.json")).findFirst().get();
}
