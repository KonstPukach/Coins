using System;
using System.Runtime.Serialization;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace CoinsServer.Models
{
    [DataContract]
    public class GlobalData
    {
        [DataMember(Name = "total_market_cap_usd")]
        public decimal TotalMarketCapUsd { get; set; }

        [DataMember(Name = "total_24h_volume_usd")]
        public decimal Total24HVolumeUsd { get; set; }

        [DataMember(Name = "bitcoin_percentage_of_market_cap")]
        public double BitcoinPercentageOfMarketCap { get; set; }

        [DataMember(Name = "active_currencies")]
        public long ActiveCurrencies { get; set; }

        [DataMember(Name = "active_assets")]
        public long ActiveAssets { get; set; }

        [DataMember(Name = "active_markets")]
        public decimal ActiveMarkets { get; set; }

        [DataMember(Name = "last_updated")]
        public int? LastUpdated { get; set; }

        public static GlobalData Parse(string json)
        {
            return JsonConvert.DeserializeObject<GlobalData>(json);
        }

        public static GlobalData ParseJson(JObject obj)
        {
            var data = new GlobalData();
            var objData = obj["data"];
            var usdQuote = objData["quote"]["USD"];

            data.TotalMarketCapUsd = ParseDecimal(usdQuote["total_market_cap"].ToString());
            data.Total24HVolumeUsd = ParseDecimal(usdQuote["total_volume_24h"].ToString());
            data.BitcoinPercentageOfMarketCap = ParseDouble(objData["btc_dominance"].ToString());
            data.ActiveCurrencies = ParseLong(objData["active_cryptocurrencies"].ToString());
            data.ActiveAssets = 0;
            data.ActiveMarkets = ParseDecimal(objData["active_market_pairs"].ToString());
            data.LastUpdated = (int)DateTimeOffset.Parse(objData["last_updated"].ToString()).ToUnixTimeSeconds();

            return data;
        }
        private static decimal ParseDecimal(string value)
        {
            decimal.TryParse(value, out decimal result);
            return result;
        }
        private static double ParseDouble(string value)
        {
            double.TryParse(value, out double result);
            return result;
        }
        private static long ParseLong(string value)
        {
            long.TryParse(value, out long result);
            return result;
        }
    }
}