using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using CoinsServer.Models;

namespace CoinsServer.Services
{
    public class CoinsService : MainService
    {
        public async Task<IList<Coin>> GetCoins(int start = 1, int limit = 200)
        {
            var response = await GetCoinsMarketApiResponse($"{GlobalConfig.CoinMarketApiListingsEndpoint}?start={start}&limit={limit}");
            if (!response.IsSuccessStatusCode)
            {
                return null;
            }
            var responseString = await response.Content.ReadAsStringAsync();
            var responseObject = JObject.Parse(responseString);
            var coinsData = (JArray)responseObject["data"];
            IList<Coin> coins = new List<Coin>();
            foreach (var coinData in coinsData)
            {
                var coin = Coin.ParseJson(coinData);
                coins.Add(coin);
            }
            return coins;
        }

        public async Task<Coin> GetCoin(string id)
        {
            var response = await GetCoinsMarketApiResponse($"{GlobalConfig.CoinMarketApiQuotesEndpoint}?id={id}");
            if (!response.IsSuccessStatusCode)
            {
                return null;
            }

            var responseString = await response.Content.ReadAsStringAsync();
            var responseObject = JObject.Parse(responseString);
            var coinData = responseObject["data"][id];
            var coin = Coin.ParseJson(coinData);

            return coin;
        }

        public async Task<string> GetCurrencyHistory(string coinId, string period)
        {
            if (!GlobalConfig.RouteByChartTimeFilter.ContainsKey(period))
            {
                return null;
            }
            var chartTimeFilter = GlobalConfig.RouteByChartTimeFilter[period];
            var response = await GetApiResponse(GetCurrencyHistoryUrl(coinId, chartTimeFilter));
            return response.IsSuccessStatusCode ? GetCurrencyHistoryJson(await response.Content.ReadAsStringAsync()) : null;
        }

        private string GetCurrencyHistoryJson(string jsonString)
        {
            var historyJson = JObject.Parse(jsonString);
            var valuesJson = historyJson["data"]["points"];
            var values = valuesJson.ToObject<Dictionary<int, Dictionary<string, List<decimal>>>>();
            var pricesArray = new List<List<decimal>>();
            var keys = values.Keys.ToList();
            keys.Sort();
            foreach (var key in keys)
            {
                pricesArray.Add(new List<decimal>() { key, values[key]["v"][0] });
            }
            return JsonConvert.SerializeObject(pricesArray);
        }

        private string GetCurrencyHistoryUrl(string coinId, ChartTimeFilter period)
        {
            string periodString = "";
            switch(period)
            {
                case ChartTimeFilter.Day:
                    periodString = "1D";
                    break;
                case ChartTimeFilter.Week:
                    periodString = "7D";
                    break;
                case ChartTimeFilter.Month:
                    periodString = "1M";
                    break;
                case ChartTimeFilter.Year:
                    periodString = "1Y";
                    break;
                case ChartTimeFilter.All:
                    periodString = "ALL";
                    break;
            };
            return $"{GlobalConfig.CoinMarketApiCoinCurrencyHistory}?id={coinId}&range={periodString}";
        }
    }
}