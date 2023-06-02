using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class ECommunityMemberStatusToECommunityMembership : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "ECommunityMemberStatus");

            migrationBuilder.CreateTable(
                name: "ECommunityMemberships",
                columns: table => new
                {
                    ECommunityId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    MemberId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    DistributionPercentage = table.Column<double>(type: "float", nullable: false),
                    EntryDate = table.Column<DateTime>(type: "datetime2", nullable: false),
                    LeftDate = table.Column<DateTime>(type: "datetime2", nullable: false),
                    ECommunityPermission = table.Column<int>(type: "int", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunityMemberships", x => new { x.ECommunityId, x.MemberId });
                    table.ForeignKey(
                        name: "FK_ECommunityMemberships_ECommunity_ECommunityId",
                        column: x => x.ECommunityId,
                        principalTable: "ECommunity",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_ECommunityMemberships_Member_MemberId",
                        column: x => x.MemberId,
                        principalTable: "Member",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_ECommunityMemberships_MemberId",
                table: "ECommunityMemberships",
                column: "MemberId");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropTable(
                name: "ECommunityMemberships");

            migrationBuilder.CreateTable(
                name: "ECommunityMemberStatus",
                columns: table => new
                {
                    ECommunityId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    MemberId = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    DistributionPercentage = table.Column<double>(type: "float", nullable: false),
                    ECommunityPermission = table.Column<int>(type: "int", nullable: false),
                    EntryDate = table.Column<DateTime>(type: "datetime2", nullable: false),
                    LeftDate = table.Column<DateTime>(type: "datetime2", nullable: false)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunityMemberStatus", x => new { x.ECommunityId, x.MemberId });
                    table.ForeignKey(
                        name: "FK_ECommunityMemberStatus_ECommunity_ECommunityId",
                        column: x => x.ECommunityId,
                        principalTable: "ECommunity",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                    table.ForeignKey(
                        name: "FK_ECommunityMemberStatus_Member_MemberId",
                        column: x => x.MemberId,
                        principalTable: "Member",
                        principalColumn: "Id",
                        onDelete: ReferentialAction.Cascade);
                });

            migrationBuilder.CreateIndex(
                name: "IX_ECommunityMemberStatus_MemberId",
                table: "ECommunityMemberStatus",
                column: "MemberId");
        }
    }
}
