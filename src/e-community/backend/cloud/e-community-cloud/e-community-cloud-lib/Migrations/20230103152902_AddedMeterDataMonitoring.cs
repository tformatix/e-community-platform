using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class AddedMeterDataMonitoring : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "MeterDataMonitoring",
                columns: table => new
                {
                    Id = table.Column<int>(type: "int", nullable: false)
                        .Annotation("SqlServer:Identity", "1, 1"),
                    SmartMeterId = table.Column<int>(type: "int", nullable: false),
                    SmartMeterId1 = table.Column<Guid>(type: "uniqueidentifier", nullable: true),
                    Timestamp = table.Column<DateTime>(type: "datetime2", nullable: false),
                    ActiveEnergyPlus = table.Column<int>(type: "int", nullable: false),
                    ActiveEnergyMinus = table.Column<int>(type: "int", nullable: false),
                    Acknowledged = table.Column<bool>(type: "bit", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MeterDataMonitoring", x => x.Id);
                    table.ForeignKey(
                        name: "FK_MeterDataMonitoring_SmartMeter_SmartMeterId1",
                        column: x => x.SmartMeterId1,
                        principalTable: "SmartMeter",
                        principalColumn: "Id");
                });

            migrationBuilder.CreateIndex(
                name: "IX_MeterDataMonitoring_SmartMeterId1",
                table: "MeterDataMonitoring",
                column: "SmartMeterId1");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "MeterDataMonitoring");
        }
    }
}
