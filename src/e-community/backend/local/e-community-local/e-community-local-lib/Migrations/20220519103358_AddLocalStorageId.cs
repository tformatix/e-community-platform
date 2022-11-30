using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_local_lib.Migrations
{
    public partial class AddLocalStorageId : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<Guid>(
                name: "LocalStorageId",
                table: "SmartMeter",
                type: "TEXT",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "LocalStorageId",
                table: "SmartMeter");
        }
    }
}
