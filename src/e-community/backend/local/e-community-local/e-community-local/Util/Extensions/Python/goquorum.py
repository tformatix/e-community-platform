import typer
import os
import json

cli = typer.Typer()

# CLI COMMANDS #
# READ ETHEREUM ACCOUNT
GOQUORUM_READ_ADDRESS = "cat $GOQUORUM_NODE/data/keystore/accountAddress"
GOQUORUM_READ_PASSWORD = "cat $GOQUORUM_NODE/data/keystore/accountPassword"

# UNLOCK ETHEREUM ACCOUNT
GOQUORUM_ACCOUNT_UNLOCK = 'curl -H "Content-Type: application/json" -X POST --data \'{"jsonrpc":"2.0",' \
                          '"method":"personal_unlockAccount","params":["%s", "%s"],"id":67}\' ' \
                          'http://192.168.0.142:22001'


class GoQuorumNode:
    """ object for a goquorum (blockchain) node """
    def __init__(self, account_address, account_password):
        self.accountAddress = account_address
        self.accountPassword = account_password


@cli.command()
def temp():
    """temp command"""


@cli.command()
def unlock_account():
    """temporaly unlocks the local ethereum account"""
    node = get_account_details()

    account_unlock_read = os.popen(GOQUORUM_ACCOUNT_UNLOCK % (node.accountAddress, node.accountPassword))
    account_unlock = json.loads(account_unlock_read.read())

    if "error" in account_unlock:
        print("ERROR: check configuration")
    elif account_unlock["result"]:
        print("ACCOUNT UNLOCKED")


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
