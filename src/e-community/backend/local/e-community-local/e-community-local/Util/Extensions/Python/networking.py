import typer
import os
from wifi import Cell, Scheme
import json
import time

cli = typer.Typer()

# add a new entry in the wpa_supplicant config via echo & tee commands
ADD_NETWORK = 'cd /etc/wpa_supplicant/ && echo "network={\n ssid=\\"%s\\"\n psk=\\"%s\\"\n}" | sudo tee -a wpa_supplicant.conf'

# kill all networking processes for wifi
KILL_WPA_SUPPLICANT = 'sudo killall wpa_supplicant'

# restart wpa_supplicant driver
RESTART_WPA_SUPPLICANT = 'sudo wpa_supplicant -B -c /etc/wpa_supplicant/wpa_supplicant.conf -i wlan0'

# status of the wired connection adapter (eth0)
STATUS_ETH0 = 'cat /sys/class/net/eth0/operstate'

# wired connection up
STATUS_ETH0_UP = 'up'

# wired connection down
STATUS_ETH0_DOWN = 'down'

# get wifi ssid, if connected
GET_WIFI_SSID = 'sudo iwgetid -r'

# adapter of the local device
WIFI_ADAPTER = 'wlan0'
#WIFI_ADAPTER = 'wlp59s0'

# class which presents a wifi network (ssid = name, quality = how far am i away)
class Network:
    def __init__(self, ssid, quality):
        self.ssid = ssid
        self.quality = quality

    # convert object to dictionary (used for json serialization)
    def to_dict(self):
      return {"ssid": self.ssid, "quality": self.quality}


@cli.command()
def temp():
    typer.echo('temp')

# adds a network to the wpa_supplicant config file
@cli.command()
def add(network_name: str = typer.Option
                        (..., "--ssid", "-s", help="name of the network"),
            network_pw: str = typer.Option
                        (..., "--pw", "-p", help="password")):

    os.system(ADD_NETWORK %(network_name, network_pw))

@cli.command()
def restart_wifi_driver():
    
    os.system(KILL_WPA_SUPPLICANT)
    os.system(RESTART_WPA_SUPPLICANT)

    # wait some time for connection
    #time.sleep(8)

    # check if we connected
    #ssid=os.popen("sudo iwgetid -r").read()
    
    #if (ssid == network_name):
    #    print("success")
    #else:
    #    print("not_connected")

    #filter(lambda score: score >= 70, scores)

@cli.command()
def is_wired_connected():
    connected_result = os.popen(STATUS_ETH0)
    connected = connected_result.read()

    if (connected.strip() == STATUS_ETH0_UP):
        print(STATUS_ETH0_UP)
    else:
        print(STATUS_ETH0_DOWN)

@cli.command()
def get_wifi_ssid():
    wifi_ssid_result = os.popen(GET_WIFI_SSID)
    wifi_ssid = wifi_ssid_result.read()
    print(wifi_ssid.strip())


# lists a avaiable wifi networks which are visible for the wifi_adapter
@cli.command()
def discover_networks():
    available_networks = list(Cell.all(WIFI_ADAPTER))

    network_list = []

    for network in available_networks:
        
        # calculate percentage quality
        quality_str = network.quality.split("/")
        quality = int((int(quality_str[0])/int(quality_str[1]))*100)

        network_list.append(Network(network.ssid, quality))
    
    network_list.sort(key=lambda x: x.quality, reverse=True)

    # maps list to a dictionary -> create json list
    results = [obj.to_dict() for obj in network_list]
    jsdata = json.dumps(results)
    
    # print is enough because standardOutput is redirected to the PairingService()
    print(jsdata)

if __name__ == '__main__':
    cli()

