using CoinsServer.Services;
using Hangfire;
using Microsoft.Owin;
using Owin;

[assembly: OwinStartup(typeof(CoinsServer.Startup))]

namespace CoinsServer
{
    public class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            GlobalConfiguration.Configuration.UseSqlServerStorage("CoinsContext");
            app.UseHangfireServer();
            var options = new DashboardOptions
            {
                Authorization = new[] { new HangfireAuthorizationFilter() }
            };
            app.UseHangfireDashboard("/hangfire", options);

            RecurringJob.AddOrUpdate("alerts-job", () => new AlertService().CheckAlerts(),
                GlobalConfig.AlertJobCronExpression);
            RecurringJob.AddOrUpdate("update-currencies-job", () => new ConverterService().UpdateCurrencies(),
                GlobalConfig.UpdateCurrenciesJobCronExpression);
        }
    }
}
