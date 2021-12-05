using CoinsServer.Models;
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
    [RoutePrefix("api/alerts")]
    public class AlertController : AppCoinsApiController
    {
        private CoinsContext db = new CoinsContext();

        [Route("")]
        public IEnumerable<Alert> GetAlerts()
        {
            return db.Alerts;
        }

        [Route("user")]
        public IEnumerable<Alert> GetAlerts(string token)
        {
            return db.Alerts.Where(alert => alert.Token == token);
        }

        [Route("{id:int}")]
        [ResponseType(typeof(Alert))]
        public async Task<IHttpActionResult> GetAlert(int id)
        {
            var alert = await db.Alerts.Where(b => b.AlertId == id).FirstOrDefaultAsync();
            if (alert == null)
            {
                return NotFound();
            }
            return Ok(alert);
        }

        [Route("create")]
        [ResponseType(typeof(Alert))]
        [HttpPost]
        public IHttpActionResult CreateAlert([FromBody] Alert alert)
        {
            if (alert == null || !ModelState.IsValid)
            {
                return BadRequest("Invalid data.");
            }
            db.Alerts.Add(alert);
            db.SaveChanges();
            return Ok(alert);
        }

        [Route("delete/{id:int}")]
        [ResponseType(typeof(string))]
        [HttpDelete]
        public async Task<IHttpActionResult> DeleteAlert(int id)
        {
            var alert = await db.Alerts.Where(b => b.AlertId == id).FirstOrDefaultAsync();
            if (alert == null)
            {
                return NotFound();
            }

            db.Alerts.Remove(alert);
            await db.SaveChangesAsync();
            return Ok();
        }

        [Route("update")]
        [ResponseType(typeof(Alert))]
        [HttpPut]
        public async Task<IHttpActionResult> UpdateAlert(Alert alert)
        {
            if (alert == null || !ModelState.IsValid)
            {
                return BadRequest("Not a valid model");
            }

            var alertFromDb = await db.Alerts.FirstOrDefaultAsync(a => a.AlertId == alert.AlertId);
            if (alertFromDb == null)
            {
                return NotFound();
            }
            alertFromDb.CoinId = alert.CoinId;
            alertFromDb.HighLimit = alert.HighLimit;
            alertFromDb.LowLimit = alert.LowLimit;
            alertFromDb.Token = alert.Token;
            db.SaveChanges();
            return Ok(alertFromDb);
        }
    }
}