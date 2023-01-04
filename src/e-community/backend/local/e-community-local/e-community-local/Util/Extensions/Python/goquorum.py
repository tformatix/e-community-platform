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
w3.middleware_onion.inject(geth_poa_middleware, layer=0)
w3.handleRevert = True

# CLI COMMANDS #
# READ ETHEREUM ACCOUNT
# GOQUORUM_READ_ADDRESS = "cat /home/pi/blockchain/Node/data/keystore/accountAddress"
# GOQUORUM_READ_PASSWORD = "cat /home/pi/blockchain/Node/data/keystore/accountPassword"
# GOQUORUM_READ_PRIVATE_KEY = "cat /home/pi/blockchain/Node/data/keystore/accountPrivateKey"

GOQUORUM_READ_ADDRESS = "cat /home/michael/Documents/dev/network/QBFT-Network/Node-0/data/keystore/accountAddress"
GOQUORUM_READ_PASSWORD = "cat /home/michael/Documents/dev/network/QBFT-Network/Node-0/data/keystore/accountPassword"
GOQUORUM_READ_PRIVATE_KEY = "cat /home/michael/Documents/dev/network/QBFT-Network/Node-0/data/keystore/accountPrivateKey"

# TRUFFLE #
# deploy new consent contract
TRUFFLE_DEPLOY_CONTRACT = 'cd $SMART_CONTRACT_PATH && truffle migrate | grep "contract ' \
                          'address" | grep -o -E "0[xX][0-9a-fA-F]+"'

TRUFFLE_CONTRACT_BYTECODE = 'cat $SMART_CONTRACT_PATH/build/contracts/ConsentContract.json | grep -oP \'(?<="bytecode": ")[^"]*\''
TRUFFLE_READ_CONTRACT_ABI = 'cat $SMART_CONTRACT_PATH/build/contracts/ConsentContract.json'

TRUFFLE_CONTRACT_BYTECODE_FACTORY = 'cat $SMART_CONTRACT_PATH/build/contracts/ConsentContractFactory.json | grep -oP \'(?<="bytecode": ")[^"]*\''
TRUFFLE_READ_CONTRACT_ABI_FACTORY = 'cat $SMART_CONTRACT_PATH/build/contracts/ConsentContractFactory.json'

# UNLOCK ETHEREUM ACCOUNT
GOQUORUM_ACCOUNT_UNLOCK = 'curl -s -H "Content-Type: application/json" -X POST --data \'{"jsonrpc":"2.0",' \
                          '"method":"personal_unlockAccount","params":["%s", "%s"],"id":1}\' ' \
                          '%s'

# GET ACCOUNT BALANCE
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
def deploy_consent_contract():
    """deploys a new consent contract to the blockchain and returns the deployed address"""

    if not w3.isConnected():
        print("Ethereum Node not running...")
        return

    # unlock account first
    if not unlock_account(GOQUORUM_NODE):
        print("Cannot unlock ethereum account...")
        return

    # deploy contract with truffle and get the deployed contract address
    deploy_contract_read = os.popen(TRUFFLE_DEPLOY_CONTRACT)
    deployed_address = deploy_contract_read.read().strip()

    # save deployed contract address, needed for updating values later
    GOQUORUM_NODE.deployedContractAddress = deployed_address

    # read abi string from builded contract
    contract_abi_read = os.popen(TRUFFLE_READ_CONTRACT_ABI_FACTORY)
    contract_abi = json.loads(contract_abi_read.read())["abi"]

    # extract the builded bytecode
    contract_bytecode_read = os.popen(TRUFFLE_CONTRACT_BYTECODE_FACTORY)
    contract_bytecode = contract_bytecode_read.read().strip()
    contract_bytecode_hex = hex(int(contract_bytecode, 16))

    # create contract object and call constructor()
    consent_contract_factory = w3.eth.contract(address=deployed_address, abi=contract_abi,
                                               bytecode=contract_bytecode_hex)

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


@cli.command()
def create_consent_contract(
    address_contract_factory: str = typer.Option(..., "--adr-con-fac", "-acf", help="address of the contract factory"),
    address_consenter: str = typer.Option(..., "--adr-con", "-ac", help="address of the consenter")
):
    if not w3.isConnected():
        print("Ethereum Node not running...")
        return

    # read abi string from builded contract
    contract_abi_read = os.popen(TRUFFLE_READ_CONTRACT_ABI_FACTORY)
    contract_abi = json.loads(contract_abi_read.read())["abi"]

    # extract the builded bytecode
    contract_bytecode_read = os.popen(TRUFFLE_CONTRACT_BYTECODE_FACTORY)
    contract_bytecode = contract_bytecode_read.read().strip()
    contract_bytecode_hex = hex(int(contract_bytecode, 16))

    consent_contract_factory = w3.eth.contract(address=address_contract_factory, abi=contract_abi,
                                               bytecode=contract_bytecode_hex)

    create_consent_contract_tx = consent_contract_factory.functions.createConsentContract("12345").build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make createContract transaction
    make_contract_tx(create_consent_contract_tx)

    # read base contract abi
    contract_abi_read = os.popen(TRUFFLE_READ_CONTRACT_ABI)
    contract_abi = json.loads(contract_abi_read.read())["abi"]

    contract_bytecode_read = os.popen(TRUFFLE_CONTRACT_BYTECODE)
    contract_bytecode = contract_bytecode_read.read().strip()
    contract_bytecode_hex = hex(int(contract_bytecode, 16))

    deployed_contract_address = consent_contract_factory.functions.getContractAddress("12345").call()

    consent_contract = w3.eth.contract(address=deployed_contract_address, abi=contract_abi,
                                       bytecode=contract_bytecode_hex)

    # build checkSum from consenter's address
    checksum_address_consenter = w3.toChecksumAddress(address_consenter)

    # set consenter address
    set_contract_details_tx = consent_contract.functions.setContractDetails(checksum_address_consenter,
                                                                            1, 1, 1, 1, 1).build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make setContractDetails transaction
    make_contract_tx(set_contract_details_tx)

    # sign the contract
    sign_contract_tx = consent_contract.functions.signContract().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make signContract transaction
    make_contract_tx(sign_contract_tx)
    print(f'deployedAddress (factory): {deployed_contract_address}')


@cli.command()
def deposit_to_contract(
        address_contract: str = typer.Option(..., "--adr-con", "-ac", help="address of the contract"),
        value: int = typer.Option(..., "--val", "-v", help="value in ETH")
):
    if not w3.isConnected():
        print("Ethereum Node not running...")
        return

    # read base contract abi
    contract_abi_read = os.popen(TRUFFLE_READ_CONTRACT_ABI)
    contract_abi = json.loads(contract_abi_read.read())["abi"]

    contract_bytecode_read = os.popen(TRUFFLE_CONTRACT_BYTECODE)
    contract_bytecode = contract_bytecode_read.read().strip()
    contract_bytecode_hex = hex(int(contract_bytecode, 16))

    consent_contract = w3.eth.contract(address=address_contract, abi=contract_abi,
                                       bytecode=contract_bytecode_hex)

    # sign the contract
    sign_contract_tx = consent_contract.functions.deposit().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price,
            'value': value
        }
    )

    # sign and make signContract transaction
    make_contract_tx(sign_contract_tx)

    print(f'balance of contract: {get_account_balance(str(address_contract), True)}')


@cli.command()
def account_unlock():
    """temporaly unlocks the local ethereum account"""

    return unlock_account(GOQUORUM_NODE)


@cli.command()
def account_balance():
    """returns the current balance of the ethereum account in ETH"""

    return get_account_balance(GOQUORUM_NODE.accountAddress, False)


def get_account_balance(address, no_convert: bool):
    if not w3.isConnected():
        print("Ethereum Node not running...")
        return

    account_balance_read = os.popen(GOQUORUM_ACCOUNT_BALANCE % (address, GOQUORUM_NODE_URL))
    account_balance = json.loads(account_balance_read.read())

    if "error" in account_balance:
        print("ERROR: check configuration")
        return
    elif account_balance["result"]:
        balance = 0
        if no_convert:
            balance = account_balance["result"]
        else:
            balance = w3.fromWei(literal_eval(account_balance["result"]), 'ether')

        print(balance)
        return balance


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
    print(tx_receipt['transactionHash'])
    return tx_receipt.contractAddress


def build_tx():
    return {
        'from': GOQUORUM_NODE.accountAddress,
        'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
        'gasPrice': w3.eth.gas_price
    }


if __name__ == '__main__':
    # get local GOQUORUM node details
    GOQUORUM_NODE = get_account_details()

    cli()
