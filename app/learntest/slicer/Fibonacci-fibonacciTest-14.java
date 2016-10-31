	public int fibonacciTest(int number) {

		if (number < 0) {
			return -1;
		}

		if (number == 0) {
			return 0;
		}else if (number == 1) {
			return 1;
		}else {
			int[] fibs = new int[number + 1];
			fibs[0] = 0;
			fibs[1] = 1;

			// populate fibs with fib sequence
			for (int i = 2; i <= number; i++) {
				fibs[i] = fibs[i - 1] + fibs[i - 2];
			}

			return fibs[number];
		}

	}