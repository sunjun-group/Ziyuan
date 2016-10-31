package testdata.experiment;

import java.util.LinkedList;
import java.util.Queue;

public class Question7 {
	
	public static void main(String[] args) {
		new Question7().kth(2);
	}

	public long kth(int k) {
		// write implementation here
		Queue<Long> three = new LinkedList<Long>();
		Queue<Long> five = new LinkedList<Long>();
		Queue<Long> seven = new LinkedList<Long>();
		three.add(3l);
		five.add(5l);
		seven.add(7l);

		int c = 0;
		long kth = 0;
		while (c++ < k) {
			kth = Math.min(three.peek(), Math.min(five.peek(), seven.peek()));
			if (kth == three.peek()) {
				System.out.println("case 1");
				three.poll();
				three.add(3 * kth);
				five.add(5 * kth);
			} else if (kth == five.peek()) {
				System.out.println("case 2");
				five.poll();
				five.add(5 * kth);
			} else if (kth == seven.peek()) {
				System.out.println("case 3");
				seven.poll();
			}
			seven.add(7 * kth);
		}
		return kth;
	}

}
