package dal.repository;

import model.StatisticaDTO;

public interface IStatisticaRepository {

	// Insert
	
	// Read
	StatisticaDTO getLastStatisticaById(String id);
	
	// Update
	// Delete
}
