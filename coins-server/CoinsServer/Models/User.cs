using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace CoinsServer.Models
{
    public class User
    {
        [Key]
        public int Id { get; set; }

        [Required]
        public string Token { get; set; }

        public ICollection<Alert> Alerts { get; set; }
    }
}