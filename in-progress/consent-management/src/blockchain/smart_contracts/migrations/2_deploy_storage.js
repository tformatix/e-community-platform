var SimpleStorage = artifacts.require("./ConsentContract.sol");

module.exports = function(deployer) {
    deployer.deploy(SimpleStorage);
};