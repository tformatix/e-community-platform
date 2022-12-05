# eCommunity Platform
![energiegemeinschaft](https://user-images.githubusercontent.com/23616275/204872236-02eb25f8-828b-42e1-a036-3c7bbf8e8928.jpg)
The project "eCommunity Platform" makes the finding and coordination of energy communities easier. Within the platform, users can easily compare all potential communities, join them and monitor the current price and energy savings in near real time. This project was created by [Tobias Fischer](https://github.com/tformatix) and [Michael Zauner](https://github.com/Maikaru-Sensei) at FH Hagenberg.

Currently, citizens have to find out for themselves which energy communities they can join and how they perform. Joining an energy community is complicated by factors such as a lack of contact channels, and even after joining, it takes a lot of effort to keep track of costs, members and performance.

With the app "eCommunity Platform" a platform for energy communities is offered, with the aim to let users see their consumption data in real time and provide them with more insight into energy communities. In doing so, we aim to enable immediate response to anomalies and increases in consumption, within their own household as well as across the energy community.
We try to simplify all the necessary steps: from setting up, to searching, to joining an energy community. Furthermore, monitoring and improvement of performance will be enabled through our platform.
In addition, we want to encourage to use electricity when it is in surplus and support users to save electricity when the feed-in from e.g. solar energy of the energy community is low. In this way, users can not only save money, but also reduce their environmental footprint.

A library of the local network operator Netz OÖ GmbH already takes care of reading, decoding and storing the data of the AMIS Smart Meter. Unfortunately, this library cannot be published.

## Local Energy Monitoring
In the second semester, we started to implement an app to monitor the local energy data provided by the smart meter. The charts were visualized using [MPAndroidChart from PhilJay](https://github.com/PhilJay/MPAndroidChart).

## Architecture
FOTO here
Every Smart Meter device is connected to a Raspberry PI via either a infrared cable or the Smart Meter adapter from OesterreichsEnergie. On the Raspberry there are two major Linux systemd processes running:

* **e-community-smart-meter-reader**: reads and decrypts the energy data from the Smart Meter and stores it inside a local SQLite database
* **e-community-local**: provides a ASP .NET Core API for communication with the cloud services and local network devices

On the cloud service there is also a ASP .NET API running, it is connected to an MSSQL database. This service is responsible for login/register, pairing the Smart Meter device, initialize realtime connection from the Raspberry with the mobile devices and much more.

On each Raspberry PI is a [SignalR](https://learn.microsoft.com/en-us/aspnet/signalr/) Listener running. This is a library for realtime connection between multiple devices. In the cloud is the SignalR-Hub running which is responsible for redirecting the data stream to the listening endpoints.

E.g.:
1) Alice wants to see her current energy data on the mobile phone
2) Mobile device starts SignalR listener and connects to cloud SignalR-Hub
3) Cloud SignalR-Hub connects to the Raspberry PI's listener
4) Raspberry PI starts to read the value from the SQLite database and sends it to the cloud
5) Cloud redirects data stream to the mobile application
6) Mobile devices gets energy data and displays them in a TileView

## Pairing of Smart-Meters
When a user wants to first configure the Raspberry PI, we need to know the IP-address of the device to connect it with the mobile device. Therefore on the Raspberry is a Multicast DNS (mDNS) service running, which broadcast to the network the .local hostname of the devices. For the first configuration the Raspberry and the mobile phone needs to be in the same network.

After the device was found in the mobile app, the user can configure the desired network configuration (change Wifi, update name of Smart-Meter, ...). The user also needs to add the AES-Key for encrypting the energy data. This key can found on the user's network provider page.

## Key Features
* **Near Realtime energy data from home to mobile** 🚀📱
* **Easy first configuration via interactive pairing mode** ✅
* **Find other energy enthusiasts and be part of your local eCommunity**🧑🏽‍🤝‍🧑🏻
* **Help to save energy and make our planet happy again** 🌎


## Bachelor Thesis (in progress)

### Consent Managment Platform for sharing and monetization of Energy Data with a private blockchain
_Michael Zauner_
* connect all smart meters in a private blockchain (Hyperledger Sawtooth)
* share energy data between members and other organizations
* store contract (consent) inside the blockchain

### Heat pump control based on near real-time energy data and various other influences
_Tobias Fischer_
* how to control a heat pump based on various influences, f.e.
  * household energy surplus
  * energy communities
  * energy costs/yields
  * weather
  * actual/setpoint temperature
  * battery/buffer storage
* control heat pump over SG Ready interface
