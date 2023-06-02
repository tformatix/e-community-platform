import typer
import os
import json
from ast import literal_eval
import web3
from web3.middleware import geth_poa_middleware
from classes.ConsentContract import ConsentContract
from classes.GoQuorumNode import GoQuorumNode

# Blockchain Node
GOQUORUM_NODE_URL = 'http://127.0.0.1:22000'
GOQUORUM_NODE = ""

# CLI object
cli = typer.Typer()

# web3 object, inject middleware because of private fork of ethereum
w3 = web3.Web3(web3.HTTPProvider(GOQUORUM_NODE_URL))
w3.middleware_onion.inject(geth_poa_middleware, layer=0)
w3.handleRevert = True

# CLI COMMANDS #
GOQUORUM_READ_ADDRESS = "cat /home/pi/blockchain/Node/data/keystore/accountAddress"
GOQUORUM_READ_PASSWORD = "cat /home/pi/blockchain/Node/data/keystore/accountPassword"
GOQUORUM_READ_PRIVATE_KEY = "cat /home/pi/blockchain/Node/data/keystore/accountPrivateKey"

TRUFFLE_CONTRACT_BYTECODE = 'cat /home/pi/backend/Util/Extensions/Python/build/ConsentContract.json | grep -oP \'(?<="bytecode": ")[^"]*\''
TRUFFLE_READ_CONTRACT_ABI = 'cat /home/pi/backend/Util/Extensions/Python/build/ConsentContract.json'

TRUFFLE_CONTRACT_BYTECODE_FACTORY = 'cat /home/pi/backend/Util/Extensions/Python/build/ConsentContractFactory.json | grep -oP \'(?<="bytecode": ")[^"]*\''
TRUFFLE_READ_CONTRACT_ABI_FACTORY = 'cat /home/pi/backend/Util/Extensions/Python/build/ConsentContractFactory.json'


# UNLOCK ETHEREUM ACCOUNT
GOQUORUM_ACCOUNT_UNLOCK = 'curl -s -H "Content-Type: application/json" -X POST --data \'{"jsonrpc":"2.0",' \
                          '"method":"personal_unlockAccount","params":["%s", "%s"],"id":1}\' ' \
                          '%s'

@cli.command()
def test():
    print("test")

@cli.command()
def deploy():
    """deploys a new consent contract to the blockchain and returns the deployed address"""

    # unlock account first
    if not unlock_account(GOQUORUM_NODE):
        print("Cannot unlock ethereum account...")
        return

    # create contract object and deploy it be calling the constructor
    consent_contract_factory = w3.eth.contract(abi=GOQUORUM_NODE.abiFactory,
                                               bytecode=GOQUORUM_NODE.bytecodeFactory)

    constructor_tx = consent_contract_factory.constructor().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make constructor transaction
    deployed_contract_factory_address = make_contract_tx(constructor_tx)

    print(f'deployedAddress (factory): {deployed_contract_factory_address}')


def get_account_details():
    """
    reads the account details from the goquorum (blockchain) node
    accountAddress => ethereum wallet
    accountPassword => password for the wallet
    accountPrivateKey => private key for signing transactions
    """
    account_address_read = os.popen(GOQUORUM_READ_ADDRESS)
    account_address = account_address_read.read()

    account_password_read = os.popen(GOQUORUM_READ_PASSWORD)
    account_password = account_password_read.read()

    account_private_key_read = os.popen(GOQUORUM_READ_PRIVATE_KEY)
    account_private_key = account_private_key_read.read()

    node = GoQuorumNode(w3.toChecksumAddress(account_address), account_password, account_private_key)

    # abi - factory contract
    abi_factory_read = os.popen(TRUFFLE_READ_CONTRACT_ABI_FACTORY)
    abi_factory = json.loads(abi_factory_read.read())["abi"]

    # bytecode - factory contract
    bytecode_factory_read = os.popen(TRUFFLE_CONTRACT_BYTECODE_FACTORY)
    bytecode_factory = bytecode_factory_read.read().strip()
    bytecode_factory = hex(int(bytecode_factory, 16))

    node.set_build_config(abi_factory, "", bytecode_factory, "")

    return node


def unlock_account(node):
    """temporaly unlocks the local ethereum account"""
    account_unlock_read = os.popen(
        GOQUORUM_ACCOUNT_UNLOCK % (node.accountAddress, node.accountPassword, GOQUORUM_NODE_URL))
    account_unlock = json.loads(account_unlock_read.read())

    if "error" in account_unlock:
        print("ERROR: check configuration")
        return False
    elif account_unlock["result"]:
        print("ACCOUNT UNLOCKED")

    return True


def make_contract_tx(tx):
    """signed and create a transaction for a smart contract => returns new contractAddress"""
    tx_create = w3.eth.account.sign_transaction(tx, GOQUORUM_NODE.accountPrivateKey)
    tx_hash = w3.eth.send_raw_transaction(tx_create.rawTransaction)
    tx_receipt = w3.eth.wait_for_transaction_receipt(tx_hash)
    # print(tx_receipt['transactionHash'])
    return tx_receipt.contractAddress


if __name__ == '__main__':
    # get local GOQUORUM node details
    GOQUORUM_NODE = get_account_details()

    if not w3.isConnected():
        print("Ethereum Node not running...")
    else:
        cli()
