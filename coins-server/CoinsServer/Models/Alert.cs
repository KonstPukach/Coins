using CoinsServer.Services;
using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Runtime.Serialization;
using System.Threading.Tasks;
using System.Web;

namespace CoinsServer.Models
{
    [DataContract]
    public class AlertDto
    {
        public AlertDto()
        {
        }

        public AlertDto(Alert alert)
        {
            AlertId = alert.AlertId;
            Token = alert.User.Token;
            CoinId = alert.CoinId;
            Name = alert.Name;
            LowLimit = alert.LowLimit;
            HighLimit = alert.HighLimit;
            CreatedAt = alert.CreatedAt;
        }

        [DataMember(Name = "alert_id")]
        public int AlertId { get; set; }

        [DataMember(Name = "token")]
        public string Token { get; set; }

        [DataMember(Name = "coin_id")]
        public string CoinId { get; set; }

        [DataMember(Name = "name")]
        public string Name { get; set; }

        [DataMember(Name = "low_limit")]
        public decimal LowLimit { get; set; }

        [DataMember(Name = "high_limit")]
        public decimal HighLimit { get; set; }

        [DataMember(Name = "created_at")]
        public DateTime CreatedAt { get; set; }
    }

    public class Alert
    {
        private static CoinsContext db = new CoinsContext();
        public Alert()
        {
        }

        public Alert(AlertDto alert)
        {
            AlertId = alert.AlertId;
            var user = db.Users.FirstOrDefault(u => u.Token == alert.Token);
            if (user == null)
            {
                user = new User { Token = alert.Token };
                user = db.Users.Add(user);
                db.SaveChanges();
            }
            UserId = user.Id;
            var coin = db.Coins.FirstOrDefault(c => c.Id == alert.CoinId);
            if (coin == null)
            {
                Task.Run(async () => await new CoinsService().GetCoin(alert.CoinId)).Wait();
                coin = db.Coins.FirstOrDefault(c => c.Id == alert.CoinId);
            }
            CoinId = coin.Id;
            Name = alert.Name;
            LowLimit = alert.LowLimit;
            HighLimit = alert.HighLimit;
            CreatedAt = alert.CreatedAt;
        }
        [Key]
        public int AlertId { get; set; }

        [Required]
        public int UserId { get; set; }
        public User User { get; set; }

        [Required]
        public string CoinId { get; set; }
        public Coin Coin { get; set; }

        [Required]
        public string Name { get; set; }

        [Required]
        public decimal LowLimit { get; set; }

        [Required]
        public decimal HighLimit { get; set; }

        [Required]
        public DateTime CreatedAt { get; set; }

    }
}