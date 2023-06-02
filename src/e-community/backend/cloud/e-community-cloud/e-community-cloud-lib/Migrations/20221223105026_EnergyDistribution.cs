using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class EnergyDistribution : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "ECommunityTransaction");

            migrationBuilder.DropTable(
                name: "MeterDataECommunity");

            migrationBuilder.CreateTable(
                name: "ECommunityDistribution",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    ECommunityId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Timestamp = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunityDistribution", x => x.Id);
                    table.ForeignKey(
                        name: "FK_ECommunityDistribution_ECommunity_ECommunityId",
                        column: x => x.ECommunityId,
                        principalTable: "ECommunity",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateTable(
                name: "SmartMeterPortion",
                columns: table => new
                {
                    ECommunityDistributionId = table.Column<int>(type: "int", nullable: false),
                    SmartMeterId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    EstimatedActiveEnergyMinus = table.Column<int>(type: "int", nullable: true),
                    EstimatedActiveEnergyPlus = table.Column<int>(type: "int", nullable: true),
                    ActualActiveEnergyPlus = table.Column<int>(type: "int", nullable: true),
                    Portion = table.Column<int>(type: "int", nullable: true),
                    Flexibility = table.Column<int>(type: "int", nullable: true),
                    Acknowledged = table.Column<bool>(type: "bit", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SmartMeterPortion", x => new { x.ECommunityDistributionId, x.SmartMeterId });
                    table.ForeignKey(
                        name: "FK_SmartMeterPortion_ECommunityDistribution_ECommunityDistributionId",
                        column: x => x.ECommunityDistributionId,
                        principalTable: "ECommunityDistribution",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_SmartMeterPortion_SmartMeter_SmartMeterId",
                        column: x => x.SmartMeterId,
                        principalTable: "SmartMeter",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_ECommunityDistribution_ECommunityId",
                table: "ECommunityDistribution",
                column: "ECommunityId");

            migrationBuilder.CreateIndex(
                name: "IX_SmartMeterPortion_SmartMeterId",
                table: "SmartMeterPortion",
                column: "SmartMeterId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "SmartMeterPortion");

            migrationBuilder.DropTable(
                name: "ECommunityDistribution");

            migrationBuilder.CreateTable(
                name: "MeterDataECommunity",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ECommunityId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ActiveEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    ActiveEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    ActivePowerMinus = table.Column<int>(type: "int", nullable: false),
                    ActivePowerPlus = table.Column<int>(type: "int", nullable: false),
                    PrepaymentCounter = table.Column<int>(type: "int", nullable: false),
                    ReactiveEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    ReactiveEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    ReactivePowerMinus = table.Column<int>(type: "int", nullable: false),
                    ReactivePowerPlus = table.Column<int>(type: "int", nullable: false),
                    Timestamp = table.Column<DateTime>(type: "datetime2", nullable: false),
                    WorkingPriceMinus = table.Column<double>(type: "float", nullable: false),
                    WorkingPricePlus = table.Column<double>(type: "float", nullable: false)
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
                name: "ECommunityTransaction",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    MeterDataECommunityId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    SmartMeterId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ECommunityEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    ECommunityEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    ECommunityPriceMinus = table.Column<double>(type: "float", nullable: false),
                    ECommunityPricePlus = table.Column<double>(type: "float", nullable: false),
                    GridPricePlus = table.Column<double>(type: "float", nullable: false),
                    SupplierEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    SupplierEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    SupplierPriceMinus = table.Column<double>(type: "float", nullable: false),
                    SupplierPricePlus = table.Column<double>(type: "float", nullable: false)
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
                name: "IX_ECommunityTransaction_MeterDataECommunityId",
                table: "ECommunityTransaction",
                column: "MeterDataECommunityId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunityTransaction_SmartMeterId",
                table: "ECommunityTransaction",
                column: "SmartMeterId");

            migrationBuilder.CreateIndex(
                name: "IX_MeterDataECommunity_ECommunityId",
                table: "MeterDataECommunity",
                column: "ECommunityId");
        }
    }
}
