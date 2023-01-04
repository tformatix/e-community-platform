using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class AddedMutedRemovedAck : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Acknowledged",
                table: "MeterDataMonitoring");

            migrationBuilder.AddColumn<bool>(
                name: "IsNonComplianceMuted",
                table: "SmartMeter",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "IsNonComplianceMuted",
                table: "SmartMeter");

            migrationBuilder.AddColumn<bool>(
                name: "Acknowledged",
                table: "MeterDataMonitoring",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }
    }
}
