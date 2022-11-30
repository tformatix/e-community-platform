using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class PluralMistake : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_ECommunityMemberships_ECommunity_ECommunityId",
                table: "ECommunityMemberships");

            migrationBuilder.DropForeignKey(
                name: "FK_ECommunityMemberships_Member_MemberId",
                table: "ECommunityMemberships");

            migrationBuilder.DropPrimaryKey(
                name: "PK_ECommunityMemberships",
                table: "ECommunityMemberships");

            migrationBuilder.RenameTable(
                name: "ECommunityMemberships",
                newName: "ECommunityMembership");

            migrationBuilder.RenameIndex(
                name: "IX_ECommunityMemberships_MemberId",
                table: "ECommunityMembership",
                newName: "IX_ECommunityMembership_MemberId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_ECommunityMembership",
                table: "ECommunityMembership",
                columns: new[] { "ECommunityId", "MemberId" });

            migrationBuilder.AddForeignKey(
                name: "FK_ECommunityMembership_ECommunity_ECommunityId",
                table: "ECommunityMembership",
                column: "ECommunityId",
                principalTable: "ECommunity",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_ECommunityMembership_Member_MemberId",
                table: "ECommunityMembership",
                column: "MemberId",
                principalTable: "Member",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_ECommunityMembership_ECommunity_ECommunityId",
                table: "ECommunityMembership");

            migrationBuilder.DropForeignKey(
                name: "FK_ECommunityMembership_Member_MemberId",
                table: "ECommunityMembership");

            migrationBuilder.DropPrimaryKey(
                name: "PK_ECommunityMembership",
                table: "ECommunityMembership");

            migrationBuilder.RenameTable(
                name: "ECommunityMembership",
                newName: "ECommunityMemberships");

            migrationBuilder.RenameIndex(
                name: "IX_ECommunityMembership_MemberId",
                table: "ECommunityMemberships",
                newName: "IX_ECommunityMemberships_MemberId");

            migrationBuilder.AddPrimaryKey(
                name: "PK_ECommunityMemberships",
                table: "ECommunityMemberships",
                columns: new[] { "ECommunityId", "MemberId" });

            migrationBuilder.AddForeignKey(
                name: "FK_ECommunityMemberships_ECommunity_ECommunityId",
                table: "ECommunityMemberships",
                column: "ECommunityId",
                principalTable: "ECommunity",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);

            migrationBuilder.AddForeignKey(
                name: "FK_ECommunityMemberships_Member_MemberId",
                table: "ECommunityMemberships",
                column: "MemberId",
                principalTable: "Member",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
