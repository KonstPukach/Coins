using System;
using System.Threading.Tasks;
using CoinsServer.Services;

namespace CoinsServer
{
    public sealed class GlobalCache
    {
        private static decimal btcRate;
        private static DateTime lastUpdatedBtcRate;

        public static decimal GetBtcRate()
        {
            if (lastUpdatedBtcRate == null || lastUpdatedBtcRate.AddSeconds(60) < DateTime.UtcNow)
            {
                var coinsService = new CoinsService();
                btcRate = Task.Run(async () => await coinsService.GetCoin("1")).Result.PriceUsd.Value;
                lastUpdatedBtcRate = DateTime.UtcNow;
            }
            return btcRate;
        }
    }
}
