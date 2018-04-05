package dal.repository;

import dal.StatisticaDTO;

public interface IStatisticaRepository {

	// Insert
	
	// Read
	StatisticaDTO getLastStatisticaById(String id);
	
	// Update
	// Delete
}
