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
GOQUORUM_CONTRACT_FACTORY_ADDRESS = "0xC6bf28eAF3E13B4376bd682EE5962bAAEFC505D2"

# empty address
GOQUORUM_EMPTY_ADDRESS = "0x0000000000000000000000000000000000000000"

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

# TRUFFLE #
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
def create_consent_contract(
    address_consenter: str = typer.Option(..., "--adr-con", "-ac", help="address of the consenter"),
    contract_id: str = typer.Option(..., "--contract-id", "-cid", help="id of the contract"),
    start_energy_data: int = typer.Option(..., "--start-e-data", "-sed",
                                          help="start datetime of energy data (unix timestamp)"),
    end_energy_data: int = typer.Option(..., "--end-e-data", "-eed",
                                        help="end datetime of energy data (unix timestamp)"),
    validity_contract: int = typer.Option(..., "--val-con", "-vc", help="validity of contract (unix timestamp)"),
    price_per_hour: str = typer.Option(..., "--price-hour", "-ph", help="price per 1h of energy data (in ETH)"),
    total_price: str = typer.Option(..., "--price-total", "-pt", help="total price of the energy data (in ETH)"),
    data_usage: int = typer.Option(..., "--data-use", "-du", help="usage of the energy data"),
    time_resolution: int = typer.Option(..., "--time-res", "-tr", help="time resolution of the energy data")
):
    """deploys a consent contract to the blockchain"""

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
    set_contract_details_tx = consent_contract.functions.setContractDetails(GOQUORUM_NODE.accountAddress,
                                                                            w3.toChecksumAddress(address_consenter),
                                                                            start_energy_data,
                                                                            end_energy_data,
                                                                            validity_contract,
                                                                            w3.toWei(price_per_hour, "ether"),
                                                                            w3.toWei(total_price,"ether"),
                                                                            data_usage,
                                                                            time_resolution).build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make setContractDetails transaction
    make_contract_tx(set_contract_details_tx)

    # sign the contract
    sign_contract_tx = consent_contract.functions.signContract(GOQUORUM_NODE.accountAddress).build_transaction(
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
def accept_contract(
    contract_id: str = typer.Option(..., "--contract-id", "-cid", help="id of the contract")
):
    """consenter accepts and signs contract"""
    consent_contract_factory = w3.eth.contract(address=GOQUORUM_CONTRACT_FACTORY_ADDRESS, abi=GOQUORUM_NODE.abiFactory,
                                               bytecode=GOQUORUM_NODE.bytecodeFactory)

    # get deployed contract address with the contract_id
    deployed_contract_address = consent_contract_factory.functions.getContractAddress(contract_id).call()

    consent_contract = w3.eth.contract(address=deployed_contract_address, abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeFactory)

    signAddress = w3.toChecksumAddress("0x89A41a3ae94F64b8DC3683787Ef2363e9d0BC957")

    # sign the contract
    sign_contract_tx = consent_contract.functions.signContract(signAddress).build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make signContract transaction
    make_contract_tx(sign_contract_tx)

@cli.command()
def reject_contract(
    contract_id: str = typer.Option(..., "--contract-id", "-cid", help="id of the contract")
):
    """consenter accepts and signs contract"""
    consent_contract_factory = w3.eth.contract(address=GOQUORUM_CONTRACT_FACTORY_ADDRESS, abi=GOQUORUM_NODE.abiFactory,
                                               bytecode=GOQUORUM_NODE.bytecodeFactory)

    # get deployed contract address with the contract_id
    deployed_contract_address = consent_contract_factory.functions.getContractAddress(contract_id).call()

    consent_contract = w3.eth.contract(address=deployed_contract_address, abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeFactory)

    # sign the contract
    sign_contract_tx = consent_contract.functions.rejectConsent().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make signContract transaction
    make_contract_tx(sign_contract_tx)

@cli.command()
def revoke_contract(
    contract_id: str = typer.Option(..., "--contract-id", "-cid", help="id of the contract")
):
    """consenter accepts and signs contract"""
    consent_contract_factory = w3.eth.contract(address=GOQUORUM_CONTRACT_FACTORY_ADDRESS, abi=GOQUORUM_NODE.abiFactory,
                                               bytecode=GOQUORUM_NODE.bytecodeFactory)

    # get deployed contract address with the contract_id
    deployed_contract_address = consent_contract_factory.functions.getContractAddress(contract_id).call()

    consent_contract = w3.eth.contract(address=deployed_contract_address, abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeFactory)

    # sign the contract
    sign_contract_tx = consent_contract.functions.revokeConsent().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make signContract transaction
    make_contract_tx(sign_contract_tx)


@cli.command()
def deposit_to_contract(
    contract_id: str = typer.Option(..., "--contract-id", "-cid", help="id of the contract"),
    value: str = typer.Option(..., "--val", "-v", help="value in ETH")
):
    """deposits the value to the contract wallet"""
    consent_contract_factory = w3.eth.contract(address=GOQUORUM_CONTRACT_FACTORY_ADDRESS, 
                                               abi=GOQUORUM_NODE.abiFactory,
                                               bytecode=GOQUORUM_NODE.bytecodeFactory)

    # get deployed contract address with the contract_id
    deployed_contract_address = consent_contract_factory.functions.getContractAddress(contract_id).call()

    consent_contract = w3.eth.contract(address=deployed_contract_address, 
                                       abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeFactory)

    # deposit to the contract wallet
    deposit_tx = consent_contract.functions.deposit().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price,
            'value': w3.toWei(value, "ether")
        }
    )

    # sign and make signContract transaction
    make_contract_tx(deposit_tx)

    get_account_balance(str(deployed_contract_address), False)


@cli.command()
def withdraw_from_contract(
    contract_id: str = typer.Option(..., "--contract-id", "-cid", help="id of the contract")
):
    """withdraws the defined totalPrice from the contract Wallet"""
    consent_contract_factory = w3.eth.contract(address=GOQUORUM_CONTRACT_FACTORY_ADDRESS, abi=GOQUORUM_NODE.abiFactory,
                                               bytecode=GOQUORUM_NODE.bytecodeFactory)

    # get deployed contract address with the contract_id
    deployed_contract_address = consent_contract_factory.functions.getContractAddress(contract_id).call()

    consent_contract = w3.eth.contract(address=deployed_contract_address, abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeFactory)

    # withdraw from the contract wallet
    withdraw_tx = consent_contract.functions.withdraw().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price
        }
    )

    # sign and make transaction
    make_contract_tx(withdraw_tx)

    # get balance of consenter's account
    contract_details = consent_contract.functions.getContractDetails().call()
    get_account_balance(str(contract_details[1]), False)


@cli.command()
def get_contract_details(
    address_contract: str = typer.Option(..., "--adr-con", "-ac", help="address of the contract")
):
    """get details about a consent contract from the blockchain"""
    print(get_contract_details_(address_contract))


@cli.command()
def get_contracts_for_member():
    """gets all contracts were the member is either proposer or consenter"""
    consent_contract_factory = w3.eth.contract(address=GOQUORUM_CONTRACT_FACTORY_ADDRESS, abi=GOQUORUM_NODE.abiFactory,
                                               bytecode=GOQUORUM_NODE.bytecodeFactory)

    consent_contract_factory = w3.eth.contract(address=GOQUORUM_CONTRACT_FACTORY_ADDRESS, abi=GOQUORUM_NODE.abiFactory,
                                               bytecode=GOQUORUM_NODE.bytecodeFactory)

    contracts = consent_contract_factory.functions.getContracts(GOQUORUM_NODE.accountAddress).call()

    contracts = list(filter(lambda x: (x != GOQUORUM_EMPTY_ADDRESS), contracts))
    contractDetails = []

    for contract in contracts:
        contractDetails.append(get_contract_details_(contract))
    
    print(json.dumps(contractDetails))


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


def get_contract_details_(address_contract):
    """get details about a consent contract from the blockchain"""
    consent_contract = w3.eth.contract(address=address_contract, abi=GOQUORUM_NODE.abiContract,
                                       bytecode=GOQUORUM_NODE.bytecodeContract)

    # get/update state of the contract
    state_tx = consent_contract.functions.getState().build_transaction(
        {
            'from': GOQUORUM_NODE.accountAddress,
            'nonce': w3.eth.get_transaction_count(GOQUORUM_NODE.accountAddress),
            'gasPrice': w3.eth.gas_price,
        }
    )

    # sign and make state transaction
    make_contract_tx(state_tx)

    # get contract details now
    contract_details = consent_contract.functions.getContractDetails().call()

    # create contract object from smart contract
    contract = ConsentContract(contract_details[0],
                               contract_details[1],
                               contract_details[2],
                               str(contract_details[3]),
                               str(contract_details[4]),
                               str(contract_details[5]),
                               str(float(w3.fromWei(contract_details[6], "ether"))),
                               str(float(w3.fromWei(contract_details[7], "ether"))),
                               contract_details[8],
                               contract_details[9],
                               contract_details[10])

    return contract.to_dict()


if __name__ == '__main__':
    # get local GOQUORUM node details
    GOQUORUM_NODE = get_account_details()

    if not w3.isConnected():
        print("Ethereum Node not running...")
    else:
        cli()
