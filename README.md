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

## Work in progress... (Bachelor Thesis)

### Consent Managment Platform for sharing and monetization of Energy Data with a private blockchain
* connect all smart meters in a private blockchain (Hyperledger Sawtooth)
* share energy data between members and other organizations
* store contract (consent) inside the blockchain

### Heat pump control based on near real-time energy data and various other influences
* how to control a heat pump based on various influences, f.e.
  * household energy surplus
  * energy communities
  * energy costs/yields
  * weather
  * actual/setpoint temperature
  * battery/buffer storage
* control heat pump over SG Ready interface
