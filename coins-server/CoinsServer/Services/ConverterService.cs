using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using Newtonsoft.Json;
using CoinsServer.Models;

namespace CoinsServer.Services
{
    public class ConverterService : MainService
    {
        private const string UsdName = "USD";

        private readonly CoinsService _coinsService = new CoinsService();

        public async Task<Rates> GetRates(int limit)
        {
            return new Rates
            {
                CryptoCoins = await GetCryptoCurrencies(limit),
                Currencies = await GetMonetaryCurrencies()
            };
        }

        public async Task<IList<Currency>> GetCryptoCurrencies(int limit)
        {
            var coins = await _coinsService.GetCoins(limit: limit);
            return coins?.Where(c => c.PriceUsd.HasValue && c.PriceUsd.Value > 0)
                         .Select(ParseCoinToCurrency).ToList();
        }

        private Currency ParseCoinToCurrency(Coin coin)
        {
            return new Currency
            {
                CurrencyId = coin.CoinId,
                Name = coin.Name,
                Rate = coin.PriceUsd.HasValue ? 1 / coin.PriceUsd : 0
            };
        }

        public async Task<IList<Currency>> GetMonetaryCurrencies()
        {
            using (var db = new CoinsContext())
            {
                var result = await db.MonetaryCurrencies.ToListAsync();
                if (result.Any())
                {
                    return result;
                }
                result = await GetMonetaryCurrenciesExternal();
                db.MonetaryCurrencies.AddRange(result);
                await db.SaveChangesAsync();

                return result;
            }
        }

        private async Task<List<Currency>> GetMonetaryCurrenciesExternal()
        {
            var response = await GetCoinsMarketApiResponse(GlobalConfig.EurMonetaryCurrenciesUri);
            if (response.IsSuccessStatusCode)
            {
                return ParseCurrencies(await response.Content.ReadAsStringAsync());
            }

            return new List<Currency> { GetDefaultCurrency() };
        }

        private List<Currency> ParseCurrencies(string jsonString)
        {
            var monetaryCurrencies = JsonConvert.DeserializeObject<MonetaryCurrencies>(jsonString);
            if (!monetaryCurrencies.Success || !monetaryCurrencies.Rates.ContainsKey(UsdName))
            {
                return new List<Currency> {GetDefaultCurrency()};
            }

            var usdRate = monetaryCurrencies.Rates[UsdName];
            return monetaryCurrencies.Rates.Select(rate => GetCurrency(rate.Key, rate.Value, usdRate)).ToList();
        }

        private Currency GetCurrency(string name, decimal rate, decimal usdRate)
        {
            return new Currency
            {
                CurrencyId = name,
                Name = name,
                Rate = rate / usdRate
            };
        }

        private Currency GetDefaultCurrency() => new Currency
        {
            CurrencyId = UsdName,
            Name = UsdName,
            Rate = 1
        };
    }
}