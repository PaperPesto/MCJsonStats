package ui;

import java.util.List;

import dal.repository.StatisticaRepository;
import model.MyConfiguration;
import model.StatisticaDTO;

public class LastStatisticsController {

	private MyConfiguration config;
	private List<StatisticaDTO> oldstats;
	
	public LastStatisticsController(MyConfiguration config) {
		this.config = config;
	}
	
	public void execute() {
		StatisticaRepository statrepo = new StatisticaRepository(config);
		statrepo.makeLastStatsCollection();
		oldstats = statrepo.getLastStatistics();
	}
	
	public List<StatisticaDTO> get(){
		return oldstats;
	}
}
