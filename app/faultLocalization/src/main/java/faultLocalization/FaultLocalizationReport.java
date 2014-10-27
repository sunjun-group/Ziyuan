package faultLocalization;

import java.util.List;

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
		return lineCoverageInfos;
	}

	public void setLineCoverageInfos(List<LineCoverageInfo> lineCoverageInfos) {
		this.lineCoverageInfos = lineCoverageInfos;
	}

}
