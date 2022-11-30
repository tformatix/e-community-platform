using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_local_lib.Migrations
{
    public partial class InitialCreation : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "BatterySystem",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false),
                    CapacityAH = table.Column<double>(type: "REAL", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_BatterySystem", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Charge",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false),
                    BaseRate = table.Column<double>(type: "REAL", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "REAL", nullable: false),
                    TaxRate = table.Column<double>(type: "REAL", nullable: false),
                    ApplyToECommunity = table.Column<bool>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Charge", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "ECommunity",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunity", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "EventCase",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false),
                    Name = table.Column<string>(type: "TEXT", nullable: true),
                    Priority = table.Column<int>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_EventCase", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "GridPriceRate",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false),
                    BaseRate = table.Column<double>(type: "REAL", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "REAL", nullable: false),
                    TaxRate = table.Column<double>(type: "REAL", nullable: false),
                    GridLevel = table.Column<int>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_GridPriceRate", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "Member",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false),
                    StreetNr = table.Column<string>(type: "TEXT", nullable: true),
                    ZipCode = table.Column<string>(type: "TEXT", nullable: true),
                    CityName = table.Column<string>(type: "TEXT", nullable: true),
                    CountryCode = table.Column<string>(type: "TEXT", nullable: true),
                    GridLevel = table.Column<int>(type: "INTEGER", nullable: false),
                    SupplyMode = table.Column<int>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Member", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "MeterDataProfile",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    WorkingPricePlus = table.Column<double>(type: "REAL", nullable: false),
                    WorkingPriceMinus = table.Column<double>(type: "REAL", nullable: false),
                    Timestamp = table.Column<DateTime>(type: "TEXT", nullable: false),
                    ActiveEnergyPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ActiveEnergyMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactiveEnergyPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactiveEnergyMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    ActivePowerPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ActivePowerMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactivePowerPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactivePowerMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    PrepaymentCounter = table.Column<int>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MeterDataProfile", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "MeterDataRealTime",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Timestamp = table.Column<DateTime>(type: "TEXT", nullable: false),
                    ActiveEnergyPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ActiveEnergyMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactiveEnergyPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactiveEnergyMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    ActivePowerPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ActivePowerMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactivePowerPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactivePowerMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    PrepaymentCounter = table.Column<int>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MeterDataRealTime", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "PVSystem",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false),
                    PeakWP = table.Column<double>(type: "REAL", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PVSystem", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "SmartMeter",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false),
                    AESKey = table.Column<string>(type: "TEXT", nullable: true),
                    APIKey = table.Column<string>(type: "TEXT", nullable: true),
                    Name = table.Column<string>(type: "TEXT", nullable: true),
                    Description = table.Column<string>(type: "TEXT", nullable: true),
                    IsMain = table.Column<bool>(type: "INTEGER", nullable: false),
                    MeasuresConsumption = table.Column<bool>(type: "INTEGER", nullable: false),
                    MeasuresFeedIn = table.Column<bool>(type: "INTEGER", nullable: false),
                    IsDirectFeedIn = table.Column<bool>(type: "INTEGER", nullable: false),
                    IsOverflowFeedIn = table.Column<bool>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SmartMeter", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "SupplierPriceRate",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false),
                    BaseRate = table.Column<double>(type: "REAL", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "REAL", nullable: false),
                    WorkingPriceMinus = table.Column<double>(type: "REAL", nullable: false),
                    PricePerPeak = table.Column<double>(type: "REAL", nullable: false),
                    TaxRate = table.Column<double>(type: "REAL", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SupplierPriceRate", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "MeterDataHistory",
                columns: table => new
                {
                    Id = table.Column<int>(type: "INTEGER", nullable: false)
                        .Annotation("Sqlite:Autoincrement", true),
                    Cloudiness = table.Column<double>(type: "REAL", nullable: false),
                    Temperature = table.Column<double>(type: "REAL", nullable: false),
                    RainVolume = table.Column<double>(type: "REAL", nullable: false),
                    SnowVolume = table.Column<double>(type: "REAL", nullable: false),
                    Visability = table.Column<double>(type: "REAL", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "REAL", nullable: false),
                    WorkingPriceMinus = table.Column<double>(type: "REAL", nullable: false),
                    EventCaseId = table.Column<Guid>(type: "TEXT", nullable: false),
                    Timestamp = table.Column<DateTime>(type: "TEXT", nullable: false),
                    ActiveEnergyPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ActiveEnergyMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactiveEnergyPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactiveEnergyMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    ActivePowerPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ActivePowerMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactivePowerPlus = table.Column<int>(type: "INTEGER", nullable: false),
                    ReactivePowerMinus = table.Column<int>(type: "INTEGER", nullable: false),
                    PrepaymentCounter = table.Column<int>(type: "INTEGER", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MeterDataHistory", x => x.Id);
                    table.ForeignKey(
                        name: "FK_MeterDataHistory_EventCase_EventCaseId",
                        column: x => x.EventCaseId,
                        principalTable: "EventCase",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_MeterDataHistory_EventCaseId",
                table: "MeterDataHistory",
                column: "EventCaseId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "BatterySystem");

            migrationBuilder.DropTable(
                name: "Charge");

            migrationBuilder.DropTable(
                name: "ECommunity");

            migrationBuilder.DropTable(
                name: "GridPriceRate");

            migrationBuilder.DropTable(
                name: "Member");

            migrationBuilder.DropTable(
                name: "MeterDataHistory");

            migrationBuilder.DropTable(
                name: "MeterDataProfile");

            migrationBuilder.DropTable(
                name: "MeterDataRealTime");

            migrationBuilder.DropTable(
                name: "PVSystem");

            migrationBuilder.DropTable(
                name: "SmartMeter");

            migrationBuilder.DropTable(
                name: "SupplierPriceRate");

            migrationBuilder.DropTable(
                name: "EventCase");
        }
    }
}
