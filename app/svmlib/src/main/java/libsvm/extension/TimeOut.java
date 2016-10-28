package libsvm.extension;

public class TimeOut {
	private boolean flag = false;

	public boolean isTrue() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public TimeOut(boolean flag) {
		super();
		this.flag = flag;
	}
	
	
}
