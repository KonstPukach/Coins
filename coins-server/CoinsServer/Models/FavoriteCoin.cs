using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CoinsServer.Models
{
    public class FavoriteCoin
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string CoinId { get; set; }
        public Coin Coin { get; set; }

        [Required]
        public int UserId { get; set; }
        public User User { get; set; }
    }
}