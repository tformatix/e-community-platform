// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7.0 <0.9.0;

// import base Consent Contract
import "./ConsentContract.sol";

/** 
 * used because factory only need to be deployed once
 * @title Factory for Consent Contracts 
*/
contract ConsentContractFactory {

    // stores all deployed consent contracts
    ConsentContract[] private contracts;

    // defines the wanted type of the contract
    enum AddressType {
        PROPOSER,
        CONSENTER
    }

    /**
     * creates a new consent contract with a unique contractID
     * @param _contractID unique ID of a Contract
     */
    function createConsentContract(string memory _contractID) public {
        ConsentContract consentContract = new ConsentContract(_contractID);

        contracts.push(consentContract);
    }

    /**
     * return the contract with the given contractID 
     * if not found return the contract address
     * @param _contractID unique ID of a contract
     * @return address of the found contract
     */
    function getContractAddress(string memory _contractID) public view returns(address) {
        for (uint i = 0; i < contracts.length; i++) {
            string memory contractID = contracts[i].contractID();

            // compare hash value of both contractIDs
            if (keccak256(abi.encodePacked(contractID)) == keccak256(abi.encodePacked(_contractID))) {
                return address(contracts[i]);
            }
        }

        return address(this);
    }

    /**
     * get related contracts for this address either Proposer or Consenter
     * @param _address address of a member
     * @return array of related contracts
     */
    function getContracts(address _address) public view returns(ConsentContract[] memory) {
        ConsentContract[] memory contractsForAddress = new ConsentContract[](contracts.length);

        for (uint i = 0; i < contracts.length; i++) {

            // add to array if addresses match
            if (contracts[i].proposer() == _address || contracts[i].consenter() == _address) {
                contractsForAddress[i] = contracts[i];
            }
        }

        return contractsForAddress;
    }

    function testArray() public view returns(ConsentContract[] memory) {
        return contracts;
    }
}

