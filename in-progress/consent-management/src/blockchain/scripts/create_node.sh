#!/bin/bash

echo "##### CREATE NEW BLOCKCHAIN NODE #####"

# script requires nodejs and npm
if ! command -v npx --version &> /dev/null
then
    echo "npx could not be found, please install nodejs and npm"
    exit
fi

echo "##### DELETE OLD CONFIG #####"
rm -rf node_gen

# read password from console, used for account unlocking and authentication
password="";

echo "Provide password for Node: "
read password

echo "##### GENERATING KEYS #####"
npx quorum-genesis-tool --consensus qbft --chainID 300319982022 --blockperiod 5 --requestTimeout 10 --epochLength 30000 --difficulty 1 --gasLimit '0xFFFFFF' --coinbase '0x0000000000000000000000000000000000000000' --validators 1 --members 0 --bootnodes 0 --outputPath 'node_gen' --accountPassword $password


echo "##### NODE GENERATED, COPY FILES #####"
# change to node_gen and rename build directory
cd node_gen
cp -r 20* generated
rm -rf 20*

# create node directory
mkdir -p Node/data/keystore

# copy files to node directory
cp generated/validator0/account* Node/data/keystore/
cp generated/validator0/nodekey* Node/data/
cp generated/validator0/address Node/data/

# delete generated folder
rm -rf generated/

echo "##### LOAD BLOCKCHAIN CONFIG #####"
cp ../../config/* Node/data

echo "##### NODE files created ######"
echo "=> node_gen/Node"