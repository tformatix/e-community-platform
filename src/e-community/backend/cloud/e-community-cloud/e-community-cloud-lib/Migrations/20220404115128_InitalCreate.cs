using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class InitalCreate : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.EnsureSchema(
                name: "dbo");

            migrationBuilder.CreateTable(
                name: "Charge",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    BaseRate = table.Column<double>(type: "float", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "float", nullable: false),
                    TaxRate = table.Column<double>(type: "float", nullable: false),
                    ApplyToECommunity = table.Column<bool>(type: "bit", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Charge", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "DistributionMode",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_DistributionMode", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "ECommunityPermission",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunityPermission", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "ECommunityType",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    DiscountLocal = table.Column<double>(type: "float", nullable: false),
                    DiscountLowRegional = table.Column<double>(type: "float", nullable: false),
                    DiscountHighRegional = table.Column<double>(type: "float", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunityType", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "EventCase",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Priority = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_EventCase", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Language",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Language", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "LegalForm",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_LegalForm", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "PriceRateType",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PriceRateType", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Provider",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Provider", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "RoleClaims",
                schema: "dbo",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    RoleId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ClaimType = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    ClaimValue = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_RoleClaims", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Roles",
                schema: "dbo",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    NormalizedName = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    ConcurrencyStamp = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Roles", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "SupplyMode",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SupplyMode", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "UserClaims",
                schema: "dbo",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    UserId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ClaimType = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    ClaimValue = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserClaims", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "UserLogins",
                schema: "dbo",
                columns: table => new
                {
                    UserId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    LoginProvider = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    ProviderKey = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    ProviderDisplayName = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserLogins", x => x.UserId);
                });

            migrationBuilder.CreateTable(
                name: "UserRoles",
                schema: "dbo",
                columns: table => new
                {
                    UserId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    RoleId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserRoles", x => new { x.UserId, x.RoleId });
                });

            migrationBuilder.CreateTable(
                name: "UserTokens",
                schema: "dbo",
                columns: table => new
                {
                    UserId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    LoginProvider = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Value = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_UserTokens", x => x.UserId);
                });

            migrationBuilder.CreateTable(
                name: "Translation",
                columns: table => new
                {
                    EventCaseId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    LanguageId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Text = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Translation", x => new { x.EventCaseId, x.LanguageId });
                    table.ForeignKey(
                        name: "FK_Translation_EventCase_EventCaseId",
                        column: x => x.EventCaseId,
                        principalTable: "EventCase",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_Translation_Language_LanguageId",
                        column: x => x.LanguageId,
                        principalTable: "Language",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "GridPriceRate",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    BaseRate = table.Column<double>(type: "float", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "float", nullable: false),
                    TaxRate = table.Column<double>(type: "float", nullable: false),
                    GridLevel = table.Column<int>(type: "int", nullable: false),
                    ProviderId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    PriceRateTypeId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_GridPriceRate", x => x.Id);
                    table.ForeignKey(
                        name: "FK_GridPriceRate_PriceRateType_PriceRateTypeId",
                        column: x => x.PriceRateTypeId,
                        principalTable: "PriceRateType",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_GridPriceRate_Provider_ProviderId",
                        column: x => x.ProviderId,
                        principalTable: "Provider",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "SupplierPriceRate",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    BaseRate = table.Column<double>(type: "float", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "float", nullable: false),
                    WorkingPriceMinus = table.Column<double>(type: "float", nullable: false),
                    PricePerPeak = table.Column<double>(type: "float", nullable: false),
                    TaxRate = table.Column<double>(type: "float", nullable: false),
                    ProviderId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    PriceRateTypeId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SupplierPriceRate", x => x.Id);
                    table.ForeignKey(
                        name: "FK_SupplierPriceRate_PriceRateType_PriceRateTypeId",
                        column: x => x.PriceRateTypeId,
                        principalTable: "PriceRateType",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_SupplierPriceRate_Provider_ProviderId",
                        column: x => x.ProviderId,
                        principalTable: "Provider",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "Member",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    StreetNr = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    ZipCode = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    CityName = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    CountryCode = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    IsEmailPublic = table.Column<bool>(type: "bit", nullable: false),
                    TransformerId = table.Column<int>(type: "int", nullable: false),
                    SubstationId = table.Column<int>(type: "int", nullable: false),
                    GridLevel = table.Column<int>(type: "int", nullable: false),
                    SupplyModeId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    LanguageId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    LegalFormId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    UserName = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    NormalizedUserName = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Email = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    NormalizedEmail = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    EmailConfirmed = table.Column<bool>(type: "bit", nullable: false),
                    PasswordHash = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    SecurityStamp = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    ConcurrencyStamp = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    PhoneNumber = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    PhoneNumberConfirmed = table.Column<bool>(type: "bit", nullable: false),
                    TwoFactorEnabled = table.Column<bool>(type: "bit", nullable: false),
                    LockoutEnd = table.Column<DateTimeOffset>(type: "datetimeoffset", nullable: true),
                    LockoutEnabled = table.Column<bool>(type: "bit", nullable: false),
                    AccessFailedCount = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Member", x => x.Id);
                    table.ForeignKey(
                        name: "FK_Member_Language_LanguageId",
                        column: x => x.LanguageId,
                        principalTable: "Language",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_Member_LegalForm_LegalFormId",
                        column: x => x.LegalFormId,
                        principalTable: "LegalForm",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_Member_SupplyMode_SupplyModeId",
                        column: x => x.SupplyModeId,
                        principalTable: "SupplyMode",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "GridPriceRateCharge",
                columns: table => new
                {
                    GridPriceRateId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ChargeId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_GridPriceRateCharge", x => new { x.GridPriceRateId, x.ChargeId });
                    table.ForeignKey(
                        name: "FK_GridPriceRateCharge_Charge_ChargeId",
                        column: x => x.ChargeId,
                        principalTable: "Charge",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_GridPriceRateCharge_GridPriceRate_GridPriceRateId",
                        column: x => x.GridPriceRateId,
                        principalTable: "GridPriceRate",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "ECommunity",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    IsPublic = table.Column<bool>(type: "bit", nullable: false),
                    IsOfficial = table.Column<bool>(type: "bit", nullable: false),
                    IsClosed = table.Column<bool>(type: "bit", nullable: false),
                    Description = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    MemberDayBinding = table.Column<int>(type: "int", nullable: false),
                    DistributionModeId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    SupplierPriceRateId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    LegalFormId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ECommunityTypeId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunity", x => x.Id);
                    table.ForeignKey(
                        name: "FK_ECommunity_DistributionMode_DistributionModeId",
                        column: x => x.DistributionModeId,
                        principalTable: "DistributionMode",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_ECommunity_ECommunityType_ECommunityTypeId",
                        column: x => x.ECommunityTypeId,
                        principalTable: "ECommunityType",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_ECommunity_LegalForm_LegalFormId",
                        column: x => x.LegalFormId,
                        principalTable: "LegalForm",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_ECommunity_SupplierPriceRate_SupplierPriceRateId",
                        column: x => x.SupplierPriceRateId,
                        principalTable: "SupplierPriceRate",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "SmartMeter",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    AESKey = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    APIKey = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    Description = table.Column<string>(type: "nvarchar(max)", nullable: true),
                    IsMain = table.Column<bool>(type: "bit", nullable: false),
                    MeasuresConsumption = table.Column<bool>(type: "bit", nullable: false),
                    MeasuresFeedIn = table.Column<bool>(type: "bit", nullable: false),
                    IsDirectFeedIn = table.Column<bool>(type: "bit", nullable: false),
                    IsOverflowFeedIn = table.Column<bool>(type: "bit", nullable: false),
                    MemberId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    SupplierPriceRateId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    GridPriceRateId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SmartMeter", x => x.Id);
                    table.ForeignKey(
                        name: "FK_SmartMeter_GridPriceRate_GridPriceRateId",
                        column: x => x.GridPriceRateId,
                        principalTable: "GridPriceRate",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_SmartMeter_Member_MemberId",
                        column: x => x.MemberId,
                        principalTable: "Member",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_SmartMeter_SupplierPriceRate_SupplierPriceRateId",
                        column: x => x.SupplierPriceRateId,
                        principalTable: "SupplierPriceRate",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateTable(
                name: "ECommunityMemberStatus",
                columns: table => new
                {
                    ECommunityId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    MemberId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    DistributionPercentage = table.Column<double>(type: "float", nullable: false),
                    EntryDate = table.Column<DateTime>(type: "datetime2", nullable: false),
                    LeftDate = table.Column<DateTime>(type: "datetime2", nullable: false),
                    ECommunityPermissionId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunityMemberStatus", x => new { x.ECommunityId, x.MemberId });
                    table.ForeignKey(
                        name: "FK_ECommunityMemberStatus_ECommunity_ECommunityId",
                        column: x => x.ECommunityId,
                        principalTable: "ECommunity",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_ECommunityMemberStatus_ECommunityPermission_ECommunityPermissionId",
                        column: x => x.ECommunityPermissionId,
                        principalTable: "ECommunityPermission",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_ECommunityMemberStatus_Member_MemberId",
                        column: x => x.MemberId,
                        principalTable: "Member",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "MeterDataECommunity",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Timestamp = table.Column<DateTime>(type: "datetime2", nullable: false),
                    ActiveEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    ActiveEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    ReactiveEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    ReactiveEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    ActivePowerPlus = table.Column<int>(type: "int", nullable: false),
                    ActivePowerMinus = table.Column<int>(type: "int", nullable: false),
                    ReactivePowerPlus = table.Column<int>(type: "int", nullable: false),
                    ReactivePowerMinus = table.Column<int>(type: "int", nullable: false),
                    PrepaymentCounter = table.Column<int>(type: "int", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "float", nullable: false),
                    WorkingPriceMinus = table.Column<double>(type: "float", nullable: false),
                    ECommunityId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MeterDataECommunity", x => x.Id);
                    table.ForeignKey(
                        name: "FK_MeterDataECommunity_ECommunity_ECommunityId",
                        column: x => x.ECommunityId,
                        principalTable: "ECommunity",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "BatterySystem",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    CapacityAH = table.Column<double>(type: "float", nullable: false),
                    SmartMeterId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_BatterySystem", x => x.Id);
                    table.ForeignKey(
                        name: "FK_BatterySystem_SmartMeter_SmartMeterId",
                        column: x => x.SmartMeterId,
                        principalTable: "SmartMeter",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "MeterDataProfile",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Timestamp = table.Column<DateTime>(type: "datetime2", nullable: false),
                    ActiveEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    ActiveEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    ReactiveEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    ReactiveEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    ActivePowerPlus = table.Column<int>(type: "int", nullable: false),
                    ActivePowerMinus = table.Column<int>(type: "int", nullable: false),
                    ReactivePowerPlus = table.Column<int>(type: "int", nullable: false),
                    ReactivePowerMinus = table.Column<int>(type: "int", nullable: false),
                    PrepaymentCounter = table.Column<int>(type: "int", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "float", nullable: false),
                    WorkingPriceMinus = table.Column<double>(type: "float", nullable: false),
                    SmartMeterId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    EventCaseId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MeterDataProfile", x => x.Id);
                    table.ForeignKey(
                        name: "FK_MeterDataProfile_EventCase_EventCaseId",
                        column: x => x.EventCaseId,
                        principalTable: "EventCase",
                        principalColumn: "Id");
                    table.ForeignKey(
                        name: "FK_MeterDataProfile_SmartMeter_SmartMeterId",
                        column: x => x.SmartMeterId,
                        principalTable: "SmartMeter",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "PVSystem",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    PeakWP = table.Column<double>(type: "float", nullable: false),
                    SmartMeterId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PVSystem", x => x.Id);
                    table.ForeignKey(
                        name: "FK_PVSystem_SmartMeter_SmartMeterId",
                        column: x => x.SmartMeterId,
                        principalTable: "SmartMeter",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "ECommunityTransaction",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ECommunityEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    ECommunityEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    ECommunityPricePlus = table.Column<double>(type: "float", nullable: false),
                    ECommunityPriceMinus = table.Column<double>(type: "float", nullable: false),
                    SupplierEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    SupplierEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    SupplierPricePlus = table.Column<double>(type: "float", nullable: false),
                    SupplierPriceMinus = table.Column<double>(type: "float", nullable: false),
                    GridPricePlus = table.Column<double>(type: "float", nullable: false),
                    MeterDataECommunityId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    SmartMeterId = table.Column<Guid>(type: "uniqueidentifier", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunityTransaction", x => x.Id);
                    table.ForeignKey(
                        name: "FK_ECommunityTransaction_MeterDataECommunity_MeterDataECommunityId",
                        column: x => x.MeterDataECommunityId,
                        principalTable: "MeterDataECommunity",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_ECommunityTransaction_SmartMeter_SmartMeterId",
                        column: x => x.SmartMeterId,
                        principalTable: "SmartMeter",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateIndex(
                name: "IX_BatterySystem_SmartMeterId",
                table: "BatterySystem",
                column: "SmartMeterId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunity_DistributionModeId",
                table: "ECommunity",
                column: "DistributionModeId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunity_ECommunityTypeId",
                table: "ECommunity",
                column: "ECommunityTypeId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunity_LegalFormId",
                table: "ECommunity",
                column: "LegalFormId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunity_SupplierPriceRateId",
                table: "ECommunity",
                column: "SupplierPriceRateId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunityMemberStatus_ECommunityPermissionId",
                table: "ECommunityMemberStatus",
                column: "ECommunityPermissionId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunityMemberStatus_MemberId",
                table: "ECommunityMemberStatus",
                column: "MemberId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunityTransaction_MeterDataECommunityId",
                table: "ECommunityTransaction",
                column: "MeterDataECommunityId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunityTransaction_SmartMeterId",
                table: "ECommunityTransaction",
                column: "SmartMeterId");

            migrationBuilder.CreateIndex(
                name: "IX_GridPriceRate_PriceRateTypeId",
                table: "GridPriceRate",
                column: "PriceRateTypeId");

            migrationBuilder.CreateIndex(
                name: "IX_GridPriceRate_ProviderId",
                table: "GridPriceRate",
                column: "ProviderId");

            migrationBuilder.CreateIndex(
                name: "IX_GridPriceRateCharge_ChargeId",
                table: "GridPriceRateCharge",
                column: "ChargeId");

            migrationBuilder.CreateIndex(
                name: "IX_Member_LanguageId",
                table: "Member",
                column: "LanguageId");

            migrationBuilder.CreateIndex(
                name: "IX_Member_LegalFormId",
                table: "Member",
                column: "LegalFormId");

            migrationBuilder.CreateIndex(
                name: "IX_Member_SupplyModeId",
                table: "Member",
                column: "SupplyModeId");

            migrationBuilder.CreateIndex(
                name: "IX_MeterDataECommunity_ECommunityId",
                table: "MeterDataECommunity",
                column: "ECommunityId");

            migrationBuilder.CreateIndex(
                name: "IX_MeterDataProfile_EventCaseId",
                table: "MeterDataProfile",
                column: "EventCaseId");

            migrationBuilder.CreateIndex(
                name: "IX_MeterDataProfile_SmartMeterId",
                table: "MeterDataProfile",
                column: "SmartMeterId");

            migrationBuilder.CreateIndex(
                name: "IX_PVSystem_SmartMeterId",
                table: "PVSystem",
                column: "SmartMeterId");

            migrationBuilder.CreateIndex(
                name: "IX_SmartMeter_GridPriceRateId",
                table: "SmartMeter",
                column: "GridPriceRateId");

            migrationBuilder.CreateIndex(
                name: "IX_SmartMeter_MemberId",
                table: "SmartMeter",
                column: "MemberId");

            migrationBuilder.CreateIndex(
                name: "IX_SmartMeter_SupplierPriceRateId",
                table: "SmartMeter",
                column: "SupplierPriceRateId");

            migrationBuilder.CreateIndex(
                name: "IX_SupplierPriceRate_PriceRateTypeId",
                table: "SupplierPriceRate",
                column: "PriceRateTypeId");

            migrationBuilder.CreateIndex(
                name: "IX_SupplierPriceRate_ProviderId",
                table: "SupplierPriceRate",
                column: "ProviderId");

            migrationBuilder.CreateIndex(
                name: "IX_Translation_LanguageId",
                table: "Translation",
                column: "LanguageId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "BatterySystem");

            migrationBuilder.DropTable(
                name: "ECommunityMemberStatus");

            migrationBuilder.DropTable(
                name: "ECommunityTransaction");

            migrationBuilder.DropTable(
                name: "GridPriceRateCharge");

            migrationBuilder.DropTable(
                name: "MeterDataProfile");

            migrationBuilder.DropTable(
                name: "PVSystem");

            migrationBuilder.DropTable(
                name: "RoleClaims",
                schema: "dbo");

            migrationBuilder.DropTable(
                name: "Roles",
                schema: "dbo");

            migrationBuilder.DropTable(
                name: "Translation");

            migrationBuilder.DropTable(
                name: "UserClaims",
                schema: "dbo");

            migrationBuilder.DropTable(
                name: "UserLogins",
                schema: "dbo");

            migrationBuilder.DropTable(
                name: "UserRoles",
                schema: "dbo");

            migrationBuilder.DropTable(
                name: "UserTokens",
                schema: "dbo");

            migrationBuilder.DropTable(
                name: "ECommunityPermission");

            migrationBuilder.DropTable(
                name: "MeterDataECommunity");

            migrationBuilder.DropTable(
                name: "Charge");

            migrationBuilder.DropTable(
                name: "SmartMeter");

            migrationBuilder.DropTable(
                name: "EventCase");

            migrationBuilder.DropTable(
                name: "ECommunity");

            migrationBuilder.DropTable(
                name: "GridPriceRate");

            migrationBuilder.DropTable(
                name: "Member");

            migrationBuilder.DropTable(
                name: "DistributionMode");

            migrationBuilder.DropTable(
                name: "ECommunityType");

            migrationBuilder.DropTable(
                name: "SupplierPriceRate");

            migrationBuilder.DropTable(
                name: "Language");

            migrationBuilder.DropTable(
                name: "LegalForm");

            migrationBuilder.DropTable(
                name: "SupplyMode");

            migrationBuilder.DropTable(
                name: "PriceRateType");

            migrationBuilder.DropTable(
                name: "Provider");
        }
    }
}
