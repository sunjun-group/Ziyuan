package learntest.activelearning.core.testgeneration.communication;

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
				ExecVarType varType = null;
				String type = input.getString("TYPE");
				if(type.equals(ExecVarType.BOOLEAN.toString())){
					value[j] = input.getInt("VALUE");
					varType = ExecVarType.BOOLEAN;
				}
				else if(type.equals(ExecVarType.BYTE.toString())){
					value[j] = input.getInt("VALUE");
					varType = ExecVarType.BYTE;
				}
				else if(type.equals(ExecVarType.INTEGER.toString())){
					value[j] = input.getInt("VALUE");
					varType = ExecVarType.INTEGER;
				}
				else if(type.equals(ExecVarType.CHAR.toString())){
					value[j] = input.getInt("VALUE");
					varType = ExecVarType.CHAR;
				}
				else if(type.equals(ExecVarType.DOUBLE.toString())){
					value[j] = input.getDouble("VALUE");
					varType = ExecVarType.DOUBLE;
				}
				else if(type.equals(ExecVarType.FLOAT.toString())){
					value[j] = input.getDouble("VALUE");
					varType = ExecVarType.FLOAT;
				}
				else if(type.equals(ExecVarType.LONG.toString())){
					value[j] = input.getInt("VALUE");
					varType = ExecVarType.LONG;
				}
				else if(type.equals(ExecVarType.SHORT.toString())){
					value[j] = input.getInt("VALUE");
					varType = ExecVarType.SHORT;
				}
				
				if(i==0){
					String name = input.getString("NAME");
					varList.add(new ExecVar(name, varType));
				}
			}
			values.add(value);
		}
		
		return new DataPoints(varList, values);
	}
}
