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

# this is the address of the factory contract (is static)
GOQUORUM_CONTRACT_FACTORY_ADDRESS = "0x5D2335F00E98757bA576921d6DD180BA3103CAF1"

# CLI object
cli = typer.Typer()

# web3 object, inject middleware because of private fork of ethereum
w3 = web3.Web3(web3.HTTPProvider(GOQUORUM_NODE_URL))
w3.middleware_onion.inject(geth_poa_middleware, layer=0)
w3.handleRevert = True

# CLI COMMANDS #
# READ ETHEREUM ACCOUNT
GOQUORUM_READ_ADDRESS = "cat /home/pi/blockchain/Node/data/keystore/accountAddress"
GOQUORUM_READ_PASSWORD = "cat /home/pi/blockchain/Node/data/keystore/accountPassword"
GOQUORUM_READ_PRIVATE_KEY = "cat /home/pi/blockchain/Node/data/keystore/accountPrivateKey"

#GOQUORUM_READ_ADDRESS = "cat /home/michael/Documents/dev/network/QBFT-Network/Node-0/data/keystore/accountAddress"
#GOQUORUM_READ_PASSWORD = "cat /home/michael/Documents/dev/network/QBFT-Network/Node-0/data/keystore/accountPassword"
#GOQUORUM_READ_PRIVATE_KEY = "cat /home/michael/Documents/dev/network/QBFT-Network/Node-0/data/keystore/accountPrivateKey"

# TRUFFLE #
# deploy new consent contract
TRUFFLE_DEPLOY_CONTRACT = 'cd bla && truffle migrate | grep "contract ' \
                          'address" | grep -o -E "0[xX][0-9a-fA-F]+"'

""" deployment configuration
TRUFFLE_DEPLOY_CONTRACT = 'cd $SMART_CONTRACT_PATH && truffle migrate | grep "contract ' \
                          'address" | grep -o -E "0[xX][0-9a-fA-F]+"'
TRUFFLE_CONTRACT_BYTECODE = 'cat $SMART_CONTRACT_PATH/build/contracts/ConsentContract.json | grep -oP \'(?<="bytecode": ")[^"]*\''
TRUFFLE_READ_CONTRACT_ABI = 'cat $SMART_CONTRACT_PATH/build/contracts/ConsentContract.json'
TRUFFLE_CONTRACT_BYTECODE_FACTORY = 'cat $SMART_CONTRACT_PATH/build/contracts/ConsentContractFactory.json | grep -oP \'(?<="bytecode": ")[^"]*\''
TRUFFLE_READ_CONTRACT_ABI_FACTORY = 'cat $SMART_CONTRACT_PATH/build/contracts/ConsentContractFactory.json'
"""

TRUFFLE_CONTRACT_BYTECODE = 'cat /home/pi/backend/Util/Extensions/Python/build/ConsentContract.json | grep -oP \'(?<="bytecode": ")[^"]*\''
TRUFFLE_READ_CONTRACT_ABI = 'cat /home/pi/backend/Util/Extensions/Python/build/ConsentContract.json'

TRUFFLE_CONTRACT_BYTECODE_FACTORY = 'cat /home/pi/backend/Util/Extensions/Python/build/ConsentContractFactory.json | grep -oP \'(?<="bytecode": ")[^"]*\''
TRUFFLE_READ_CONTRACT_ABI_FACTORY = 'cat /home/pi/backend/Util/Extensions/Python/build/ConsentContractFactory.json'

# UNLOCK ETHEREUM ACCOUNT
GOQUORUM_ACCOUNT_UNLOCK = 'curl -s -H "Content-Type: application/json" -X POST --data \'{"jsonrpc":"2.0",' \
                          '"method":"personal_unlockAccount","params":["%s", "%s"],"id":1}\' ' \
                          '%s'

# GET ACCOUNT BALANCE
GOQUORUM_ACCOUNT_BALANCE = 'curl -s -H "Content-Type: application/json" -X POST --data \'{"jsonrpc":"2.0",' \
                           '"method":"eth_getBalance","params":["%s", "latest"],"id":1}\' ' \
                           '%s'


@cli.command()
def deploy_consent_contract():
    """deploys a new consent contract to the blockchain and returns the deployed address"""

    # unlock account first
    if not unlock_account(GOQUORUM_NODE):
        print("Cannot unlock ethereum account...")
        return

    # deploy contract with truffle and get the deployed contract address
    deploy_contract_read = os.popen(TRUFFLE_DEPLOY_CONTRACT)
    deployed_address = deploy_contract_read.read().strip()

    # save deployed contract address, needed for updating values later
    GOQUORUM_NODE.deployedContractAddress = deployed_address

    # create contract object and call constructor()
    consent_contract_factory = w3.eth.contract(address=deployed_address, abi=GOQUORUM_NODE.abiFactory,
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


@cli.command()
def create_consent_contract(
    address_consenter: str = typer.Option(..., "--adr-con", "-ac", help="address of the consenter"),
    contract_id: str = typer.Option(..., "--contract-id", "-cid", help="id of the contract"),
    start_energy_data: int = typer.Option(..., "--start-e-data", "-sed",
                                          help="start datetime of energy data (unix timestamp)"),
    end_energy_data: int = typer.Option(..., "--end-e-data", "-eed",
                                        help="end datetime of energy data (unix timestamp)"),
    validity_contract: int = typer.Option(..., "--val-con", "-vc", help="validity of contract (unix timestamp)"),
    price_per_hour: str = typer.Option(..., "--price-hour", "-ph", help="price per 1h of energy data (in ETH)"),
    total_price: str = typer.Option(..., "--price-total", "-pt", help="total price of the energy data (in ETH)")
):

    consent_contract_factory = w3.eth.contract(address=GOQUORUM_CONTRACT_FACTORY_ADDRESS, abi=GOQUORUM_NODE.abiFactory,
                                               bytecode=GOQUORUM_NODE.bytecodeFactory)

    create_consent_contract_tx = consent_contract_factory.functions.createConsentContract(
        contract_id).build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make createContract transaction
    make_contract_tx(create_consent_contract_tx)

    # get deployed contract address with the contract_id
    deployed_contract_address = consent_contract_factory.functions.getContractAddress(contract_id).call()

    consent_contract = w3.eth.contract(address=deployed_contract_address, abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeFactory)

    # set contract details
    set_contract_details_tx = consent_contract.functions.setContractDetails(w3.toChecksumAddress(address_consenter),
                                                                            start_energy_data,
                                                                            end_energy_data,
                                                                            validity_contract,
                                                                            w3.toWei(price_per_hour, "ether"),
                                                                            w3.toWei(total_price,
                                                                                     "ether")).build_transaction(
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
    print(deployed_contract_address)


@cli.command()
def deposit_to_contract(
    address_contract: str = typer.Option(..., "--adr-con", "-ac", help="address of the contract"),
    value: str = typer.Option(..., "--val", "-v", help="value in ETH")
):

    consent_contract = w3.eth.contract(address=address_contract, abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeFactory)

    # sign the contract
    sign_contract_tx = consent_contract.functions.deposit().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price,
            'value': w3.toWei(value, "ether")
        }
    )

    # sign and make signContract transaction
    make_contract_tx(sign_contract_tx)

    print(f'balance of contract: {get_account_balance(str(address_contract), True)}')


@cli.command()
def withdraw_from_contract(
    address_contract: str = typer.Option(..., "--adr-con", "-ac", help="address of the contract")
):

    consent_contract = w3.eth.contract(address=address_contract, abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeFactory)

    # sign the contract
    withdraw_tx = consent_contract.functions.withdraw().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price,
        }
    )

    # sign and make signContract transaction
    make_contract_tx(withdraw_tx)


@cli.command()
def get_contract_details(
    address_contract: str = typer.Option(..., "--adr-con", "-ac", help="address of the contract")
):

    consent_contract = w3.eth.contract(address=address_contract, abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeFactory)

    contract_details = consent_contract.functions.getContractDetails().call()

    # create contract object from smart contract
    contract = ConsentContract(contract_details[0],
                               contract_details[1],
                               contract_details[2],
                               contract_details[3],
                               contract_details[4],
                               contract_details[5],
                               w3.fromWei(contract_details[6], "ether"),
                               w3.fromWei(contract_details[7], "ether"))

    print(contract.to_dict())


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

    # abi - base contract
    abi_contract_read = os.popen(TRUFFLE_READ_CONTRACT_ABI)
    abi_contract = json.loads(abi_contract_read.read())["abi"]

    # bytecode - base contract
    bytecode_contract_read = os.popen(TRUFFLE_CONTRACT_BYTECODE)
    bytecode_contract = bytecode_contract_read.read().strip()
    bytecode_contract = hex(int(bytecode_contract, 16))

    node.set_build_config(abi_factory, abi_contract, bytecode_factory, bytecode_contract)

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


def build_tx():
    return {
        'from': GOQUORUM_NODE.accountAddress,
        'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
        'gasPrice': w3.eth.gas_price
    }


if __name__ == '__main__':
    # get local GOQUORUM node details
    GOQUORUM_NODE = get_account_details()

    if not w3.isConnected():
        print("Ethereum Node not running...")
    else:
        cli()
