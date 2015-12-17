using Castle.Components.DictionaryAdapter;

namespace Lombard.Common.Configuration
{
    [KeyPrefix("dfs:")]
    [AppSettingWrapper]
    public interface IDfsConfiguration
    {
        string Repository { get; set; }
        string UserName { get; set; }
        string Password { get; set; }
        string ServiceUrl { get; set; }

        int MaxQueryResults { get; set; }

        string DishonourLetterPath { get; set; }
        int DishonourTimeWindowDays { get; set; }
    }
}
