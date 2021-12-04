using System.Runtime.Serialization;
using Newtonsoft.Json;

namespace CoinsServer.Models
{
    [DataContract]
    public class CapShare
    {
        [DataMember(Name = "coin_id")]
        public string CoinId { get; set; }

        [DataMember(Name = "name")]
        public string Name { get; set; }

        [DataMember(Name = "share")]
        public decimal Share { get; set; }

        public static CapShare Parse(string json)
        {
            return JsonConvert.DeserializeObject<CapShare>(json);
        }
    }
}