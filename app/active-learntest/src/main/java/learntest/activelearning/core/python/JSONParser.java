package learntest.activelearning.core.python;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import sav.strategies.dto.execute.value.ExecVar;
import sav.strategies.dto.execute.value.ExecVarType;

public class JSONParser {
	
	
	public static DataPoints parseUnlabeledDataPoints(String jsonStr) {
		List<ExecVar> varList = new ArrayList<>();
		List<double[]> values = new ArrayList<>();
		
		JSONArray array = new JSONArray(jsonStr);
		for(int i=0; i<array.length(); i++){
			JSONArray obj = (JSONArray) array.get(i);
			double[] value = new double[obj.length()];
			for(int j=0; j<obj.length(); j++){
				JSONObject input = (JSONObject) obj.get(j);
				value[j] = input.getInt("VALUE");
				
				if(i==0){
					//TODO
					String typeStr = input.getString("TYPE");
					String name = input.getString("NAME");
					varList.add(new ExecVar(name, ExecVarType.INTEGER));
				}
			}
			values.add(value);
		}
		
		return new DataPoints(varList, values);
	}
}
