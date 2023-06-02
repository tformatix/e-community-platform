using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class AddedMemberFCMToken : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.CreateTable(
                name: "MemberFCMToken",
                columns: table => new
                {
                    MemberId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Token = table.Column<string>(type: "nvarchar(450)", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_MemberFCMToken", x => new { x.MemberId, x.Token });
                    table.ForeignKey(
                        name: "FK_MemberFCMToken_Member_MemberId",
                        column: x => x.MemberId,
                        principalTable: "Member",
                        principalColumn: "Id");
                });
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "MemberFCMToken");
        }
    }
}
