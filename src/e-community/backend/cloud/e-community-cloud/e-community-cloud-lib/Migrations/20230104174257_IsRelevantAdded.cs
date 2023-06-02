using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class IsRelevantAdded : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<bool>(
                name: "IsRelevant",
                table: "SmartMeterPortion",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "IsRelevant",
                table: "SmartMeterPortion");
        }
    }
}
