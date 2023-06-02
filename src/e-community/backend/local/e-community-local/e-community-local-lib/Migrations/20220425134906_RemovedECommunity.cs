using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_local_lib.Migrations
{
    public partial class RemovedECommunity : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "ECommunity");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "ECommunity",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "TEXT", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunity", x => x.Id);
                });
        }
    }
}
