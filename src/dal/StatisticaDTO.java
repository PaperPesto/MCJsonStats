package dal;

import java.util.Date;

import org.bson.types.ObjectId;
import org.json.JSONObject;

public class StatisticaDTO {
	// Contiene solo i metadati, il json intero delle statistiche è complicato da parsare dal BSON
	public ObjectId _id;
	public Date date;
	public String uuid;
	public int count;
}
