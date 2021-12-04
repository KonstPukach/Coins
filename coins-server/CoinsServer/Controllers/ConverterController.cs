using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;
using System.Web.Http.Description;
using CoinsServer.Models;
using CoinsServer.Services;

namespace CoinsServer.Controllers
{
    [RoutePrefix("api/exchange-rates")]
    public class ConverterController : AppCoinsApiController
    {
        private readonly ConverterService _converterService = new ConverterService();

        [Route("{limit?}")]
        [ResponseType(typeof(Rates))]
        public async Task<HttpResponseMessage> Get(int limit=0)
        {
            return GetResponse(await _converterService.GetRates(limit));
        }
    }
}
