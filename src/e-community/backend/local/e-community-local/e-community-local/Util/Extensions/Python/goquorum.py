import typer
import os
import json
from ast import literal_eval

cli = typer.Typer()

# CLI COMMANDS #
# READ ETHEREUM ACCOUNT
GOQUORUM_READ_ADDRESS = "cat $GOQUORUM_NODE/data/keystore/accountAddress"
GOQUORUM_READ_PASSWORD = "cat $GOQUORUM_NODE/data/keystore/accountPassword"

# UNLOCK ETHEREUM ACCOUNT
GOQUORUM_ACCOUNT_UNLOCK = 'curl -s -H "Content-Type: application/json" -X POST --data \'{"jsonrpc":"2.0",' \
                          '"method":"personal_unlockAccount","params":["%s", "%s"],"id":1}\' ' \
                          'http://127.0.0.1:22000'

GOQUORUM_ACCOUNT_BALANCE = 'curl -s -H "Content-Type: application/json" -X POST --data \'{"jsonrpc":"2.0",' \
                          '"method":"eth_getBalance","params":["%s", "latest"],"id":1}\' ' \
                          'http://127.0.0.1:22000'


class GoQuorumNode:
    """ object for a goquorum (blockchain) node """
    def __init__(self, account_address, account_password):
        self.accountAddress = account_address
        self.accountPassword = account_password


@cli.command()
def deploy_new_contract():
    """deploys a new consent contract to the blockchain and returns the deployed address"""


@cli.command()
def account_unlock():
    """temporaly unlocks the local ethereum account"""
    node = get_account_details()

    account_unlock_read = os.popen(GOQUORUM_ACCOUNT_UNLOCK % (node.accountAddress, node.accountPassword))
    account_unlock = json.loads(account_unlock_read.read())

    if "error" in account_unlock:
        print("ERROR: check configuration")
    elif account_unlock["result"]:
        print("ACCOUNT UNLOCKED")


@cli.command()
def account_balance():
    """returns the current balance of the ethereum account in ETH"""
    node = get_account_details()

    account_balance_read = os.popen(GOQUORUM_ACCOUNT_BALANCE % node.accountAddress)
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
    $GOQUORUM_NODE needs to be set
    """
    account_address_read = os.popen(GOQUORUM_READ_ADDRESS)
    account_address = account_address_read.read()

    account_password_read = os.popen(GOQUORUM_READ_PASSWORD)
    account_password = account_password_read.read()

    return GoQuorumNode(account_address, account_password)


if __name__ == '__main__':
    cli()
