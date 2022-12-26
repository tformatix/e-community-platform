# create new blocknode with the node_gen script (in-progress/consent-management/src/blockchain/scripts/node_generate/create_node.sh)

# on Raspberry make folder ~/blockchain
# copy generated Node folder to ~/blockchain


# make executeable and install.sh (installation and start of the service)
sudo chmod +x install.sh
sudo ./install.sh

# view status
sudo systemctl status e-community-blockchain

# stop service
sudo systemctl stop e-community-blockchain

# view logs
journalctl -u e-community-blockchain.service -f
