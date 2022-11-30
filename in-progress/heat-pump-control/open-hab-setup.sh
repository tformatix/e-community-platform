# INSTALLING ##########################################################################################################################################################

# add repository key to package manager
curl -fsSL "https://openhab.jfrog.io/artifactory/api/gpg/key/public" | gpg --dearmor > openhab.gpg
sudo mkdir /usr/share/keyrings
sudo mv openhab.gpg /usr/share/keyrings
sudo chmod u=rw,g=r,o=r /usr/share/keyrings/openhab.gpg

# add stable release to apt source list
echo 'deb [signed-by=/usr/share/keyrings/openhab.gpg] https://openhab.jfrog.io/artifactory/openhab-linuxpkg stable main' | sudo tee /etc/apt/sources.list.d/openhab.list

# resynchronize the package index
sudo apt-get update

# install openHAB
sudo apt-get install openhab

# install openHAB add-ons
sudo apt-get install openhab-addons

# start openHAB
sudo systemctl start openhab.service

# register to be automatically executed at system startup
sudo systemctl daemon-reload
sudo systemctl enable openhab.service

# ADDITIONAL ##########################################################################################################################################################

# privileges for common peripherals
sudo adduser openhab dialout
sudo adduser openhab tty
sudo adduser openhab audio

# Java network permissions
sudo setcap 'cap_net_raw,cap_net_admin=+eip cap_net_bind_service=+ep' $(realpath /usr/bin/java)

# LOGGING #############################################################################################################################################################

# show status of openHAB
sudo systemctl status openhab.service

# show logs
tail -f /var/log/openhab/openhab.log -f /var/log/openhab/events.log

# BACKUP ##############################################################################################################################################################

# backup
sudo $OPENHAB_RUNTIME/bin/backup ~/backupOpenHAB.zip

# restore
sudo $OPENHAB_RUNTIME/bin/restore ~/backupOpenHAB.zip

# OPENING #############################################################################################################################################################

# http://locahost:8080/habpanel