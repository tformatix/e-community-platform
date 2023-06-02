using e_community_cloud.Dtos;
using e_community_cloud_lib.Util;
using e_community_cloud_lib.Util.BusinessLogic;
using Microsoft.AspNetCore.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers
{
    [ApiController]
    [ApiExplorerSettings(IgnoreApi = true)]
    public class ErrorController : ControllerBase
    {
        [Route("/Error")]
        public IActionResult Error()
        {
            var context = HttpContext.Features.Get<IExceptionHandlerFeature>();
            var exc = context.Error;
            if (!(exc is ServiceException))
                return new ObjectResult(new ErrorDto { Error = "INTERNAL_ERROR" }) { StatusCode = 500 };
            Log.Information(exc.Message);
            return BadRequest(new ErrorDto { Error = exc.Message });
        }
    }
}
