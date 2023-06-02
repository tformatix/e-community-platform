class GoQuorumNode:
    """ object for a goquorum (blockchain) node """
    deployedContractAddress = ""
    abiFactory = ""
    bytecodeFactory = ""
    abiContract = ""
    bytecodeContract = ""

    def __init__(self, account_address, account_password, account_private_key):
        self.accountAddress = account_address
        self.accountPassword = account_password
        self.accountPrivateKey = account_private_key

    def set_build_config(self, abi_factory, abi_contract, bytecode_factory, bytecode_contract):
        self.abiFactory = abi_factory
        self.abiContract = abi_contract
        self.bytecodeFactory = bytecode_factory
        self.bytecodeContract = bytecode_contract

    def set_deployed_contract_address(self, deployed_address):
        self.deployedContractAddress = deployed_address
