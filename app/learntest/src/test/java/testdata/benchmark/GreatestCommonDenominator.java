package testdata.benchmark;

public class GreatestCommonDenominator {

	public int GreatestCommonDenominatorTest(int first, int second)
    {
        return second == 0 ? first : GreatestCommonDenominatorTest(second, first % second);
    }
}
