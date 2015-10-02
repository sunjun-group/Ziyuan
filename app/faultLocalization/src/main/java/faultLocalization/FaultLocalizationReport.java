package faultLocalization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sav.common.core.utils.CollectionUtils;
import sav.strategies.dto.ClassLocation;
import faultLocalization.LineCoverageInfo.LineCoverageInfoComparator;

public class FaultLocalizationReport {

	private CoverageReport coverageReport;
	private List<LineCoverageInfo> lineCoverageInfos;

	public CoverageReport getCoverageReport() {
		return coverageReport;
	}

	public void setCoverageReport(CoverageReport coverageReport) {
		this.coverageReport = coverageReport;
	}

	public List<LineCoverageInfo> getLineCoverageInfos() {
		return CollectionUtils.initIfEmpty(lineCoverageInfos);
	}

	public void setLineCoverageInfos(List<LineCoverageInfo> lineCoverageInfos) {
		this.lineCoverageInfos = lineCoverageInfos;
	}
	
	public List<LineCoverageInfo> getFirstRanks(int rank){
		List<LineCoverageInfo> result = new ArrayList<LineCoverageInfo>();
		
		int size = lineCoverageInfos.size();
		int run = 0;
		while(rank > 0 && run < size){
			//add linecoverage for current rank
			double maxSuspiciousness = lineCoverageInfos.get(run).getSuspiciousness();
			/*
			 *  if result is empty, add to the result event the suspiciousness = 0
			 *  TODO: TO FIND A SOLUTION FOR THIS.
			 *  THIS HAPPENDS BECAUSE JACOCO CAN IGNORE SOME LINE RIGHT BEFORE THE PLACE 
			 *  WHERE THE EXCEPTION OCCUR INSIDE THE PROGRAM.
			 */
			if (maxSuspiciousness <= 0 && !result.isEmpty()) {
				break;
			}
			int i ;
			for(i = run; i < size && Double.compare(lineCoverageInfos.get(i).getSuspiciousness(), maxSuspiciousness) == 0; i++){
				result.add(lineCoverageInfos.get(i));
			}
			run = i;		
			
			rank--;
			
		}
		
		return result;
	}
	
	public List<ClassLocation> getFirstRanksLocation(int rank){
		List<LineCoverageInfo> lineCoverateInfos = getFirstRanks(rank);
		
		List<ClassLocation> result = new ArrayList<ClassLocation>();
		for(LineCoverageInfo lineCoverateInfo: lineCoverateInfos){
			result.add(lineCoverateInfo.getLocation());
		}
		
		return result;
	}
	
	public void sort(){
		Collections.sort(lineCoverageInfos, new LineCoverageInfoComparator());
	}
	
	public void setSuspiciousnessForAll(double value){
		for(LineCoverageInfo lineInfo: lineCoverageInfos){
			lineInfo.setSuspiciousness(value);
		}
	}

	@Override
	public String toString() {
		return lineCoverageInfos.toString();
	}

}
