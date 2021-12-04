using System;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Threading.Tasks;
using CoinsServer.Models;

namespace CoinsServer.Services
{
    public class MainService
    {
        public async Task<HttpResponseMessage> GetCoinsMarketApiResponse(string requestUri)
        {
            return await GetJsonResponse(GlobalConfig.CoinMarketApiUri, requestUri, true);
        }

        public async Task<HttpResponseMessage> GetApiResponse(string requestUri)
        {
            return await GetJsonResponse("", requestUri, false);
        }

        public async Task<HttpResponseMessage> GetJsonResponse(string baseUri, string endpoint, bool useCoinsMarketApiKey = false)
        {
            using (var client = new HttpClient())
            {
                if (!String.IsNullOrEmpty(baseUri))
                    client.BaseAddress = new Uri(baseUri);
                client.DefaultRequestHeaders.Accept.Clear();
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
                if (useCoinsMarketApiKey)
                    client.DefaultRequestHeaders.Add("X-CMC_PRO_API_KEY", GlobalConfig.CoinMarketApiKey);
                client.DefaultRequestHeaders.Add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Safari/537.36");
                return await client.GetAsync(endpoint);
            }
        }

        protected long GetPeriodLeftBorder(ChartTimeFilter period)
        {
            const int periodDelta = -1;
            switch (period)
            {
                case ChartTimeFilter.Day:
                    return GetUnixTimestamp(DateTime.UtcNow.AddDays(periodDelta));
                case ChartTimeFilter.Week:
                    return GetUnixTimestamp(DateTime.UtcNow.AddDays(periodDelta * 7));
                case ChartTimeFilter.Month:
                    return GetUnixTimestamp(DateTime.UtcNow.AddMonths(periodDelta));
                case ChartTimeFilter.Year:
                    return GetUnixTimestamp(DateTime.UtcNow.AddYears(periodDelta));
                case ChartTimeFilter.All:
                    return 0;
                default:
                    return 0;
            }
        }

        protected long GetUnixTimestamp(DateTime time)
        {
            return (long)(time - new DateTime(1970, 1, 1)).TotalMilliseconds;
        }
    }
}