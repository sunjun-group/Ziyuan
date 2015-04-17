package mutation.mutator;

import java.io.File;
import java.util.List;
import java.util.Map;

import sav.strategies.dto.ClassLocation;

/**
 * Created by hoangtung on 4/9/15.
 */

public class MutationResult {
	private Map<ClassLocation, List<LineMutationResult>> locationMap;

	public Map<ClassLocation, List<LineMutationResult>> getLocationMap() {
		return locationMap;
	}

	public void setLocationMap(
			Map<ClassLocation, List<LineMutationResult>> locationMap) {
		this.locationMap = locationMap;
	}

	public static class LineMutationResult {
		private List<ClassLocation> mappedLocations;
		private ClassLocation addedLine;
		private File mutatedFile;

		public List<ClassLocation> getMappedLocations() {
			return mappedLocations;
		}

		public void setMappedLocations(List<ClassLocation> mappedLocations) {
			this.mappedLocations = mappedLocations;
		}

		public ClassLocation getAddedLine() {
			return addedLine;
		}

		public void setAddedLine(ClassLocation addedLine) {
			this.addedLine = addedLine;
		}

		public File getMutatedFile() {
			return mutatedFile;
		}

		public void setMutatedFile(File mutatedFile) {
			this.mutatedFile = mutatedFile;
		}
	}

}
