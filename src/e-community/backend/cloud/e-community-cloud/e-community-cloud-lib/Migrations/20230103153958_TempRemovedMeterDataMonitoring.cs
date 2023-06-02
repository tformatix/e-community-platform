using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class TempRemovedMeterDataMonitoring : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "MeterDataMonitoring");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "MeterDataMonitoring",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    SmartMeterId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Acknowledged = table.Column<bool>(type: "bit", nullable: false),
                    ActiveEnergyMinus = table.Column<int>(type: "int", nullable: true),
                    ActiveEnergyPlus = table.Column<int>(type: "int", nullable: true),
                    Timestamp = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MeterDataMonitoring", x => x.Id);
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
    }
}
