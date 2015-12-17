using FluentMigrator.Builders.Create.Table;

namespace Lombard.Data.Tracking.Migrator
{
    internal static class FluentMigratorExtensions
    {
        public static ICreateTableColumnOptionOrWithColumnSyntax AsMaxString(this ICreateTableColumnAsTypeSyntax createTableColumnAsTypeSyntax)
        {
            return createTableColumnAsTypeSyntax.AsString(int.MaxValue);
        }
    }
}
