using System;
using System.Collections.Generic;
using System.Runtime.Serialization;

namespace CoinsServer.Models
{

    [DataContract]
    public class MonetaryCurrencies
    {
        [DataMember(Name = "success")]
        public bool Success { get; set; }

        [DataMember(Name = "base")]
        public string Base { get; set; }

        [DataMember(Name = "date")]
        public DateTime Date { get; set; }

        [DataMember(Name = "rates")]
        public Dictionary<string, decimal> Rates { get; set; }
    }
}