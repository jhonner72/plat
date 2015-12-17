using System;

namespace FujiXerox.Adapters.DipsAdapter.Debug
{
    public class ServiceWrapper : DipsService
    {
        public void Start(string[] args = null)
        {
            AppDomain.CurrentDomain.ProcessExit += ProcessExit;
            OnStart(args);
        }

        void ProcessExit(object sender, EventArgs e)
        {
            Console.WriteLine("exit");
            Console.ReadLine();
            OnStop();

        }
    }
}
