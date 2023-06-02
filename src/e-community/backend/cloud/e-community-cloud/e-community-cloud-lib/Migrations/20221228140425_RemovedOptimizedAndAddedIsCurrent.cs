using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class RemovedOptimizedAndAddedIsCurrent : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "Optimized",
                table: "SmartMeterPortion");

            migrationBuilder.AddColumn<bool>(
                name: "IsCurrent",
                table: "ECommunityDistribution",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "IsCurrent",
                table: "ECommunityDistribution");

            migrationBuilder.AddColumn<bool>(
                name: "Optimized",
                table: "SmartMeterPortion",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }
    }
}
