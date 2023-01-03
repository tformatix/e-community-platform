using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class TryAddAgain : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {

            migrationBuilder.CreateTable(
                name: "Monitoring",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    Timestamp = table.Column<DateTime>(type: "datetime2", nullable: false),
                    IsCurrent = table.Column<bool>(type: "bit", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_Monitoring", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "MeterDataMonitoring",
                columns: table => new
                {
                    MonitoringId = table.Column<int>(type: "int", nullable: false),
                    SmartMeterId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    ActiveEnergyPlus = table.Column<int>(type: "int", nullable: true),
                    ActiveEnergyMinus = table.Column<int>(type: "int", nullable: true),
                    Acknowledged = table.Column<bool>(type: "bit", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MeterDataMonitoring", x => new { x.MonitoringId, x.SmartMeterId });
                    table.ForeignKey(
                        name: "FK_MeterDataMonitoring_Monitoring_MonitoringId",
                        column: x => x.MonitoringId,
                        principalTable: "Monitoring",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_MeterDataMonitoring_SmartMeter_SmartMeterId",
                        column: x => x.SmartMeterId,
                        principalTable: "SmartMeter",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_MeterDataMonitoring_SmartMeterId",
                table: "MeterDataMonitoring",
                column: "SmartMeterId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "MeterDataMonitoring");

            migrationBuilder.DropTable(
                name: "Monitoring");
        }
    }
}
