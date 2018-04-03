package utility;

import java.text.SimpleDateFormat;

import org.json.JSONObject;

import model.MyConfiguration;

public class FileNameBuilder {

	private String fileName;
	private JSONObject json;
	private MyConfiguration config;

	public FileNameBuilder(JSONObject json, MyConfiguration config) {
		this.json = json;
		this.config = config;
	}
	
	public void buildNameWithDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy_HHmmss");
		String cookedName = config.outputStatsDirectory.getAbsolutePath()
				.replace("\\", "\\\\")
				.concat("\\\\")
				.concat(json.getString("uuid"))
				.concat("_")
				.concat(sdf.format(json.get("date")))
				.concat(".json");
		
		fileName = cookedName;
	}
	
	public void buildName() {
		String cookedName = config.outputStatsDirectory.getAbsolutePath()
				.replace("\\", "\\\\")
				.concat("\\\\")
				.concat(json.getString("uuid"))
				.concat(".json");
		
		fileName = cookedName;
	}
	
	public String getName() {
		return fileName;
	}
}
