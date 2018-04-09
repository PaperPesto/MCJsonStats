package dal.repository;

import java.util.List;

import model.PlayerDTO;

public interface IPlayerRepository {

	// Read
	PlayerDTO getPlayerByNome(String nome, String cognome);
	PlayerDTO getPlayerByNickname(String nickname);
	List<PlayerDTO> getAllPlayers();
}
