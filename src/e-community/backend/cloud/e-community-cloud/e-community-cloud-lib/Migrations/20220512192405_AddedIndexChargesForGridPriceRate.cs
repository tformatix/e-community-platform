using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_cloud_lib.Migrations
{
    public partial class AddedIndexChargesForGridPriceRate : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<Guid>(
                name: "GridPriceRateId",
                table: "Charge",
                type: "uniqueidentifier",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_Charge_GridPriceRateId",
                table: "Charge",
                column: "GridPriceRateId");

            migrationBuilder.AddForeignKey(
                name: "FK_Charge_GridPriceRate_GridPriceRateId",
                table: "Charge",
                column: "GridPriceRateId",
                principalTable: "GridPriceRate",
                principalColumn: "Id");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_Charge_GridPriceRate_GridPriceRateId",
                table: "Charge");

            migrationBuilder.DropIndex(
                name: "IX_Charge_GridPriceRateId",
                table: "Charge");

            migrationBuilder.DropColumn(
                name: "GridPriceRateId",
                table: "Charge");
        }
    }
}
