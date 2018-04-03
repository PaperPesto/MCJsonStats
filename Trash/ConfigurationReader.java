package bl;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import model.MyConfiguration;
import utility.ConfigurationManager;

public class ConfigurationReader {
	
	private MyConfiguration config;
	private List<File> fileList;
	
	public ConfigurationReader(MyConfiguration config) {
		this.config = config;
	}
	
	public void readConfiguration() {
		File folder = new File(config.inputStatsDirectory.getAbsolutePath());
		fileList = Arrays.asList(folder.listFiles());
		
		Logger log = Logger.getLogger("myLogger");	// si può far di meglio?
		log.info("### Start applicazione");
		log.info("Trovati " + fileList.size() + " file di statistiche nella cartella " + folder.getAbsolutePath());
	}
	
	public List<File> getFileList(){
		Logger log = Logger.getLogger("myLogger");
		log.info("Restituita tutta la lista di file");
		return fileList;
	}
	
	public List<File> getFileList(Predicate<File> predicate){
		List<File> filelist = fileList.stream()
				.filter(predicate).collect(Collectors.toList());
		
		Logger log = Logger.getLogger("myLogger");
		log.info("Restituiti " + filelist.size() + " file");
		return filelist;
	}
}

// File jsonConAchievement = inputFiles.stream()
//.filter(x -> x.getName().equals("412bdbfd-f2cc-3489-a757-cd1b2bf48278.json")).findFirst().get();
