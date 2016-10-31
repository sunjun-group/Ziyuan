	public static void test(int x) {
		int c=0;
		while(true) {
			if(x<=0) break;
			if(c==50) {
				assert false; // error 1
			}
			c=c+1;
			x=x-1;
		}
		if(c==30) {
			System.out.println("abort2");
			assert false; // error 2
		}
	}