using System;
using System.Collections.Generic;
using System.Net.Http;
using System.Web.Http;
using System.Threading.Tasks;
using System.Web.Http.Description;
using CoinsServer.Models;
using CoinsServer.Services;

namespace CoinsServer.Controllers
{
    [RoutePrefix("api/market-stats")]
    public class MarketStatsController : AppCoinsApiController
    {
        private readonly MarketService _marketService = new MarketService();

        [Route("global")]
        [ResponseType(typeof(GlobalData))]
        public async Task<HttpResponseMessage> GetGlobalData()
        {
            return GetResponse(await _marketService.GetGlobalData());
        }

        [Route("capshare/current/{sharesCount}")]
        [ResponseType(typeof(IList<CapShare>))]
        public async Task<HttpResponseMessage> GetCurrentCapShare(int sharesCount)
        {
            return GetResponse(await _marketService.GetPieCapShare(sharesCount));
        }
    }
}
