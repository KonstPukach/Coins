using System;
using System.Collections.Generic;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using CoinsServer.Models;

namespace CoinsServer.Services
{
    public class MarketService : MainService
    {
        private readonly CoinsService _coinsService = new CoinsService();

        public async Task<GlobalData> GetGlobalData()
        {
            var response = await GetCoinsMarketApiResponse(GlobalConfig.CoinMarketApiGlobalEndpoint);
            if (!response.IsSuccessStatusCode)
                return null;

            var responseString = await response.Content.ReadAsStringAsync();
            var responseObject = JObject.Parse(responseString);
            return GlobalData.ParseJson(responseObject);
        }

        public async Task<IList<CapShare>> GetPieCapShare(int sharesCount)
        {
            var globalData = await GetGlobalData();
            var coins = await _coinsService.GetCoins(limit: sharesCount);
            var shares = new List<CapShare>();
            var otherShare = 1m;

            foreach (var coin in coins)
            {
                if (!coin.MarketCapUsd.HasValue)
                {
                    continue;
                }
                var currentShare = Math.Round(coin.MarketCapUsd.Value / globalData.TotalMarketCapUsd, 4);
                otherShare -= currentShare;
                AddCapShare(shares, coin, currentShare);
            }
            return AddOtherCapShare(shares, otherShare);
        }

        private void AddCapShare(IList<CapShare> shares, Coin coin, decimal sharePercentage)
        {
            var share = new CapShare
            {
                CoinId = coin.Id,
                Name = coin.Name,
                Share = sharePercentage
            };
            shares.Add(share);
        }

        private IList<CapShare> AddOtherCapShare(IList<CapShare> shares, decimal otherShare)
        {
            var share = new CapShare
            {
                CoinId = "Other",
                Name = "Other",
                Share = otherShare
            };
            shares.Add(share);
            return shares;
        }
    }
}