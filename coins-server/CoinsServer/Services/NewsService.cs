using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using System.Web;

namespace CoinsServer.Services
{
    public class NewsService : MainService
    {
        public async Task<string> GetNews(string language, int page = 0)
        {
            var response = await GetCoinsMarketApiResponse($"{GlobalConfig.NewsUri}&page={page}&language={language}");
            if (!response.IsSuccessStatusCode)
            {
                return null;
            }
            return response.IsSuccessStatusCode ? await response.Content.ReadAsStringAsync() : null;
        }
    }
}