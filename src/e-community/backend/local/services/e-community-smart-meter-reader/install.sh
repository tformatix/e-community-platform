#!/bin/bash

echo "copy service to /etc/systemd/system"
sudo cp e-community-smart-meter-reader.service /etc/systemd/system/

echo "reload systemctl daemon..."
sudo systemctl daemon-reload

# enable service on boot
sudo systemctl enable e-community-smart-meter-reader.service

# restart service
sudo systemctl restart e-community-smart-meter-reader.service
