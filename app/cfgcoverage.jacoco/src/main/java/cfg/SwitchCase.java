package cfg;

/**
 * @author LLT
 *
 */
public class SwitchCase {
	private boolean isDefault;
	private int keyValue;
	private CfgNode then;
	private CfgNode switchNode;

	public SwitchCase(CfgNode switchNode, int key, CfgNode thenNode) {
		this.keyValue = key;
		this.then = thenNode;
		this.switchNode = switchNode;
	}
	
	public SwitchCase(CfgNode switchNode, CfgNode thenNode) {
		isDefault = true;
		this.then = thenNode;
		this.switchNode = switchNode;
	}

	public int getKeyValue() {
		return keyValue;
	}

	public CfgNode getThen() {
		return then;
	}

	public CfgNode getSwitchNode() {
		return switchNode;
	}

	public boolean isDefault() {
		return isDefault;
	}
}
