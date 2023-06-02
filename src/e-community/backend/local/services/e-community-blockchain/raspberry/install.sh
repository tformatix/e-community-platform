
echo "copy service to /etc/systemd/system"
sudo cp e-community-blockchain.service /etc/systemd/system/

echo "reload systemctl daemon..."
sudo systemctl daemon-reload

# enable service on boot
sudo systemctl enable e-community-blockchain.service

# restart service
sudo systemctl restart e-community-blockchain.service
