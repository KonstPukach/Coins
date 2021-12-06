using CoinsServer.Models;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading.Tasks;

namespace CoinsServer.Services
{
    public class AlertService
    {
        public async Task CheckAlerts()
        {
            using (var db = new CoinsContext())
            {
                var alerts = await db.Alerts.Include(a => a.User).ToListAsync();
                if (!alerts.Any())
                {
                    return;
                }
                var coins = await GetCoins();
                if (coins != null)
                {
                    await PushAlerts(alerts, coins, db);
                }
            }
        }

        private async Task PushAlerts(List<Alert> alerts, Dictionary<string, Coin> coins, CoinsContext db)
        {
            foreach (var alert in alerts)
            {
                if (!coins.ContainsKey(alert.CoinId))
                {
                    continue;
                }
                var coin = coins[alert.CoinId];
                if (coin != null && IsAlertShouldBePushed(alert, coin.PriceUsd))
                {
                    PushAlert(alert, coin);
                    db.Alerts.Remove(alert);
                }
            }
            try
            {
                await db.SaveChangesAsync();
            }
            catch(Exception)
            {
            }
        }

        private async Task<Dictionary<string, Coin>> GetCoins()
        {
            var coins = await new CoinsService().GetCoins();
            return coins?.ToDictionary(coin => coin.Id);
        }

        private bool IsAlertShouldBePushed(Alert alert, decimal? currentUsdPrice)
        {
            return currentUsdPrice.HasValue && (currentUsdPrice.Value < alert.LowLimit ||
                                                currentUsdPrice.Value > alert.HighLimit);
        }

        private void PushAlert(Alert alert, Coin coin)
        {
            try
            {
                var byteArray = Encoding.UTF8.GetBytes(JsonConvert.SerializeObject(GetNotificationData(alert, coin)));
                var webRequest = GetFirebaseWebRequest(byteArray.Length);

                using (Stream dataStream = webRequest.GetRequestStream())
                {
                    dataStream.Write(byteArray, 0, byteArray.Length);

                    using (WebResponse webResponse = webRequest.GetResponse())
                    {
                        using (Stream dataStreamResponse = webResponse.GetResponseStream())
                        {
                            using (StreamReader reader = new StreamReader(dataStreamResponse))
                            {
                                var response = reader.ReadToEnd();
                            }
                        }
                    }
                }
            }
            catch (Exception)
            {
            }
        }

        private WebRequest GetFirebaseWebRequest(int dataLength)
        {
            WebRequest webRequest = WebRequest.Create(GlobalConfig.FirebaseRequestUri);
            webRequest.Method = "post";
            webRequest.ContentType = "application/json";
            webRequest.Headers.Add($"Authorization: key={GlobalConfig.FirebaseApplicationId}");
            webRequest.Headers.Add($"Sender: id={GlobalConfig.FirebaseSenderId}");
            webRequest.ContentLength = dataLength;
            return webRequest;
        }

        private object GetNotificationData(Alert alert, Coin coin)
        {
            return new
            {
                to = alert.User.Token,
                notification = new
                {
                    body = $"Current {coin.Name} price is {coin.PriceUsd}$",
                    title = $"{coin.Name} price was changed!",
                    icon = coin.IconUrl
                },
                priority = "high"
            };
        }
    }
}