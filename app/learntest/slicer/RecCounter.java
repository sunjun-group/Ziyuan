package testdata.numeric;

public class RecCounter {
	
	public int rec(int a) {
		if(a <= 0)
			return 0;
		int res = rec(a-1);
		int rescopy = res;
		System.out.println(rescopy);
		while(rescopy > 0)
			rescopy--;
		return 1 + res;
	}
	
	public static void main(String[] args) {
		new RecCounter().rec(23);
	}

}
