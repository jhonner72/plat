using System;
using Castle.Components.DictionaryAdapter;

namespace Lombard.Common.Configuration
{
    public class AppSettingWrapperAttribute : DictionaryBehaviorAttribute, IPropertyDescriptorInitializer, IDictionaryPropertyGetter
    {
        public object GetPropertyValue(IDictionaryAdapter dictionaryAdapter, string key, object storedValue, PropertyDescriptor property, bool ifExists)
        {
            if (storedValue != null)
            {
                return storedValue;
            }
            
            throw new InvalidOperationException(string.Format("App setting '{0}' not found!", key));
        }

        public void Initialize(PropertyDescriptor propertyDescriptor, object[] behaviors)
        {
            propertyDescriptor.Fetch = true;
        } 
    }
}
