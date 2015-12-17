using FluentMigrator.Builders.Create.Table;

namespace Lombard.Data.Job.Migrator
{
    internal static class FluentMigratorExtensions
    {
        public static ICreateTableColumnOptionOrWithColumnSyntax AsMaxString(this ICreateTableColumnAsTypeSyntax createTableColumnAsTypeSyntax)
        {
            return createTableColumnAsTypeSyntax.AsString(int.MaxValue);
        }
    }
}
