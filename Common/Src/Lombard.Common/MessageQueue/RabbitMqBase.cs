using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Sockets;
using System.Threading;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using RabbitMQ.Client.Exceptions;
using Serilog;

namespace Lombard.Common.MessageQueue
{
    /// <summary>
    ///
    /// </summary>
    public class RabbitMqBase : IDisposable
    {
        private readonly IList<string> hostNames;
        private readonly string userName;
        private readonly string password;
        private readonly int timeout;
        private readonly int heartbeat;
        private readonly bool automaticRecoveryEnabled;
        protected bool WaitingToConnect;

        private IConnectionFactory factory;
        protected IConnection Connection;
        protected IModel Model;

        public event EventHandler<ShutdownEventArgs> ConnectionLost;

        public RabbitMqBase(string hostNames, string userName, string password, int timeout, int heartbeatSeconds, bool automaticRecoverEnabled)
        {
            this.hostNames = hostNames.Split(',').ToList();
            this.userName = userName;
            this.password = password;
            this.timeout = timeout;
            this.automaticRecoveryEnabled = automaticRecoverEnabled;
            heartbeat = heartbeatSeconds;
        }

        protected void TryCreateConnectionFactory(object stateInfo)
        {
            if (Connection != null) Connection.Dispose();
            Connection = null;

            if (stateInfo != null)
            {
                Log.Information("Retrying connection");
                var autoEvent = (AutoResetEvent)stateInfo;
                autoEvent.Set();
            }
            var tempHostNames = hostNames;
            //hostNames.Shuffle();
            foreach (var hostName in tempHostNames)
            {
                try
                {
                    factory = new ConnectionFactory
                    {
                        HostName = hostName,
                        UserName = userName,
                        Password = password,
                        NetworkRecoveryInterval = TimeSpan.FromMilliseconds(timeout),
                        AutomaticRecoveryEnabled = automaticRecoveryEnabled,
                        RequestedHeartbeat = (ushort) heartbeat,
                        RequestedConnectionTimeout = 60000
                    };
                    Connection = factory.CreateConnection();
                    Log.Information(
                        "Connection heartbeat is configured to {0} seconds,  and the connection has returned a value of {1} seconds",
                        heartbeat, Connection.Heartbeat);
                    Connection.ConnectionShutdown += connection_ConnectionShutdown;
                    WaitingToConnect = false;
                    break;
                }
                catch (SocketException se)
                {
                    Log.Error(se, "RabbitMq: TryCreateConnectionFactory has an Error creating connection to host {0}",
                        hostName);
                }
                catch (BrokerUnreachableException be)
                {
                    Log.Error(be, "RabbitMq: TryCreateConnectionFactory Cannot reach broker for host {0}", hostName);
                }
                catch (Exception ex)
                {
                    Log.Error(ex, "RabbitMq: TryCreateConnectionFactory has an Error with {0} - {1}", hostName,
                        ex.Message);
                }
                finally
                {
                    if (Connection != null) Connection.Close();
                }
            }
            if (Connection == null) StartTryToConnect();
        }

        private void connection_ConnectionShutdown(object sender, ShutdownEventArgs reason)
        {
            Log.Warning("Connection lost to RabbitMQ Server due to {0}", reason.Cause);
            var connectionLost = ConnectionLost;
            if (connectionLost != null) connectionLost(sender, reason);
            if (Connection == null) StartTryToConnect();
        }

        private void StartTryToConnect()
        {
            const string compare = "Milliseconds";
            WaitingToConnect = true;
            var autoEvent = new AutoResetEvent(false);
            var timerDelegate = new TimerCallback(TryCreateConnectionFactory);
            var timer = new Timer(timerDelegate, autoEvent, TimeSpan.FromMilliseconds(timeout), TimeSpan.Zero);
            timer.Change(timeout, Timeout.Infinite);
            const string template = "RabbitMq: StartTryToConnect() - Failed to connect to RabbitMQ Cluster.  Will attempt to reconnect after {0} {1}";
            Log.Warning(template, timeout, compare);
        }

        public void Dispose()
        {
            Dispose(true);
            GC.SuppressFinalize(this);
        }

        protected virtual void Dispose(bool disposing)
        {
            if (!disposing) return;
            if (Model != null)
            {
                if (Model.IsOpen) Model.Abort();
                Model.Dispose();
            }
            if (Connection == null) return;
            if (Connection.IsOpen) Connection.Close();
            Connection.Dispose();
        }
    }
}
