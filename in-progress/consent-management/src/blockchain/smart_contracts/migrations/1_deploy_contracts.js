var Storage = artifacts.require("./SimpleStorage.sol");

module.exports = function(deployer) {
 deployer.deploy(Storage);
};
