# make executeable and install.sh (installation and start of the service)
sudo chmod +x install.sh
sudo ./install.sh

# view status
sudo systemctl status e-community-local

# stop service
sudo systemctl stop e-community-local

# view logs
journalctl -u e-community-local.service -f
