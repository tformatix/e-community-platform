@echo ############## start copying REST files ##############
@RD /S /Q "..\GenericDeliveryTerminal\app\src\main\java\org"
@Xcopy .\swagger-generated\src\main\kotlin ..\..\compose\app\src\main\java /E/H/C/I/Y
@RD /S /Q ".\swagger-generated"
@echo ############## finished ##############
@PAUSE
