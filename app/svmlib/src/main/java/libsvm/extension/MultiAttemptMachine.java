package libsvm.extension;

import libsvm.core.Machine;

/**
 * The training algorithm for this Machine is as follows:<br/>
 * <ul>
 * <li>Run SVM algorithm on the current data set to find a divider</li>
 * <li>Find the collection of all points which are (A) and are not (B)
 * classified correctly using the divider</li>
 * <li>While (B) is not empty, do the following steps:</li>
 * <ul>
 * <li>Randomly select 1 point from (B), mix it with (A), then run SVM again on
 * this new data set.</li>
 * <li>Remove from (B) all points which are correctly classified using this new
 * divider</li>
 * <li>Merge this new divider with the existing divider</li>
 * </ul>
 * </ul> <br/>
 * Because it is not guaranteed that the algorithm will be able to find correct
 * divider for all cases, we limit the maximum number of the while loops by
 * using <code>maximumLoopCount</code>, which has the value of <code>10</code>
 * by default that can be altered using the constructor.
 * 
 * @author Nguyen Phuoc Nguong Phuc (npn)
 * 
 */
public class MultiAttemptMachine extends Machine {

	private final int maximumLoopCount;

	public MultiAttemptMachine() {
		this.maximumLoopCount = 10;
	}

	public MultiAttemptMachine(final int maximumLoopCount) {
		this.maximumLoopCount = maximumLoopCount;
	}

	@Override
	public Machine train() {
		// TODO NPN train on the current data set
		// Check which points are not classified correctly (A)
		// Randomly select 1 point from (A)

		return this;
	}

}
