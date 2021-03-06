using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Runtime.Serialization;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace CoinsServer.Models
{
    [DataContract]
    public class Coin
    {
        [Key]
        [DatabaseGenerated(DatabaseGeneratedOption.None)]
        [DataMember(Name = "id")]
        public string Id { get; set; }

        [Required]
        [DataMember(Name = "name")]
        public string Name { get; set; }

        [Required]
        [DataMember(Name = "icon_url")]
        public string IconUrl { get; set; }

        [Required]
        [DataMember(Name = "symbol")]
        public string Symbol { get; set; }

        [Required]
        [DataMember(Name = "rank")]
        public int Rank { get; set; }

        [Required]
        [DataMember(Name = "price_usd")]
        public decimal? PriceUsd { get; set; }

        [Required]
        [DataMember(Name = "price_btc")]
        public decimal? PriceBtc { get; set; }

        [DataMember(Name = "24h_volume_usd")]
        public decimal? VolumeUsd24H { get; set; }

        [DataMember(Name = "market_cap_usd")]
        public decimal? MarketCapUsd { get; set; }

        [DataMember(Name = "available_supply")]
        public decimal? AvailableSupply { get; set; }

        [DataMember(Name = "total_supply")]
        public decimal? TotalSupply { get; set; }

        [DataMember(Name = "max_supply")]
        public decimal? MaxSupply { get; set; }

        [DataMember(Name = "percent_change_1h")]
        public double? PercentChange1H { get; set; }

        [DataMember(Name = "percent_change_24h")]
        public double? PercentChange24H { get; set; }

        [DataMember(Name = "percent_change_7d")]
        public double? PercentChange7D { get; set; }

        [DataMember(Name = "last_updated")]
        public int? LastUpdated { get; set; }

        public ICollection<Alert> Alerts { get; set; }
        public ICollection<FavoriteCoin> FavoriteCoins { get; set; }

        public static IList<Coin> Parse(string jsonCoin)
        {
            return JsonConvert.DeserializeObject<IList<Coin>>(jsonCoin);
        } 

        public static Coin ParseJson(JToken token)
        {
            var usdQuote = token["quote"]["USD"];

            var priceUsd = ParseDecimal(usdQuote["price"].ToString());
            var priceBtc = token["id"].ToString() == "1" ? priceUsd : GlobalCache.GetBtcRate();
            var coin = new Coin();

            coin.Id = token["id"].ToString();
            coin.Name = token["name"].ToString();
            coin.Symbol = token["symbol"].ToString();
            coin.IconUrl = $"{GlobalConfig.CoinIconApiUrl}{coin.Id}.png";
            coin.PriceUsd = ParseDecimal(usdQuote["price"].ToString());
            coin.PriceBtc = priceUsd / priceBtc;
            coin.VolumeUsd24H = ParseDecimal(usdQuote["volume_24h"].ToString());
            coin.MarketCapUsd = ParseDecimal(usdQuote["market_cap"].ToString());
            coin.TotalSupply = ParseDecimal(token["total_supply"].ToString());
            coin.AvailableSupply = ParseDecimal(token["circulating_supply"].ToString());
            coin.MaxSupply = ParseDecimal(token["max_supply"].ToString());
            coin.PercentChange1H = ParseDouble(usdQuote["percent_change_1h"].ToString());
            coin.PercentChange24H = ParseDouble(usdQuote["percent_change_24h"].ToString());
            coin.PercentChange7D = ParseDouble(usdQuote["percent_change_7d"].ToString());
            coin.LastUpdated = (int)DateTimeOffset.Parse(token["last_updated"].ToString()).ToUnixTimeSeconds();
            
            return coin;
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
    }
}