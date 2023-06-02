using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class RemoveAESAndAPIKey : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "AESKey",
                table: "SmartMeter");

            migrationBuilder.DropColumn(
                name: "APIKey",
                table: "SmartMeter");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<string>(
                name: "AESKey",
                table: "SmartMeter",
                type: "nvarchar(max)",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "APIKey",
                table: "SmartMeter",
                type: "nvarchar(max)",
                nullable: true);
        }
    }
}
