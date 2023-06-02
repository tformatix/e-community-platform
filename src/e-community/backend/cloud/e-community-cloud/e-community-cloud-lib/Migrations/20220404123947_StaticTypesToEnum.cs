using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class StaticTypesToEnum : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_ECommunity_DistributionMode_DistributionModeId",
                table: "ECommunity");

            migrationBuilder.DropForeignKey(
                name: "FK_ECommunityMemberStatus_ECommunityPermission_ECommunityPermissionId",
                table: "ECommunityMemberStatus");

            migrationBuilder.DropForeignKey(
                name: "FK_GridPriceRate_PriceRateType_PriceRateTypeId",
                table: "GridPriceRate");

            migrationBuilder.DropForeignKey(
                name: "FK_Member_SupplyMode_SupplyModeId",
                table: "Member");

            migrationBuilder.DropForeignKey(
                name: "FK_SupplierPriceRate_PriceRateType_PriceRateTypeId",
                table: "SupplierPriceRate");

            migrationBuilder.DropTable(
                name: "DistributionMode");

            migrationBuilder.DropTable(
                name: "ECommunityPermission");

            migrationBuilder.DropTable(
                name: "PriceRateType");

            migrationBuilder.DropTable(
                name: "SupplyMode");

            migrationBuilder.DropIndex(
                name: "IX_SupplierPriceRate_PriceRateTypeId",
                table: "SupplierPriceRate");

            migrationBuilder.DropIndex(
                name: "IX_Member_SupplyModeId",
                table: "Member");

            migrationBuilder.DropIndex(
                name: "IX_GridPriceRate_PriceRateTypeId",
                table: "GridPriceRate");

            migrationBuilder.DropIndex(
                name: "IX_ECommunityMemberStatus_ECommunityPermissionId",
                table: "ECommunityMemberStatus");

            migrationBuilder.DropIndex(
                name: "IX_ECommunity_DistributionModeId",
                table: "ECommunity");

            migrationBuilder.DropColumn(
                name: "PriceRateTypeId",
                table: "SupplierPriceRate");

            migrationBuilder.DropColumn(
                name: "SupplyModeId",
                table: "Member");

            migrationBuilder.DropColumn(
                name: "PriceRateTypeId",
                table: "GridPriceRate");

            migrationBuilder.DropColumn(
                name: "ECommunityPermissionId",
                table: "ECommunityMemberStatus");

            migrationBuilder.DropColumn(
                name: "DistributionModeId",
                table: "ECommunity");

            migrationBuilder.AddColumn<int>(
                name: "PriceRateType",
                table: "SupplierPriceRate",
                type: "int",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "SupplyMode",
                table: "Member",
                type: "int",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "PriceRateType",
                table: "GridPriceRate",
                type: "int",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "ECommunityPermission",
                table: "ECommunityMemberStatus",
                type: "int",
                nullable: false,
                defaultValue: 0);

            migrationBuilder.AddColumn<int>(
                name: "DistributionMode",
                table: "ECommunity",
                type: "int",
                nullable: false,
                defaultValue: 0);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "PriceRateType",
                table: "SupplierPriceRate");

            migrationBuilder.DropColumn(
                name: "SupplyMode",
                table: "Member");

            migrationBuilder.DropColumn(
                name: "PriceRateType",
                table: "GridPriceRate");

            migrationBuilder.DropColumn(
                name: "ECommunityPermission",
                table: "ECommunityMemberStatus");

            migrationBuilder.DropColumn(
                name: "DistributionMode",
                table: "ECommunity");

            migrationBuilder.AddColumn<Guid>(
                name: "PriceRateTypeId",
                table: "SupplierPriceRate",
                type: "uniqueidentifier",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.AddColumn<Guid>(
                name: "SupplyModeId",
                table: "Member",
                type: "uniqueidentifier",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.AddColumn<Guid>(
                name: "PriceRateTypeId",
                table: "GridPriceRate",
                type: "uniqueidentifier",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.AddColumn<Guid>(
                name: "ECommunityPermissionId",
                table: "ECommunityMemberStatus",
                type: "uniqueidentifier",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.AddColumn<Guid>(
                name: "DistributionModeId",
                table: "ECommunity",
                type: "uniqueidentifier",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"));

            migrationBuilder.CreateTable(
                name: "DistributionMode",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_DistributionMode", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "ECommunityPermission",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_ECommunityPermission", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "PriceRateType",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_PriceRateType", x => x.Id);
                });

            migrationBuilder.CreateTable(
                name: "SupplyMode",
                columns: table => new
                {
                    Id = table.Column<Guid>(type: "uniqueidentifier", nullable: false),
                    Name = table.Column<string>(type: "nvarchar(max)", nullable: true)
                },
                constraints: table =>
                {
                    table.PrimaryKey("PK_SupplyMode", x => x.Id);
                });

            migrationBuilder.CreateIndex(
                name: "IX_SupplierPriceRate_PriceRateTypeId",
                table: "SupplierPriceRate",
                column: "PriceRateTypeId");

            migrationBuilder.CreateIndex(
                name: "IX_Member_SupplyModeId",
                table: "Member",
                column: "SupplyModeId");

            migrationBuilder.CreateIndex(
                name: "IX_GridPriceRate_PriceRateTypeId",
                table: "GridPriceRate",
                column: "PriceRateTypeId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunityMemberStatus_ECommunityPermissionId",
                table: "ECommunityMemberStatus",
                column: "ECommunityPermissionId");

            migrationBuilder.CreateIndex(
                name: "IX_ECommunity_DistributionModeId",
                table: "ECommunity",
                column: "DistributionModeId");

            migrationBuilder.AddForeignKey(
                name: "FK_ECommunity_DistributionMode_DistributionModeId",
                table: "ECommunity",
                column: "DistributionModeId",
                principalTable: "DistributionMode",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_ECommunityMemberStatus_ECommunityPermission_ECommunityPermissionId",
                table: "ECommunityMemberStatus",
                column: "ECommunityPermissionId",
                principalTable: "ECommunityPermission",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_GridPriceRate_PriceRateType_PriceRateTypeId",
                table: "GridPriceRate",
                column: "PriceRateTypeId",
                principalTable: "PriceRateType",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_Member_SupplyMode_SupplyModeId",
                table: "Member",
                column: "SupplyModeId",
                principalTable: "SupplyMode",
                principalColumn: "Id");

            migrationBuilder.AddForeignKey(
                name: "FK_SupplierPriceRate_PriceRateType_PriceRateTypeId",
                table: "SupplierPriceRate",
                column: "PriceRateTypeId",
                principalTable: "PriceRateType",
                principalColumn: "Id");
        }
    }
}
