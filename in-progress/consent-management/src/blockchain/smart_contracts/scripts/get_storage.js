const Storage = artifacts.require("SimpleStorage")

module.exports = async function(callback) {

    let instance = await Storage.deployed();
    let result = await instance.newNumber(5000);

    callback();
}

