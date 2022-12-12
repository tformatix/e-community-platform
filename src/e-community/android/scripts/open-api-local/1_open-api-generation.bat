@echo ############## start generating REST files ##############
@openapi-generator-cli generate -i 1_swagger.json -g kotlin -o .\swagger-generated --additional-properties="packageName=at.fhooe.ecommunity.data.remote.openapi.local"
@echo ############## finished ##############
@PAUSE