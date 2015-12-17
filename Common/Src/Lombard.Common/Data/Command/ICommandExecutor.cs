using System.Threading.Tasks;

namespace Lombard.Common.Data.Command
{
    public interface ICommandExecutor
    {
        Task ExecuteAsync<TCommand>(TCommand command) where TCommand : ICommand;
    }
}