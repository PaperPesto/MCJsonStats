package dal.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

import dal.MongoClientConnection;
import model.MyConfiguration;
import model.PlayerDTO;

public class PlayerRepository extends AbstractRepository implements IPlayerRepository {

	public PlayerRepository(MyConfiguration config) {
		db = MongoClientConnection.getInstance(config).getDatabase(config.nameDB); // Setto la stringa di conn. da
																					// configurazione
		coll = db.getCollection(config.playersCollection);
	}

	public List<PlayerDTO> getAllPlayers() {

		Logger log = Logger.getLogger("PlayerRepository::getAllPlayers");

		List<PlayerDTO> giocatori = new ArrayList<PlayerDTO>();

		MongoCursor<Document> cursor = coll.find().iterator();

		while (cursor.hasNext()) {
			Document document = cursor.next();

			PlayerDTO player = new PlayerDTO();
			player.nome = document.getString("nome");
			player.cognome = document.getString("cognome");
			player.nickName = document.getString("nickname");
			player.uuid = document.getString("minecraft_uuid");

			giocatori.add(player);
		}
		cursor.close();

		return giocatori;
	}

	public PlayerDTO getPlayerByNome(String nome, String cognome) {

		Document document = coll.find(new Document("nome", nome).append("cognome", cognome)).first();

		PlayerDTO playerdto = new PlayerDTO();
		playerdto.nome = document.getString("nome");
		playerdto.cognome = document.getString("cognome");
		playerdto.nickName = document.getString("nickname");
		playerdto.uuid = document.getString("minecraft_uuid");

		return playerdto;
	}

	public PlayerDTO getPlayerByNickname(String nickname) {
		 Document document = coll.find(new Document("nickname", nickname)).first();
//		FindIterable<Document> document = coll.find(new Document());
//		Document myDoc = coll.find().first();

		PlayerDTO playerdto = new PlayerDTO();
		playerdto.nome = document.getString("nome");
		playerdto.cognome = document.getString("cognome");
		playerdto.nickName = document.getString("nickname");
		playerdto.uuid = document.getString("minecraft_uuid");

		return playerdto;
	}
}
