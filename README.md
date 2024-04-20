# eCommunity Platform
<p>
    <img src="https://user-images.githubusercontent.com/23616275/204872236-02eb25f8-828b-42e1-a036-3c7bbf8e8928.jpg" alt>
    <em>© energiegemeinschaften.gv.at</em>
</p>

The project "eCommunity Platform" makes the finding and coordination of energy communities easier. Within the platform, users can easily compare all potential communities, join them and monitor the current price and energy savings in near real time. This project was created by [Tobias Fischer](https://github.com/tformatix) and [Michael Zauner](https://github.com/Maikaru-Sensei) at FH Hagenberg.

Currently, citizens have to find out for themselves which energy communities they can join and how they perform. Joining an energy community is complicated by factors such as a lack of contact channels, and even after joining, it takes a lot of effort to keep track of costs, members and performance.

With the app "eCommunity Platform" a platform for energy communities is offered, with the aim to let users see their consumption data in real time and provide them with more insight into energy communities. In doing so, we aim to enable immediate response to anomalies and increases in consumption, within their own household as well as across the energy community.
We try to simplify all the necessary steps: from setting up, to searching, to joining an energy community. Furthermore, monitoring and improvement of performance will be enabled through our platform.
In addition, we want to encourage to use electricity when it is in surplus and support users to save electricity when the feed-in from e.g. solar energy of the energy community is low. In this way, users can not only save money, but also reduce their environmental footprint.

## ⚠️ Prototype

This repository serves as the home for the early prototype of our ambitious eCommunity Platform. Please bear in mind that this project is currently in its prototype phase and is not under active development. As such, it lacks essential elements like tests and other components necessary for a polished project.

### What to Expect

- Early Prototype: Explore our initial concept and vision for the eCommunity Platform.
- Not Actively Developed: Please note that this prototype is not actively maintained or expanded upon at the moment.
- Work in Progress: As a work in progress, this repository might lack certain features and best practices found in fully developed projects.

## M-Bus Slave

A client of the local network operator Netz OÖ GmbH by Dipl.-Ing. Dr. techn. Markus Flohberger already takes care of reading, decoding and storing the data of the AMIS Smart Meter. It is also based on [libmbus](http://www.rscada.se/libmbus/) (M-bus Library from Raditex Control) and the AES decryption by Andreas Tetzner. This client was adapted for the purposes of this project. Currently the project is only compatible with AMIS meter of Netz OÖ GmbH.

## Local Energy Monitoring
In the second semester, we started to implement an app to monitor the local energy data provided by the smart meter. The charts were visualized using [MPAndroidChart from PhilJay](https://github.com/PhilJay/MPAndroidChart). This app is the first version of this project, further implementations are based on eCommunities.

## Architecture
![Screenshot from 2022-12-01 15-29-26](https://user-images.githubusercontent.com/23616275/205596847-60c5d5e0-707a-43db-9862-afc9a23b8668.png)
Every Smart Meter device is connected to a Raspberry PI via either a infrared cable or the Smart Meter adapter from OesterreichsEnergie (not yet available). On the Raspberry there are two major Linux systemd processes running:

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

## 3rd place at Young EPCON Award 2022 🏆
As part of the EPCON energy congress, sustainable and innovative projects with potential for change are awarded annually by imh GmbH. In 2022, we were able to achieve 3rd place.

## Bachelor Project/Thesis

### Decentralized Consent Management Platform for Monetization of Energy Data
_Michael Zauner_
* share and sell user's own energy data between the members of the eCommunity platform
* the contract between two involved parties is stored inside a Consent-Contract
* creation, storage and management of the consent contract is done in a GoQuorum Blockchain (fork of the Ethereum Blockchain)
* all Raspberry Pi's have a local Blockchain service running

### Optimization of energy communities incorporating forecasting, participant behavior and monitoring of actual behavior in near real time
_Tobias Fischer_
* generation of hourly forecasts
  * consumption/feed in
  * members receive allocated amount of energy from the energy community
* adjustable flexibility
  * could consume more/less
* monitoring of actual behavior in near real time
* in future: automated via a smart home system
