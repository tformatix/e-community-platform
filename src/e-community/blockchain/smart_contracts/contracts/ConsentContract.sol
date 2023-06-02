// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7.0 <0.9.0;

/** 
 * base contract for consent contracts
 * @title Consent Contract
*/
contract ConsentContract {

    // state of contract
    enum STATE {
        CONTRACT_CREATED,
        CONTRACT_PENDING,
        CONTRACT_ACTIVE,
        CONTRACT_ACTIVE_WITHDRAW_READY,
        CONTRACT_PAYMENT_PENDING,
        CONTRACT_REJECTED,
        CONTRACT_REVOKED,
        CONTRACT_CLOSED
    }

    /* usage of the energy data
    enum DATA_USAGE {
        FORECASTS,
        STATISTICAL,
        PERFORMANCE,
        CANDIDATE_E_COMMUNITY
    } */

    /* time resolution of the energy data
    enum TIME_RESOLUTION {
        TIME_15MIN,
        TIME_30MIN,
        TIME_1H,
        TIME_12H,
        TIME_1D
    }*/

    // both parties need to sign the contract
    struct ContractSign {
        bool signedByProposer;
        bool signedByConsenter;
    }

    // Contract Details
    string public contractID;
    address public proposer; // address of the owner of the contract (proposer)    
    address public consenter; // address of the consenter
    uint private startEnergyData; 
    uint private endEnergyData;
    uint private validityOfContract;
    uint private pricePerHour;
    uint private totalPrice;
    uint private dataUsage;
    uint private timeResolution;
    
    // signature of both parties
    ContractSign private signature;

    // State of the contract
    STATE private contractState;

    /**
     * create a new consent contract
     * @param _contractID each contract has a unique contractID
     */
    constructor(string memory _contractID) {
        contractID = _contractID;
        contractState = STATE.CONTRACT_CREATED;
    }

    /**
     * get the contract represented in "json" string
     * @return object as string
     */
    function getContractDetails() public view returns(address, address, 
                                                      string memory, uint, 
                                                      uint, uint, uint, uint, uint, uint, uint) {
        return (proposer, 
                consenter, 
                contractID, 
                startEnergyData, 
                endEnergyData, 
                validityOfContract, 
                pricePerHour, 
                totalPrice,
                uint(contractState),
                dataUsage,
                timeResolution);
    }

    /**
     * set details about the contract
     * @param _proposer creator of the contract (wants energy data from the consenter)
     * @param _consenter give consent about the energy data to the proposer
     * @param _startEnergyData start date of the energy date (epoche)
     * @param _endEnergyData end date of the energy date (epoche)
     * @param _validityOfContract validity date for the contract (epoche)
     * @param _pricePerHour price for 1h of energy data (in Wei)
     * @param _totalPrice total price for all energy data (in Wei)
     * @param _dataUsage usage of the energy data
     * @param _timeResolution time resolution of the energy data
     */
    function setContractDetails(
        address _proposer,
        address _consenter, 
        uint _startEnergyData, 
        uint _endEnergyData,
        uint _validityOfContract,
        uint _pricePerHour,
        uint _totalPrice,
        uint _dataUsage,
        uint _timeResolution
    ) public onlyOwner {
        proposer = _proposer;
        consenter = _consenter;
        startEnergyData = _startEnergyData;
        endEnergyData = _endEnergyData;
        validityOfContract = _validityOfContract;
        pricePerHour = _pricePerHour;
        totalPrice = _totalPrice;
        dataUsage = _dataUsage;
        timeResolution = _timeResolution;
    }

    /** 
     * both parties need to sign the contract inorder to start the energy sharing
     * proposer also needs to sign the contract because the consenter
     * can modify the regulations before the contract is active
     */
    function signContract(address _sender) public bothParties {
        if (_sender == proposer) { 
            signature.signedByProposer = true; 
            contractState = STATE.CONTRACT_PENDING;
        }
        if (_sender == consenter) {
            signature.signedByConsenter = true;
        }

        if (signature.signedByProposer && signature.signedByConsenter) {
            contractState = STATE.CONTRACT_PAYMENT_PENDING;
        }
    }

    // contract can be revoked from both parties
    function revokeConsent() public bothParties {
        contractState = STATE.CONTRACT_REVOKED;
    }

    // reject the offered consent contract
    function rejectConsent() public onlyConsenter {
        contractState = STATE.CONTRACT_REJECTED;
    }

    // proposer needs to deposit the agreed ethers to the contract
    /**
     * proposer need to deposit the agreed price to the contract wallet
     * {require} both parties need to sign the contract before
     * {require} the msg.value needs to be the agreed price
     */
    function deposit() public payable onlyOwner {
        require(contractState == STATE.CONTRACT_PAYMENT_PENDING, "Contract need to be signed from both parties");
        require(msg.value == totalPrice, "You need to deposit the agreed price!");
        
        contractState = STATE.CONTRACT_ACTIVE_WITHDRAW_READY;
    }

    /**
     * withdraw the ethers balance from the contract to address of the consenter
     * after withdraw the contract is active and running
     * {require} ethers were deposited to the contract
     */
    function withdraw() public onlyConsenter {
        require(contractState == STATE.CONTRACT_ACTIVE_WITHDRAW_READY, "Nothing withdraw, consenter needs to send ETH first");

        // transfer the ether to the consenter from the contract wallet
        payable(consenter).transfer(totalPrice);

        contractState = STATE.CONTRACT_ACTIVE;
    }

    // returns the contract state, will be always called when the user interact with energy data
    /**
     * returns the current state of the contract
     * used for displaying status in the UI
     * @return STATE
     */
    function getState() public returns(STATE) {

        // check if contract is still valid if not emit Closed() event
        if (contractState == STATE.CONTRACT_ACTIVE) {
            if (block.timestamp > validityOfContract) {
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
