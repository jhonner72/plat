﻿namespace Lombard.ECLMatchingEngine.Service.Domain
{
    using System;
    using System.Linq;

    public static class ObjectExtensions
    {
        public static T GetAttributeFrom<T>(this object instance, string propertyName) where T : Attribute
        {
            var attrType = typeof(T);
            var property = instance.GetType().GetProperty(propertyName);
            return (T)property.GetCustomAttributes(attrType, false).First();
        }
    }
}
