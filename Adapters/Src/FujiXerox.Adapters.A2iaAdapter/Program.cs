using System;
using System.ServiceProcess;
using FujiXerox.Adapters.A2iaAdapter;
using FujiXerox.Adapters.A2iaAdapter.Debug;

namespace FujiXerox.Adapters.A2iaAdapter
{
    static class Program
    {
        /// <summary>
        /// The main entry point for the application.
        /// </summary>
        private static void Main()
        {
#if(!DEBUG)
            ServiceBase[] ServicesToRun;
            ServicesToRun = new ServiceBase[] 
            { 
                new CarService() 
            };
            ServiceBase.Run(ServicesToRun);
#else
            var carService = new ServiceWrapper();
            carService.Start();
            //Console.WriteLine("Press Enter to stop");
            //Console.ReadLine();
#endif
        
        }
    }
}
