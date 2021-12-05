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
        private IQueryable<Alert> Alerts => db.Alerts.Include(a => a.User);

        [Route("")]
        public IEnumerable<AlertDto> GetAlerts()
        {
            return Alerts.ToList().Select(alert => new AlertDto(alert));
        }

        [Route("user")]
        public IEnumerable<AlertDto> GetAlerts(string token)
        {
            return Alerts.Where(alert => alert.User.Token == token).ToList().Select(alert => new AlertDto(alert));
        }

        [Route("{id:int}")]
        [ResponseType(typeof(AlertDto))]
        public async Task<IHttpActionResult> GetAlert(int id)
        {
            var alert = await Alerts.Where(b => b.AlertId == id).FirstOrDefaultAsync();
            if (alert == null)
            {
                return NotFound();
            }
            return Ok(alert);
        }

        [Route("create")]
        [ResponseType(typeof(AlertDto))]
        [HttpPost]
        public IHttpActionResult CreateAlert([FromBody] AlertDto alert)
        {
            if (alert == null || !ModelState.IsValid)
            {
                return BadRequest("Invalid data.");
            }
            db.Alerts.Add(new Alert(alert));
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
        [ResponseType(typeof(AlertDto))]
        [HttpPut]
        public async Task<IHttpActionResult> UpdateAlert(AlertDto alert)
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
            db.SaveChanges();
            return Ok(alertFromDb);
        }
    }
}