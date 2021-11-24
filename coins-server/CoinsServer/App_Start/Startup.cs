using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(CoinsServer.Startup))]

namespace CoinsServer
{
    public class Startup
    {
        public void Configuration(IAppBuilder app)
        {

        }
    }
}
