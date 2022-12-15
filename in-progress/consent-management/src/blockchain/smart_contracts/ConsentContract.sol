// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7.0 <0.9.0;

contract ConsentContract {

    // contract must have a state
    enum STATE {
        INACTIVE,
        ACTIVE, 
        REVOKED, 
        CLOSED 
    }

    // both parties need to sign the contract
    struct ContractSign {
        bool signedByProposer;
        bool signedByConsenter;
    }

    // notify consenter that a contract got created
    event ProposedContract();

    // notify proposer that the contract got signed by the consenter
    event ApprovedContract();

    // address of the owner of the contract (proposer)
    address private proposer;

    // address of the consenter
    address private consenter;

    // state of the contract
    STATE state;

    // signature of both parties
    ContractSign private signature;

    // set owner of the contract
    constructor(address _consenter) {
        proposer = msg.sender;
        state = STATE.INACTIVE;
        consenter = _consenter;
    }

    /* both parties need to sign the contract inorder to start the energy sharing
       proposer also needs to sign the contract because the consenter
       can modify the regulations before the contract is active
    */
    function signContract() public bothParties {
        if (msg.sender == proposer) { signature.signedByProposer = true; }
        if (msg.sender == consenter) { signature.signedByConsenter = true; }

        if (signature.signedByProposer && signature.signedByConsenter) {
            emit ApprovedContract();
        }
    }

    // contract can be revoked from both parties
    function revokeConsent() public bothParties {
        state = STATE.REVOKED;
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