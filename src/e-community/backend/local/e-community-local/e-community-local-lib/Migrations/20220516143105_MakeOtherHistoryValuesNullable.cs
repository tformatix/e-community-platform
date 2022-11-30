using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace e_community_local_lib.Migrations
{
    public partial class MakeOtherHistoryValuesNullable : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MeterDataHistory_EventCase_EventCaseId",
                table: "MeterDataHistory");

            migrationBuilder.AlterColumn<double>(
                name: "WorkingPricePlus",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "REAL");

            migrationBuilder.AlterColumn<double>(
                name: "WorkingPriceMinus",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "REAL");

            migrationBuilder.AlterColumn<double>(
                name: "Visability",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "REAL");

            migrationBuilder.AlterColumn<double>(
                name: "Temperature",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "REAL");

            migrationBuilder.AlterColumn<double>(
                name: "SnowVolume",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "REAL");

            migrationBuilder.AlterColumn<double>(
                name: "RainVolume",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "REAL");

            migrationBuilder.AlterColumn<Guid>(
                name: "EventCaseId",
                table: "MeterDataHistory",
                type: "TEXT",
                nullable: true,
                oldClrType: typeof(Guid),
                oldType: "TEXT");

            migrationBuilder.AlterColumn<double>(
                name: "Cloudiness",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: true,
                oldClrType: typeof(double),
                oldType: "REAL");

            migrationBuilder.AddForeignKey(
                name: "FK_MeterDataHistory_EventCase_EventCaseId",
                table: "MeterDataHistory",
                column: "EventCaseId",
                principalTable: "EventCase",
                principalColumn: "Id");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_MeterDataHistory_EventCase_EventCaseId",
                table: "MeterDataHistory");

            migrationBuilder.AlterColumn<double>(
                name: "WorkingPricePlus",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "REAL",
                oldNullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "WorkingPriceMinus",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "REAL",
                oldNullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "Visability",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "REAL",
                oldNullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "Temperature",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "REAL",
                oldNullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "SnowVolume",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "REAL",
                oldNullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "RainVolume",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "REAL",
                oldNullable: true);

            migrationBuilder.AlterColumn<Guid>(
                name: "EventCaseId",
                table: "MeterDataHistory",
                type: "TEXT",
                nullable: false,
                defaultValue: new Guid("00000000-0000-0000-0000-000000000000"),
                oldClrType: typeof(Guid),
                oldType: "TEXT",
                oldNullable: true);

            migrationBuilder.AlterColumn<double>(
                name: "Cloudiness",
                table: "MeterDataHistory",
                type: "REAL",
                nullable: false,
                defaultValue: 0.0,
                oldClrType: typeof(double),
                oldType: "REAL",
                oldNullable: true);

            migrationBuilder.AddForeignKey(
                name: "FK_MeterDataHistory_EventCase_EventCaseId",
                table: "MeterDataHistory",
                column: "EventCaseId",
                principalTable: "EventCase",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
