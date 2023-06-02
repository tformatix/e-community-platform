@echo ############## start generating REST files ##############
@openapi-generator-cli generate -i https://e-community.azurewebsites.net/swagger/v1/swagger.json -g kotlin -o .\swagger-generated --additional-properties="packageName=at.fhooe.ecommunity.data.remote.openapi.cloud"
@echo ############## finished ##############
@PAUSE