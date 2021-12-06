using CoinsServer.Models;
using CoinsServer.Services;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Linq;
using System.Threading.Tasks;
using System.Web;
using System.Web.Http;
using System.Web.Http.Description;

namespace CoinsServer.Controllers
{
    [RoutePrefix("api/favorites")]
    public class FavoritesController : AppCoinsApiController
    {
        private CoinsContext db = new CoinsContext();
        private IQueryable<FavoriteCoin> Favorites => db.Favorites.Include(f => f.User).Include(f => f.Coin);

        [Route("user")]
        public IEnumerable<string> GetFavorites(string token)
        {
            return Favorites.Where(fav => fav.User.Token == token).ToList().Select(fav => fav.CoinId);
        }

        [Route("add")]
        public IHttpActionResult AddFavorite(string token, string coinId)
        {
            if (token == null || coinId == null)
            {
                return BadRequest("Invalid data.");
            }
            var user = db.Users.FirstOrDefault(u => u.Token == token);
            if (user == null)
            {
                user = new User { Token = token };
                user = db.Users.Add(user);
                db.SaveChanges();
            }
            var coin = db.Coins.FirstOrDefault(c => c.Id == coinId);
            if (coin == null)
            {
                Task.Run(async () => await new CoinsService().GetCoin(coinId)).Wait();
                coin = db.Coins.FirstOrDefault(c => c.Id == coinId);
            }
            db.Favorites.Add(new FavoriteCoin { CoinId = coin.Id, UserId = user.Id });
            db.SaveChanges();
            return Ok();
        }

        [Route("remove")]
        public IHttpActionResult RemoveFavorite(string token, string coinId)
        {
            if (token == null || coinId == null)
            {
                return BadRequest("Invalid data.");
            }
            var favorite = Favorites.FirstOrDefault(fav => fav.User.Token == token && fav.CoinId == coinId);
            if (favorite == null)
            {
                return NotFound();
            }
            db.Favorites.Remove(favorite);
            db.SaveChanges();
            return Ok();
        }

        [Route("clear")]
        public IHttpActionResult ClearFavorites(string token)
        {
            if (token == null)
            {
                return BadRequest("Invalid data.");
            }
            var favorites = Favorites.Where(fav => fav.User.Token == token);
            db.Favorites.RemoveRange(favorites);
            db.SaveChanges();
            return Ok();
        }
    }
}