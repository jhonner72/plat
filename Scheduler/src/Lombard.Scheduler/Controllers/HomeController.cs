using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Hangfire;

namespace Lombard.Scheduler.Controllers
{
    public class HomeController : Controller
    {
        public ActionResult Index()
        {
            //return View((object)TextBuffer.ToString());
            return View();
        }

        public ActionResult Buffer()
        {
            //return Content(TextBuffer.ToString());
            return View();
        }

        [HttpPost]
        public ActionResult Create()
        {
            //BackgroundJob.Enqueue(() => TextBuffer.WriteLine("Background Job completed successfully!"));
            //TextBuffer.WriteLine("Background job has been created.");

            //return RedirectToAction("Index");
            return RedirectToAction("ActionResult Create");
        }
    }
}