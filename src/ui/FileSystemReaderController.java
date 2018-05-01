package ui;

import java.util.List;

import dal.fs.FileSystemReader;
import model.MyConfiguration;
import model.StatisticaFS;

public class FileSystemReaderController {

	private MyConfiguration config;
	private List<StatisticaFS> newstats;

	public FileSystemReaderController(MyConfiguration config) {
		this.config = config;
	}

	public void execute() {
		// Lettura da FS
		FileSystemReader reader = new FileSystemReader(config);
		reader.readFileList();
		reader.read();
		newstats = reader.getPayload();
	}
	
	public List<StatisticaFS> get(){
		return newstats;
	}
}
