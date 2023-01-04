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
        CONTRACT_SIGNED_BOTH,
        CONTRACT_PAYMENT_RECEIVED,
        CONTRACT_VALID,
        CONTRACT_REJECTED,
        CONTRACT_INVALID
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
            contractState = STATE.CONTRACT_SIGNED_BOTH;
        }
    }

    // contract can be revoked from both parties
    function revokeConsent() public bothParties {
    }

    // proposer needs to deposit the agreed ethers to the contract
    function deposit() public payable onlyOwner {
        require(msg.value == totalPrice, "You need to deposit the agreed price!");
    }

    // get the balance of the contract
    function balance() public view bothParties returns(uint) {
        return address(this).balance;
    }

    // withdraw the ethers balance from the proposer
    function withdraw() public onlyConsenter {
        payable(consenter).transfer(address(this).balance);

        emit PaymentReceived(consenter, proposer, contractID);

        contractState = STATE.CONTRACT_VALID;
    }

    // returns the contract state
    function getState() view public returns(STATE) {
        if (contractState == STATE.CONTRACT_VALID) {
            // check if contract is still valid
        }
        
        return STATE.CONTRACT_INVALID;
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
