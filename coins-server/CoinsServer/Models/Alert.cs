using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Runtime.Serialization;
using System.Web;

namespace CoinsServer.Models
{
    [DataContract]
    public class Alert
    {
        [Key]
        [DataMember(Name = "alert_id")]
        public int AlertId { get; set; }

        [Required]
        [DataMember(Name = "token")]
        public string Token { get; set; }

        [Required]
        [DataMember(Name = "coin_id")]
        public string CoinId { get; set; }

        [Required]
        [DataMember(Name = "low_limit")]
        public decimal LowLimit { get; set; }

        [Required]
        [DataMember(Name = "high_limit")]
        public decimal HighLimit { get; set; }
    }
}