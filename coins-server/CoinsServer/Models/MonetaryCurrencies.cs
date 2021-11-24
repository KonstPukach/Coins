using System;
using System.Collections.Generic;
using System.Runtime.Serialization;

namespace CoinsServer.Models
{
    /// <summary>
    /// MonetaryCurrencies model
    /// </summary>
    [DataContract]
    public class MonetaryCurrencies
    {
        /// <summary>
        /// Success
        /// </summary>
        [DataMember(Name = "success")]
        public bool Success { get; set; }

        /// <summary>
        /// Base
        /// </summary>
        [DataMember(Name = "base")]
        public string Base { get; set; }

        /// <summary>
        /// Date
        /// </summary>
        [DataMember(Name = "date")]
        public DateTime Date { get; set; }

        /// <summary>
        /// Rates
        /// </summary>
        [DataMember(Name = "rates")]
        public Dictionary<string, decimal> Rates { get; set; }
    }
}