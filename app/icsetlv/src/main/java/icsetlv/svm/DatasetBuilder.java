package icsetlv.svm;

import icsetlv.common.dto.BreakPoint;
import icsetlv.common.dto.VariablesExtractorResult.BreakpointResult;
import icsetlv.common.dto.VariablesExtractorResult.BreakpointValue;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

public class DatasetBuilder {
	
	private BreakpointResult bpr;
	private int lineNumber;
	private int featureSize;

	public DatasetBuilder(BreakpointResult bpr){
		this.bpr = bpr;
		this.lineNumber = bpr.getFailValues().size() + bpr.getPassValues().size();
		this.featureSize = bpr.getPassValues().get(0).getValues().size();
	}
	
	public BreakPoint getBreakPoint(){
		return this.bpr.getBreakpoint();
	} 
	
	public Dataset buildDataset(){
		Dataset ds = new DefaultDataset();
		for(BreakpointValue bp : bpr.getPassValues()){
			Instance instance = new DenseInstance(featureSize);
			for(int i = 0; i < featureSize; i++){
				instance.put(i, Double.parseDouble(bp.getValues().get(i).getValue()));
			}
			instance.setClassValue("positive");
			ds.add(instance);
		}
		
		for(BreakpointValue bp : bpr.getFailValues()){
			Instance instance = new DenseInstance(featureSize);
			for(int i = 0; i < featureSize; i++){
				instance.put(i, Double.parseDouble(bp.getValues().get(i).getValue()));
			}
			instance.setClassValue("negative");
			ds.add(instance);
		}
		return ds;
	}
	
	public int getFeatureSize(){
		return featureSize;
	}
	
	public int getLineNumber(){
		return lineNumber;
	}
}
