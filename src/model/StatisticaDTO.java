package model;

import java.util.Date;

import org.bson.types.ObjectId;

// Classe mappata su DB
public class StatisticaDTO {
	public ObjectId _id;
	
	public String uuid;
	public Date date;
	public int count;
}
