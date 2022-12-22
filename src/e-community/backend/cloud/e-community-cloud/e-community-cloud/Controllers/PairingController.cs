using e_community_cloud.Dtos;
using e_community_cloud_lib.BusinessLogic.Interfaces;
using e_community_cloud_lib.Database.Local;
using e_community_cloud_lib.LocalDtos;
using e_community_cloud_lib.Models.SmartMeter;
using e_community_cloud_lib.Util.Enums;
using e_community_cloud_lib.Util.Extensions;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Serilog;
using System;
using System.Linq;
using System.Threading.Tasks;

namespace e_community_cloud.Controllers
{
    [ApiController]
    [Route("[controller]/[action]")]
    public class PairingController : ControllerBase
    {
        private readonly IPairingService mPairingService;
        private readonly IAuthService mAuthService;

        public PairingController(IPairingService _pairingService, IAuthService _authService)
        {
            mPairingService = _pairingService;
            mAuthService = _authService;
        }

        [HttpPost]
        [ProducesResponseType(typeof(CreateSmartMeterDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        public async Task<IActionResult> CreateSmartMeter([FromBody] CreateSmartMeterModel _createSmartMeterModel)
        {
            Log.Information($"Pairing/CreateSmartMeter::{_createSmartMeterModel.Name}");
            var memberId = (Guid)User.GetMemberId();
            var smartMeter = (await mPairingService.CreateSmartMeter(_createSmartMeterModel, memberId)).CopyPropertiesTo(new LocalSmartMeterDto());
            string refreshToken = (await mAuthService.Refresh(_createSmartMeterModel.RefreshToken)).RefreshToken;
            return base.Ok(new CreateSmartMeterDto()
            {
                SmartMeter = smartMeter,
                RefreshToken = refreshToken
            });
        }

        [HttpGet]
        [ProducesResponseType(typeof(LocalDto), 200)]
        [ProducesResponseType(typeof(ErrorDto), 400)]
        [Authorize]
        public async Task<IActionResult> LocalData(Guid _smartMeterId)
        {
            Log.Information($"Pairing/LocalData::{_smartMeterId}");
            SmartMeter smartMeter = await mPairingService.GetLocalData(_smartMeterId);

            var batterySystems = smartMeter.BatterySystems
                .Select(x => x.CopyPropertiesTo(new LocalBatterySystemDto()))
                .ToList();
            var pvSystems = smartMeter.PVSystems
                .Select(x => x.CopyPropertiesTo(new LocalPVSystemDto()))
                .ToList();
            var supplierPriceRate = smartMeter.SupplierPriceRate?.CopyPropertiesTo(new LocalSupplierPriceRateDto());
            var gridPriceRate = smartMeter.GridPriceRate?.CopyPropertiesTo(new LocalGridPriceRateDto());
            var charges = smartMeter.GridPriceRate?.Charges
                .Select(x => x.CopyPropertiesTo(new LocalChargeDto()))
                .ToList();
            var member = smartMeter.Member?.CopyPropertiesTo(new LocalMemberDto());
            var eCommunityMembership = smartMeter.Member.ECommunityMemberships
                .FirstOrDefault(x => x.ECommunityPermission != ECommunityPermission.Former && x.ECommunityPermission != ECommunityPermission.Denied);
            LocalECommunityDto eCommunity = null;
            if (eCommunityMembership != null)
            {
                eCommunity = new LocalECommunityDto()
                {
                    Id = eCommunityMembership.ECommunityId,
                    IsPowerFeedAllowed = eCommunityMembership.ECommunityPermission != ECommunityPermission.Former
                };
            }

            return base.Ok(new LocalDto()
            {
                BatterySystems = batterySystems,
                PVSystems = pvSystems,
                SupplierPriceRate = supplierPriceRate,
                GridPriceRate = gridPriceRate,
                Charges = charges,
                Member = member,
                ECommunity = eCommunity
            });

        }
    }
}
