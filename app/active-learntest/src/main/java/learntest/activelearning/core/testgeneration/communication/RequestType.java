package learntest.activelearning.core.testgeneration.communication;

/**
 * @author LLT
 *
 */
public enum RequestType {
	$TRAINING,
	$BOUNDARY_REMAINING,
	$INPUT_OF_BRANCH, 
	$TRAINING_FINISH,
	$REQUEST_LABEL,
	$RECEIVED, 
	$SEND_LABEL,
	$BOUNDARY_EXPLORATION,
	$MODEL_CHECK, 
	$EXPLORATION_FINISH, 
	$REQUEST_MASK_RESULT, 
	$SEND_MASK_RESULT
}
