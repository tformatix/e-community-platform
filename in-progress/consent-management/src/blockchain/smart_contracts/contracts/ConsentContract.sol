// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7.0 <0.9.0;

// factory for new consent contracts
// used because factory only need to be deployed once
contract ConsentContractFactory {

    ConsentContract[] private contracts;

    // creates a new consent contract and returns its address
    function createConsentContract() public returns(address) {
        ConsentContract consentContract = new ConsentContract();

        contracts.push(consentContract);

        return address(consentContract);
    }
}

contract ConsentContract {

    // both parties need to sign the contract
    struct ContractSign {
        bool signedByProposer;
        bool signedByConsenter;
    }

    // notify consenter that a contract got created
    event ProposedContract();

    // notify proposer that the contract got signed by the consenter
    event ApprovedContract();

    // ### Contract Details ###
    
    address private proposer; // address of the owner of the contract (proposer)    
    address private consenter; // address of the consenter
    uint private startEnergyData; 
    uint private endEnergyData;
    uint private contractValidInDays;
    uint private pricePerHour;
    uint private totalPrice;

    // signature of both parties
    ContractSign private signature;

    // set owner of the contract
    constructor() {
        proposer = msg.sender;
    }

    // set details about the contract
    function setContractDetails(
        address _consenter, 
        uint _startEnergyData, 
        uint _endEnergyData,
        uint _contractValidInDays,
        uint _pricePerHour,
        uint _totalPrice
    ) public onlyOwner {
        consenter = _consenter;
        startEnergyData = _startEnergyData;
        endEnergyData = _endEnergyData;
        contractValidInDays = _contractValidInDays;
        pricePerHour = _pricePerHour;
        totalPrice = _totalPrice;
    }

    /* both parties need to sign the contract inorder to start the energy sharing
       proposer also needs to sign the contract because the consenter
       can modify the regulations before the contract is active
    */
    function signContract() public bothParties {
        if (msg.sender == proposer) { signature.signedByProposer = true; }
        if (msg.sender == consenter) { signature.signedByConsenter = true; }

        if (signature.signedByProposer && signature.signedByConsenter) {
            // TODO: calucalte contract validity time (contractValidInDays) upon both parties signed date
            uint dat = block.timestamp + 3 minutes;
            emit ApprovedContract();
        }
    }

    // contract can be revoked from both parties
    function revokeConsent() public bothParties {
    }


    // modifier for functions only the owner can execute
    modifier onlyOwner() {
        require(msg.sender == proposer);
        _;
    }

    // modifier for functions, both parties (proposer and consenter) can execute
    modifier bothParties() {
        require(msg.sender == proposer || msg.sender == consenter);
        _;
    }
}
