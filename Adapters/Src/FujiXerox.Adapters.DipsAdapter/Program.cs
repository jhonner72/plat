using System;
using System.ServiceProcess;
using FujiXerox.Adapters.DipsAdapter.Debug;

namespace FujiXerox.Adapters.DipsAdapter
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        static void Main()
        {
#if !DEBUG
            ServiceBase[] ServicesToRun;
            ServicesToRun = new ServiceBase[] 
            { 
                new DipsService() 
            };
            ServiceBase.Run(ServicesToRun);
#else
            var dipsService = new ServiceWrapper();
            dipsService.Start();
            //Console.WriteLine("Press Enter to stop");
            //Console.ReadLine();

#endif
        }
    }
}
