using System.Collections.Generic;
using System.Runtime.Serialization;

namespace CoinsServer.Models
{
    [DataContract]
    public class Rates
    {
        [DataMember(Name = "cryptocoins")]
        public IList<Currency> CryptoCoins { get; set; }

        [DataMember(Name = "currencies")]
        public IList<Currency> Currencies { get; set; }
    }
}