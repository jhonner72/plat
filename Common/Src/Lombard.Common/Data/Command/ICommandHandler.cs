using System.Threading.Tasks;

namespace Lombard.Common.Data.Command
{
    public interface ICommandHandler<in TCommand>
        where TCommand : ICommand
    {
        Task ExecuteAsync(TCommand command);
    }
}