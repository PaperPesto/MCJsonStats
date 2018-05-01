package ui;

import java.util.List;

import org.json.JSONObject;

import dal.fs.FileSystemWriter;
import model.MyConfiguration;

public class FileSystemWriterController {

	private MyConfiguration config;
	private List<JSONObject> jsonlist;
	
	public FileSystemWriterController(MyConfiguration config, List<JSONObject> jsonlist) {
		this.config = config;
		this.jsonlist = jsonlist;
	}
	
	public void execute() {
		if (config.writeOnFs) {
			FileSystemWriter writer = new FileSystemWriter(jsonlist, config);
			writer.writeFiles();
		}
	}
}
