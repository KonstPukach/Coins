using System.Collections.Generic;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using CoinsServer.Models;
using CoinsServer.Services;

namespace CoinsServer.Controllers
{
    [RoutePrefix("api/coins")]
    public class CoinsController : AppCoinsApiController
    {
        private readonly CoinsService _coinsService = new CoinsService();
        [Route("")]
        [ResponseType(typeof(IList<Coin>))]
        public async Task<HttpResponseMessage> Get()
        {
            return GetResponse(await _coinsService.GetCoins());
        }

        [Route("list/{start}/{limit}")]
        [ResponseType(typeof(IList<Coin>))]
        public async Task<HttpResponseMessage> Get(int start, int limit)
        {
            return GetResponse(await _coinsService.GetCoins(start, limit));
        }

        [Route("{id}")]
        [ResponseType(typeof(Coin))]
        public async Task<HttpResponseMessage> Get(string id)
        {
            return GetResponse(await _coinsService.GetCoin(id));
        }

        [Route("history/{coinId}/{period}")]
        [ResponseType(typeof(IList<IList<double>>))]
        public async Task<HttpResponseMessage> GetCurrencyHistory(string coinId, string period)
        {
            return GetJsonResponse(await _coinsService.GetCurrencyHistory(coinId, period));
        }
    }
}
