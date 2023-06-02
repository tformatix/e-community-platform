using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class ChangedSomeFields : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.RenameColumn(
                name: "IsCurrent",
                table: "Monitoring",
                newName: "IsCalculating");

            migrationBuilder.RenameColumn(
                name: "IsCurrent",
                table: "ECommunityDistribution",
                newName: "IsCalculating");

            migrationBuilder.AddColumn<bool>(
                name: "NonCompliance",
                table: "MeterDataMonitoring",
                type: "bit",
                nullable: false,
                defaultValue: false);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "NonCompliance",
                table: "MeterDataMonitoring");

            migrationBuilder.RenameColumn(
                name: "IsCalculating",
                table: "Monitoring",
                newName: "IsCurrent");

            migrationBuilder.RenameColumn(
                name: "IsCalculating",
                table: "ECommunityDistribution",
                newName: "IsCurrent");
        }
    }
}
