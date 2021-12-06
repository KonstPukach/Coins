using CoinsServer.Services;
using System.Net.Http;
using System.Threading.Tasks;
using System.Web.Http;

namespace CoinsServer.Controllers
{
    [RoutePrefix("api/news")]
    public class NewsController : AppCoinsApiController
    {
        private readonly NewsService newsService = new NewsService();

        [Route("{language}/{page?}")]
        public async Task<HttpResponseMessage> Get(string language, int page = 1)
        {
            return GetJsonResponse(await newsService.GetNews(language, page));
        }
    }
}