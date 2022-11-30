# make executeable and install.sh (installation and start of the service)
sudo chmod +x install.sh
sudo ./install.sh

# view status
sudo systemctl status e-community-smart-meter-reader

# stop service
sudo systemctl stop e-community-smart-meter-reader

# view logs
journalctl -u e-community-smart-meter-reader.service -f
