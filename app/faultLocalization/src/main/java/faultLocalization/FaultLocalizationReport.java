package faultLocalization;

import java.util.ArrayList;
import java.util.List;

import sav.common.core.utils.CollectionUtils;

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
		return CollectionUtils.nullToEmpty(lineCoverageInfos);
	}

	public void setLineCoverageInfos(List<LineCoverageInfo> lineCoverageInfos) {
		this.lineCoverageInfos = lineCoverageInfos;
	}
	
	public List<LineCoverageInfo> getFirstRanks(int rank){
		List<LineCoverageInfo> result = new ArrayList<LineCoverageInfo>();
		int run = 0;
		
		int size = lineCoverageInfos.size();
		while(rank > 0 && run < size){
			//add linecoverage for current rank
			double maxSuspiciousness = lineCoverageInfos.get(run).getSuspiciousness();
			int i ;
			for(i = run; i < size && Double.compare(lineCoverageInfos.get(i).getSuspiciousness(), maxSuspiciousness) == 0; i++){
				result.add(lineCoverageInfos.get(i));
			}
			run = i;		
			
			rank--;
			
		}
		
		return result;
	}

}
