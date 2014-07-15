package testdata.test;
import java.util.Random;

public class Test {
	private static int stint = 0;
	int foo2;
	private NewType type = new NewType();

	public static void main(String[] args) {
		Random random = new Random();
		Test test1 = new Test(0, 0);
		test1.type.anotherType = new NewType();
		test1.localFunc(random);
	}
	
	public Test(int stint, int initFoo) {
		Test.stint = stint; 
		foo2 = initFoo;
	}
	
	public void execute() {
		Random random = new Random();
		type.anotherType = new NewType();
		localFunc(random);
	}

	private void localFunc(Random random) {
		for (int i = 0; i < 10; i++) {
			stint += 2;
			foo2 = random.nextInt();
		}
		assert stint + foo2 < 100;
	}
	
	public NewType getType() {
		return type;
	}

	public void setType(NewType type) {
		this.type = type;
	}

	public static class NewType {
		public int value = 1;
		public NewType anotherType;
	}
}
