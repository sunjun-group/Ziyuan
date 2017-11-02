package learntest.core.rule;

public class NotEqualVarRelationShip implements RelationShip{
	private String left, right;
	
	public NotEqualVarRelationShip(String left, String right){
		this.left = left;
		this.right = right;
	}

	public String getLeft() {
		return left;
	}

	public void setLeft(String left) {
		this.left = left;
	}

	public String getRight() {
		return right;
	}

	public void setRight(String right) {
		this.right = right;
	}
	
	
}
