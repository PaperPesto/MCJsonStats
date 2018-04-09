package bl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import model.StatisticaFS;
import model.MyConfiguration;

public class JsonBusiness {

	private List<StatisticaFS> inputJsonStringList; // Raw - serve per metadati (data, sourcefile, ecc)
	private MyConfiguration config;
	private List<JSONObject> outputJsonList; // Riorganizzato

	public List<JSONObject> getOutputJson() {
		return outputJsonList;
	}

	public JsonBusiness(List<StatisticaFS> inputJsonStringList, MyConfiguration config) {
		this.inputJsonStringList = inputJsonStringList;
		this.config = config;
		outputJsonList = new ArrayList<JSONObject>();
	}

	public void execute() {
		Logger log = Logger.getLogger("execute");
		
		for(StatisticaFS mj : inputJsonStringList) {
		
		JSONObject cookedJson = executeJsonReorganization(mj);
		
		JSONObject dateJson = new JSONObject();
		dateJson.put("date", new Date(mj.sourceFile.lastModified()));
		JSONObject uuidJson = new JSONObject();
		uuidJson.put("uuid", mj.sourceFile.getName().replace(".json", ""));
//		JSONObject sourceFileJson = new JSONObject();
//		sourceFileJson.put("sourceFile", inputMetaJson.metaDati.sourceFile.getAbsolutePath());
		
		try {
		cookedJson = deepMerge(cookedJson, dateJson);
		cookedJson = deepMerge(cookedJson, uuidJson);
//		cookedJson = deepMerge(cookedJson, sourceFileJson);
		} catch (Exception e) {
			log.warning("Errore nell'aggiunta dei metadati");
			throw e;
		}
		outputJsonList.add(cookedJson);
		}
	}
	

	private JSONObject executeJsonReorganization(StatisticaFS inputJsonString) {
		Logger log = Logger.getLogger("JsonReorganization");
		
		JSONObject rawJson = new JSONObject(inputJsonString.jsonString);
		List<JSONObject> rawJsonList = getJsonList(rawJson);
		JSONObject myJson = new JSONObject();
		
		try {
			for (JSONObject j : rawJsonList) {
				myJson = deepMerge(myJson, j);
			}
		} catch (Exception e) {
			log.warning("Errore nella riorganizzazione del json");
			throw e;
		}
		log.info("executeJsonReorganization success: " + rawJsonList.size() + " campi riorganizzati per l'uuid " + inputJsonString.uuid);
		return myJson;
	}

	private List<String> getStringPathFromDotString(String dotString) {
		List<String> strList = new ArrayList<String>();

		for (String s : dotString.split("\\.")) {
			strList.add(s);
		}
		strList = cleanStringList(strList);
		return strList;
	}

	private List<String> cleanStringList(List<String> strList) {
		// Implementazione molto semplice, tiene di conto solo del nome
		String stringForDeleting = config.nameKeyToAvoid;
		strList.removeIf(x -> x.equals(stringForDeleting));
		return strList;
	}

	private JSONObject getJSONFromGenericValue(String strkey, Object genericValue) throws Exception {
		JSONObject outputJson = null;

		if (genericValue.getClass() == Integer.class) {
			// value
			int value = (int) genericValue;
			// Buildo JSON = key + string
			JSONObject myJson = new JSONObject();
			myJson.put(strkey, value);
			outputJson = myJson;

		} else if (genericValue.getClass() == JSONObject.class) {
			// value
			JSONObject myJson = new JSONObject();
			JSONObject value = (JSONObject) genericValue;
			myJson.put(strkey, value);
			outputJson = myJson;

		} else if (genericValue instanceof String) {
			// value
			JSONObject myJson = new JSONObject();
			String value = (String) genericValue;
			myJson.put(strkey, value);
			outputJson = myJson;
		} else {
			throw new Exception(genericValue.toString() + ": tipo sconosciuto non ancora gestito");
		}
		return outputJson;
	}

	// - Core Business ---
	public JSONObject deepMerge(JSONObject target, JSONObject source) throws JSONException {
		for (String key : JSONObject.getNames(source)) {
			Object value = source.get(key);
			if (!target.has(key)) {
				// new value for "key":
				target.put(key, value);
			} else {
				// existing value for "key" - recursively deep merge:

				// --- Controllo nr.1
				// --- Nel caso si debba mergiare un primitivo dentro un json
				// target -> {"b":{"c":{"z":56,"k":45}}}
				// source -> {"b":4}
				if (!(value instanceof JSONObject) && target instanceof JSONObject) {
					JSONObject myJson = new JSONObject();
					myJson.put(config.defaultNameKey, value);
					value = myJson;
				}

				// --- Controllo nr.2
				// --- Nel caso si debba mergiare un json dentro un record primitivo
				// target -> {"stat":"drop":1,
				// {"mineBlock":{"minecraft":{"double_plant":1,"birch_fence":1}},...
				// source -> {"stat":{"drop":{"minecraft":{"wheat_seeds":1}}}}
				if (!(target.get(key) instanceof JSONObject) && value instanceof JSONObject) {
					JSONObject myJson = new JSONObject();
					myJson.put("innerValue", target.get(key));
					target.put(key, myJson); // Sovrascrive il vecchio record con un json ad hoc
				}

				if (value instanceof JSONObject) {
					JSONObject valueJson = (JSONObject) value;
					JSONObject targetJson = (JSONObject) target.getJSONObject(key);
					try {
						deepMerge(targetJson, valueJson);
					} catch (Exception e) {
						throw e;
					}
				} else {
					target.putOnce(key, value); // Con putOnce() non sovrascrive
				}
			}
		}
		return target;
	}

	private JSONObject makeJsonFromPath(String dotString, Object genericValue) {
		List<String> stringPath = getStringPathFromDotString(dotString);
		Collections.reverse(stringPath.subList(0, stringPath.size())); // Reverse della lista

		JSONObject myJson = null;
		;
		try {
			myJson = getJSONFromGenericValue(stringPath.get(0), genericValue);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (String s : stringPath.subList(1, stringPath.size())) {
			try {
				myJson = getJSONFromGenericValue(s, myJson);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return myJson;
	}

	private List<JSONObject> getJsonList(JSONObject json) {
		List<JSONObject> jsonList = new ArrayList<JSONObject>();

		for (String keystr : json.keySet()) {
			JSONObject myJson = makeJsonFromPath(keystr, json.get(keystr));
			jsonList.add(myJson);
		}
		return jsonList;
	}
}
