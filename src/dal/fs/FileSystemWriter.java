package dal.fs;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;

import model.MyConfiguration;
import utility.FileNameBuilder;

public class FileSystemWriter {

	private List<JSONObject> outputJsonList;
	private MyConfiguration myConfig;

	public FileSystemWriter(List<JSONObject> outputJsonList, MyConfiguration config) {
		this.outputJsonList = outputJsonList;
		this.myConfig = config;
	}

	public void writeFiles() {
		Logger log = Logger.getLogger("writeFiles");
		PrintWriter writer = null;

		for (JSONObject js : outputJsonList) {
			FileNameBuilder fnbuilder = new FileNameBuilder(js, myConfig);
			fnbuilder.buildName();
			String fileName = fnbuilder.getName();
			
			// lacchezzo long->date
			Date date = new Date(js.getLong("date"));
			js.remove("date");
			js.put("date", date);
			
			try {
				writer = new PrintWriter(fileName, "UTF-8");
				writer.print(js.toString(1));
				log.info("Scrittura completata: " + fileName);
			} catch (FileNotFoundException e) {
				log.info("Problemi in scrittura: " + fileName);
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				log.info("Problemi generici: " + fileName);
				e.printStackTrace();
			}
			writer.close();
		}
	}
}
