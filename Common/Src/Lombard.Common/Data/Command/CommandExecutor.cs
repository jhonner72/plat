using System.Threading.Tasks;
using Autofac;

namespace Lombard.Common.Data.Command
{
    public class CommandExecutor : ICommandExecutor
    {
        private readonly ILifetimeScope currentScope;

        public CommandExecutor(ILifetimeScope currentScope)
        {
            this.currentScope = currentScope;
        }

        public Task ExecuteAsync<T>(T command) where T : ICommand
        {
            var handler = currentScope.Resolve<ICommandHandler<T>>();

            return handler.ExecuteAsync(command);
        }
    }
}