using System.ComponentModel.DataAnnotations;
using System.Runtime.Serialization;

namespace CoinsServer.Models
{
    [DataContract]
    public class Currency
    {
        [Key]
        [DataMember(Name = "currency_id")]
        public string CurrencyId { get; set; }

        [DataMember(Name = "name")]
        public string Name { get; set; }

        [DataMember(Name = "rate")]
        public decimal? Rate { get; set; }
    }
}