namespace Lombard
{
    public interface IMapper<TIn, TOut>
    {
        TOut Map(TIn input);
    }
}