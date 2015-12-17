using System;

namespace FujiXerox.Adapters.A2iaAdapter.Debug
{
    public class ServiceWrapper : CarService
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
