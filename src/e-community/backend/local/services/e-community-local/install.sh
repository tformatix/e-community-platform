
echo "copy service to /etc/systemd/system"
sudo cp e-community-local.service /etc/systemd/system/

echo "reload systemctl daemon..."
sudo systemctl daemon-reload

# enable service on boot
sudo systemctl enable e-community-local.service

# restart service
sudo systemctl restart e-community-local.service
