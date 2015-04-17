package mutation.parser;

/**
 * Created by hoangtung on 4/6/15.
 */
public class Tuple<T, T1, T2>
{
    public T first;
    public T1 second;
    public T2 third;

    public Tuple(T first, T1 second, T2 third)
    {
        this.first = first;
        this.second = second;
        this.third = third;
    }
}
