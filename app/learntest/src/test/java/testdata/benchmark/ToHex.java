package testdata.benchmark;

public class ToHex {

	public static void main(String[] args) {
		new ToHex().toHexTest(233);
	}
	
	public String toHexTest(int value) {
		if (value < 0) {
			return "-1";
		}
		
		if (value == 0) {
			return "0";
		}

		StringBuilder sb = new StringBuilder();
		while (value > 0) {
			int result = value % 16;
			if (result < 10) {
				sb.append(result);
			} else {
				sb.append(GetHexSymbol(result));
			}

			value /= 16;
		}

		return sb.reverse().toString();
	}

	private char GetHexSymbol(int result) {
		char symbol = ' ';

		// match relevent symbol with result
		switch (result) {
		case 10:
			symbol = 'A';
			break;
		case 11:
			symbol = 'B';
			break;
		case 12:
			symbol = 'C';
			break;
		case 13:
			symbol = 'D';
			break;
		case 14:
			symbol = 'E';
			break;
		case 15:
			symbol = 'F';
			break;
		}

		return symbol;
	}
}
