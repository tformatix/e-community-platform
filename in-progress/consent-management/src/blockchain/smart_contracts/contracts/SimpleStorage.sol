// SPDX-License-Identifier: GPL-3.0

pragma solidity >=0.7.0 <0.9.0;

contract SimpleStorage {
    int value = 0;

    event NumberChanged(int value);

    function newNumber(int _value) public {
        value = _value;
        emit NumberChanged(value);
    }
}