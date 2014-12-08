package faultLocalization;

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

}
