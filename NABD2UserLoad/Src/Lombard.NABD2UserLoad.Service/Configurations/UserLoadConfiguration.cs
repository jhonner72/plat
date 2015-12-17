using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Lombard.NABD2UserLoad.Service.Configurations
{
    /// <summary>
    /// Class provides application configuration values
    /// </summary>
    public sealed class UserLoadConfiguration
    {
        #region App setting keys

       /// <summary>
        /// AppConfig class instance - following singleton pattern
        /// </summary>
        private static UserLoadConfiguration _settings;

        /// <summary>
        /// Object to lock the appconfig instance
        /// </summary>
        private static readonly object Padlock = new object();

        #endregion

        /// <summary>
        /// Static variable settings to instantiate appconfig class
        /// </summary>
        public static UserLoadConfiguration Settings
        {
            get
            {
                if (_settings == null)
                {
                    lock (Padlock)
                    {
                        if (_settings == null)
                        {
                            _settings = new UserLoadConfiguration();
                        }
                    }
                }
                return _settings;
            }
        }

        

        
        public int ActionHour
        {
            get
            {
                int v;
                int.TryParse(ConfigurationManager.AppSettings["userload:actionHour"], out v);
                return v;
            }
        }

        public int ActionMinute
        {
            get
            {
                int v;
                int.TryParse(ConfigurationManager.AppSettings["userload:actionMinute"], out v);
                return v;
            }
        }

        public string UserloadSourcePath
        {
            get { return ConfigurationManager.AppSettings["userload:sourcepath"]; }
        }
        public string UserloadArchivePath
        {
            get { return ConfigurationManager.AppSettings["userload:archivepath"]; }
        }
        public double UserloadInterval
        {
            get
            {
                int v;
                int.TryParse(ConfigurationManager.AppSettings["userload:interval"], out v);
                return v;
            }
        }
    }
}
