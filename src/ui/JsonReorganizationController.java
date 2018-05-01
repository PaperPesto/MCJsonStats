package ui;

import java.util.List;

import org.json.JSONObject;

import bl.JsonBusiness;
import model.MyConfiguration;
import model.StatisticaFS;

public class JsonReorganizationController {

	private MyConfiguration config;
	private List<StatisticaFS> newstats;
	private List<JSONObject> jsonlist;

	public JsonReorganizationController(MyConfiguration config, List<StatisticaFS> newstats) {
		this.config = config;
		this.newstats = newstats;
	}

	public void execute() {
		JsonBusiness business = new JsonBusiness(newstats, config);
		business.execute();
		jsonlist = business.getOutputJson();
	}
	
	public List<JSONObject> get(){
		return jsonlist;
	}
}
