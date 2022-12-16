import json
from web3 import Web3
import asyncio

GOQUORUM_NODE_URL = 'http://127.0.0.1:22000'

# web3 object
web3 = Web3(Web3.HTTPProvider(GOQUORUM_NODE_URL))

# address and abi
contract_address = '0x1022f7Cd14f1dd4ba01B2b856e527008Dca5E415'
contract_abi = json.loads('[    {      "anonymous": false,      "inputs": [        {          "indexed": false,          "internalType": "int256",          "name": "value",          "type": "int256"        }      ],      "name": "NumberChanged",      "type": "event"    },    {      "inputs": [        {          "internalType": "int256",          "name": "_value",          "type": "int256"        }      ],      "name": "newNumber",      "outputs": [],      "stateMutability": "nonpayable",      "type": "function"    }  ]')

contract = web3.eth.contract(address=contract_address, abi=contract_abi)


# define function to handle events and print to the console
def handle_event(event):
    print(Web3.toJSON(event))
    # and whatever


# asynchronous defined function to loop
# this loop sets up an event filter and is looking for new entires for the "PairCreated" event
# this loop runs on a poll interval
async def log_loop(event_filter, poll_interval):
    while True:
        for NumberChanged in event_filter.get_new_entries():
            handle_event(NumberChanged)
        await asyncio.sleep(poll_interval)


# when main is called
# create a filter for the latest block and look for the "PairCreated" event for the uniswap factory contract
# run an async loop
# try to run the log_loop function above every 2 seconds
def main():
    event_filter = contract.events.NumberChanged.createFilter(fromBlock='latest')
    #block_filter = web3.eth.filter('latest')
    # tx_filter = web3.eth.filter('pending')
    loop = asyncio.get_event_loop()
    try:
        loop.run_until_complete(
            asyncio.gather(
                log_loop(event_filter, 2)))
        # log_loop(block_filter, 2),
        # log_loop(tx_filter, 2)))
    finally:
        # close loop to free up system resources
        loop.close()


if __name__ == "__main__":
    main()
