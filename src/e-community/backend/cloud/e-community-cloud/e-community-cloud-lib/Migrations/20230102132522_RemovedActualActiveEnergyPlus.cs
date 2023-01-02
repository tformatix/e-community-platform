using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class RemovedActualActiveEnergyPlus : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "ActualActiveEnergyPlus",
                table: "SmartMeterPortion");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "ActualActiveEnergyPlus",
                table: "SmartMeterPortion",
                type: "int",
                nullable: true);
        }
    }
}
