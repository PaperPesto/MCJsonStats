package ui;

import java.util.List;

import org.json.JSONObject;

import dal.repository.StatisticaRepository;
import model.MyConfiguration;
import model.StatisticaDTO;

public class InsertOnlyNewStatsController {

	private MyConfiguration config;
	private List<JSONObject> jsonlist;
	private List<StatisticaDTO> oldstats;
	
	public InsertOnlyNewStatsController(MyConfiguration config, List<JSONObject> jsonlist, List<StatisticaDTO> oldstats) {
		this.config = config;
		this.jsonlist = jsonlist;
		this.oldstats = oldstats;
	}
	
	public void execute() {
		StatisticaRepository statrepo = new StatisticaRepository(config);
		statrepo.insertOnlyNewStats(oldstats, jsonlist);
	}
}
