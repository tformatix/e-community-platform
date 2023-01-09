// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7.0 <0.9.0;

// factory for new consent contracts
// used because factory only need to be deployed once
contract ConsentContractFactory {

    ConsentContract[] private contracts;

    // creates a new consent contract and returns its address
    function createConsentContract(string memory _contractID) public {
        ConsentContract consentContract = new ConsentContract(_contractID);

        contracts.push(consentContract);
    }

    // returns address for given contractID
    function getContractAddress(string memory _contractID) public view returns(address) {
        for (uint i = 0; i < contracts.length; i++) {
            string memory contractID = contracts[i].contractID();
            if (keccak256(abi.encodePacked(contractID)) == keccak256(abi.encodePacked(_contractID))) {
                return address(contracts[i]);
            }
        }

        return address(contracts[0]);
    }
}

contract ConsentContract {

    enum STATE {
        CONTRACT_PENDING,
        CONTRACT_ACTIVE,
        CONTRACT_PAYMENT_DEPOSIT,
        CONTRACT_PAYMENT_PENDING,
        CONTRACT_REJECTED,
        CONTRACT_REVOKED,
        CONTRACT_CLOSED
    }

    // both parties need to sign the contract
    struct ContractSign {
        bool signedByProposer;
        bool signedByConsenter;
    }


    // notify consenter that he received a payment => contract is now valid
    event PaymentReceived(address proposer, address consenter, string contractID);

    // notify proposer that the contract got signed by the consenter
    event ApprovedContract(address proposer, address consenter, string contractID);

    // notify both when contract gets closed
    event ClosedContract(address proposer, address consenter, string contractID);

    // notify proposer that the contract got revoked
    event RevokedContract(address proposer, address consenter, string contractID);

    // ### Contract Details ###
    string public contractID;
    address private proposer; // address of the owner of the contract (proposer)    
    address private consenter; // address of the consenter
    uint private startEnergyData; 
    uint private endEnergyData;
    uint private validityOfContract;
    uint private pricePerHour;
    uint private totalPrice;
    
    // signature of both parties
    ContractSign private signature;

    // State of the contract
    STATE private contractState;

    // set owner of the contract
    constructor(string memory _contractID) {
        proposer = msg.sender;
        contractID = _contractID;
    }

    function getContractDetails() public view returns(address, address, string memory, uint, uint, uint, uint, uint) {
        return (proposer, consenter, contractID, startEnergyData, endEnergyData, validityOfContract, pricePerHour, totalPrice);
    }

    // set details about the contract
    function setContractDetails(
        address _consenter, 
        uint _startEnergyData, 
        uint _endEnergyData,
        uint _validityOfContract,
        uint _pricePerHour,
        uint _totalPrice
    ) public onlyOwner {
        consenter = _consenter;
        startEnergyData = _startEnergyData;
        endEnergyData = _endEnergyData;
        validityOfContract = _validityOfContract;
        pricePerHour = _pricePerHour;
        totalPrice = _totalPrice;
    }

    /* both parties need to sign the contract inorder to start the energy sharing
       proposer also needs to sign the contract because the consenter
       can modify the regulations before the contract is active
    */
    function signContract() public bothParties {
        if (msg.sender == proposer) { 
            signature.signedByProposer = true; 
            contractState = STATE.CONTRACT_PENDING;
        }
        if (msg.sender == consenter) {
            signature.signedByConsenter = true;
        }

        if (signature.signedByProposer && signature.signedByConsenter) {
            emit ApprovedContract(proposer, consenter, contractID);
            contractState = STATE.CONTRACT_PAYMENT_PENDING;
        }
    }

    // contract can be revoked from both parties
    function revokeConsent() public bothParties {
        contractState = STATE.CONTRACT_REVOKED;
        emit RevokedContract(proposer, consenter, contractID);
    }

    // proposer needs to deposit the agreed ethers to the contract
    function deposit() public payable onlyOwner {
        require(msg.value == totalPrice, "You need to deposit the agreed price!");
        contractState = STATE.CONTRACT_PAYMENT_DEPOSIT;
    }

    // withdraw the ethers balance from the contract to address of the consenter
    // after withdraw the contract is active and running
    function withdraw() public onlyConsenter {
        require(contractState == STATE.CONTRACT_PAYMENT_DEPOSIT, "Nothing withdraw, consenter needs to send ETH first");

        payable(consenter).transfer(totalPrice);

        emit PaymentReceived(consenter, proposer, contractID);

        contractState = STATE.CONTRACT_ACTIVE;
    }

    // returns the contract state, will be always called when the user interact with energy data
    function getState() public returns(STATE) {

        // check if contract is still valid if not emit Closed() event
        if (contractState == STATE.CONTRACT_ACTIVE) {
            if (block.timestamp > validityOfContract) {
                emit ClosedContract(proposer, consenter, contractID);
                contractState = STATE.CONTRACT_CLOSED;
            }
        }
        
        return contractState;
    }

    // modifier for functions only the owner can execute
    modifier onlyOwner() {
        //require(msg.sender == proposer);
        _;
    }

    // modifier for functions only the consenter can execute
    modifier onlyConsenter() {
        //require(msg.sender == consenter);
        _;
    }

    // modifier for functions, both parties (proposer and consenter) can execute
    modifier bothParties() {
        //require(msg.sender == proposer || msg.sender == consenter);
        _;
    }
}
