import typer
import os
import json
from ast import literal_eval
import web3
from web3.middleware import geth_poa_middleware

GOQUORUM_NODE_URL = 'http://127.0.0.1:22000'
GOQUORUM_NODE = ""

# CLI object
cli = typer.Typer()

# web3 object, inject middleware because of private fork of ethereum
w3 = web3.Web3(web3.HTTPProvider(GOQUORUM_NODE_URL))


# CLI COMMANDS #
# READ ETHEREUM ACCOUNT
GOQUORUM_READ_ADDRESS = "cat $GOQUORUM_NODE/data/keystore/accountAddress"
GOQUORUM_READ_PASSWORD = "cat $GOQUORUM_NODE/data/keystore/accountPassword"
GOQUORUM_READ_PRIVATE_KEY = "cat $GOQUORUM_NODE/data/keystore/accountPrivateKey"

# TRUFFLE #
# deploy new consent contract
TRUFFLE_DEPLOY_CONTRACT = 'cd ../../smart_contracts && truffle migrate deploy_consent_contract.js | grep "contract ' \
                          'address" | grep -o -E "0[xX][0-9a-fA-F]+"'

TRUFFLE_CONTRACT_BYTECODE = 'cat ../../smart_contracts/build/contracts/ConsentContract.json | grep -oP \'(?<="bytecode": ")[^"]*\''

TRUFFLE_READ_CONTRACT_ABI = 'cat ../../smart_contracts/build/contracts/ConsentContract.json'

# UNLOCK ETHEREUM ACCOUNT
GOQUORUM_ACCOUNT_UNLOCK = 'curl -s -H "Content-Type: application/json" -X POST --data \'{"jsonrpc":"2.0",' \
                          '"method":"personal_unlockAccount","params":["%s", "%s"],"id":1}\' ' \
                          '%s'

GOQUORUM_ACCOUNT_BALANCE = 'curl -s -H "Content-Type: application/json" -X POST --data \'{"jsonrpc":"2.0",' \
                          '"method":"eth_getBalance","params":["%s", "latest"],"id":1}\' ' \
                          '%s'


class GoQuorumNode:
    """ object for a goquorum (blockchain) node """
    deployedContractAddress = ""

    def __init__(self, account_address, account_password, account_private_key):
        self.accountAddress = w3.toChecksumAddress(account_address)
        self.accountPassword = account_password
        self.accountPrivateKey = account_private_key

    def set_deployed_contract_address(self, deployed_address):
        self.deployedContractAddress = deployed_address


@cli.command()
def deploy_new_consent_contract(address_consenter: str = typer.Option(..., "--adr-con", "-ac", help="address of the consenter")):
    """deploys a new consent contract to the blockchain and returns the deployed address"""

    if not w3.isConnected():
        print("Ethereum Node not running...")
        return

    # unlock account first
    unlock_account(GOQUORUM_NODE)

    # deploy contract with truffle and get the deployed contract address
    deploy_contract_read = os.popen(TRUFFLE_DEPLOY_CONTRACT)
    deployed_address = deploy_contract_read.read().strip()
    print(f"deployedAddress: {deployed_address}")

    # save deployed contract address, needed for updating values later
    GOQUORUM_NODE.deployedContractAddress = deployed_address

    # read abi string from builded contract
    contract_abi_read = os.popen(TRUFFLE_READ_CONTRACT_ABI)
    contract_abi = json.loads(contract_abi_read.read())["abi"]

    # extract the builded bytecode
    contract_bytecode_read = os.popen(TRUFFLE_CONTRACT_BYTECODE)
    contract_bytecode = contract_bytecode_read.read().strip()
    contract_bytecode_hex = hex(int(contract_bytecode, 16))

    # build check from consenter's address
    checksum_address_consenter = w3.toChecksumAddress(address_consenter)
    w3.middleware_onion.inject(geth_poa_middleware, layer=0)
    w3.handleRevert = True

    # create contract object and setConsenter(address_consenter) functions.greet().call()
    ConsentContract = w3.eth.contract(address=deployed_address, abi=contract_abi, bytecode=contract_bytecode_hex)

    constructor_tx = ConsentContract.constructor(checksum_address_consenter).build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    tx_create = w3.eth.account.sign_transaction(constructor_tx, GOQUORUM_NODE.accountPrivateKey)
    tx_hash = w3.eth.send_raw_transaction(tx_create.rawTransaction)
    tx_receipt = w3.eth.wait_for_transaction_receipt(tx_hash)

    print(f'Tx successful with hash: { tx_receipt.contractAddress }')

    ConsentContract = w3.eth.contract(address=tx_receipt.contractAddress, abi=contract_abi, bytecode=contract_bytecode_hex)

    print(ConsentContract.functions.getValue().call())


@cli.command()
def account_unlock():
    """temporaly unlocks the local ethereum account"""

    account_unlock = unlock_account(GOQUORUM_NODE)

    if "error" in account_unlock:
        print("ERROR: check configuration")
    elif account_unlock["result"]:
        print("ACCOUNT UNLOCKED")


@cli.command()
def account_balance():
    """returns the current balance of the ethereum account in ETH"""

    account_balance_read = os.popen(GOQUORUM_ACCOUNT_BALANCE % (GOQUORUM_NODE.accountAddress, GOQUORUM_NODE_URL))
    account_balance = json.loads(account_balance_read.read())

    if "error" in account_balance:
        print("ERROR: check configuration")
    elif account_balance["result"]:
        eth_value = literal_eval(account_balance["result"])
        print(f'{str(eth_value)} ETH')


def get_account_details():
    """
    reads the account details from the goquorum (blockchain) node
    accountAddress => ethereum wallet
    accountPassword => password for the wallet
    accountPrivateKey => private key for signing transactions
    $GOQUORUM_NODE needs to be set
    """
    account_address_read = os.popen(GOQUORUM_READ_ADDRESS)
    account_address = account_address_read.read()

    account_password_read = os.popen(GOQUORUM_READ_PASSWORD)
    account_password = account_password_read.read()

    account_private_key_read = os.popen(GOQUORUM_READ_PRIVATE_KEY)
    account_private_key = account_private_key_read.read()

    return GoQuorumNode(account_address, account_password, account_private_key)


def unlock_account(node):
    """temporaly unlocks the local ethereum account"""
    account_unlock_read = os.popen(GOQUORUM_ACCOUNT_UNLOCK % (node.accountAddress, node.accountPassword, GOQUORUM_NODE_URL))
    account_unlock = json.loads(account_unlock_read.read())
    return account_unlock


if __name__ == '__main__':
    GOQUORUM_NODE = get_account_details()
    cli()
