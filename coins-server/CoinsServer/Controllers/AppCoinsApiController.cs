using System.Net;
using System.Net.Http;
using System.Text;
using System.Web.Http;
using System.Web.Http.Description;

namespace CoinsServer.Controllers
{
    public class AppCoinsApiController: ApiController
    {
        [ApiExplorerSettings(IgnoreApi = true)]
        public HttpResponseMessage GetResponse<T>(T response)
        {
            return response == null
                ? Request.CreateResponse(HttpStatusCode.BadRequest)
                : Request.CreateResponse(HttpStatusCode.OK, response);
        }

        [ApiExplorerSettings(IgnoreApi = true)]
        public HttpResponseMessage GetJsonResponse(string jsonString)
        {
            if (jsonString == null)
            {
                return Request.CreateResponse(HttpStatusCode.BadRequest);
            }
            var response = Request.CreateResponse(HttpStatusCode.OK);
            response.Content = new StringContent(jsonString, Encoding.UTF8, "application/json");
            return response;
        }
    }
}