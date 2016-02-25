#Argo Multicast Gateway

##About

Argo is a dynamic service discovery protocol that can, if configured appropriately, use IP multicast to transport service probe messages to Argo protocol responders listening on the multicast transport configured with the same group (multicast IP address).  You can learn more about Argo by going to [the Argo documentation website](http://www.argo.ws) or you can go right to the [Argo Github site](https://github.com/di2e/Argo)
 
Multicast is a group communication pub/sub protocol.  According to Wikipedia:

> In computer networking, multicast ([one-to-many or many-to-many distribution][1]) is [group communication][2] where information is addressed to a group of destination computers simultaneously. Multicast should not be confused with physical layer point-to-multipoint communication.

[1]: http://books.google.se/books?id=EUUqAAAACAAJ&dq=multicast+%22one+to+many%22+television&hl=sv&sa=X&ei=bYNyU_q-FofyyAPKu4GYAQ&redir_esc=y "Lawrence Harte, Introduction to Data Multicasting, Althos Publishing 2008."
[2]: http://books.google.se/books?id=I4excGQVTVkC&pg=PA302&dq=multicast+%22one+to+many%22+%22group+communication%22+-IP_multicast&hl=sv&sa=X&ei=iIRyU7rEJemdyQPktICQCA&ved=0CDIQ6AEwAA#v=onepage&q=multicast%20%22one%20to%20many%22%20%22group%20communication%22%20-IP_multicast&f=false "Media-communication based on Application-Layer Multicast"

This scheme works great in a local network that easily allows multicast traffic.  However, Argo is a "long-range" discovery protocol, meaning that it's built to solve the problem of working across multiple networks and specifically [Mobile Ad Hoc Networks (MANETs)](https://en.wikipedia.org/wiki/Mobile_ad_hoc_network).  In these situations, you want discovery probes to move from network to network.  An Argo probe should contain "respondTo" address that any responder on any network can send a response to - e.g. the address is can be routed from any target network.  How this routing is done in MANETs is a function of special MANET protocols.  To see how this works, it may be best to see the description of how the Argo protocol works first and the

The best way to route multicast UDP packets is via a PIM (Protocol Independent Multicast) routing protocol or via a GRE Tunnel that directly links the networks (much like a VPN channel - actually most VPNs do not allow multicast to traverse the link).  However, you don't always have a network that will allow such a "native" channel to be created to seemlessley move multicast UDP traffic for one reason or another.

Occasionally, you will really need to move multicast from one network to another.  The way to do this is to use a general purpose Multicast Gateway that can link two networks together and move UDP traffic from one gateway process to another.  This is what the Argo Multicast Gateway was built to do.

If you really find yourself in this situation, you might want to look at some other probe pub/sub mechanism other than multicast.  Sure, all the other cool service discovery protocols use multicast (Multicast DNS, WS-Discovery, AllJoyn, SSDP, etc.).  Why can't you?  Well, these protocols DON'T operate over multiple networks either, so don't feel bad.  In fact, if you want to use the Argo Multicast Gateway to move the UDP discovery messages from these other protocols, you are more than welcome to use the protocol.  It will even work, however, the answers you get back across the network might be useless because the IP addresses of the discovered service will likely not be addressable from the other network.  Most of the other protocols use "link-local" addresses that can **only** be used in a local network.  Sorry.

So, when doing non-natively routed probe messages, you'll likely need to use a tertiary pub/sub mechanism that can be addressed by both network you are trying to bridge.  Something like MQTT would work well.  

However, if you can route multicast messages around, then I would recommend that for a number of reasons.  These reasons are listed in detail in the Argo protocol documentation.

#The Gateway

This project is ancillary and really independent of Argo.  The Multicast Gateway is capable of moving any multicast UDP protocol from one network, move it to another network via unicast TCP and the rebroadcast the UDP packets in the target network.  

The gateway has two components.

 * **The Gateway Sender** - listens for multicast traffic on a configured channel and forwards that traffic via unicast TCP to a configured Receiver on another network.
 * **The Gateway Receiver** - listens for unicast TCP traffic on a configured port and replays that traffic as multicast UDP into the local network on the configured multicast channel.
 
The monikers (Sender and Receiver) are derived from the perspective of the direction of multicast traffic.
 
The Gateway components are unidirectional.  They don't work both ways.  However, you can run a Sender and a Receiver in the same network to enable two way communications between networks.  A Receiver can receive traffic from any number of other networks.
 
![Alt text](/Users/jmsimpson/intelij/multicastGateway/src/resources/gateway/docs/multicast_gateway_overview.svg)


#Installation

You can install the Multicast Gateway Sender and Receiver via two methods: RPM and ZIP/Tarball File.  The RPM is targeted at Linux systems that support RPM or YUM.  The ZIP/Tarball File is targeted at Linux/Windows systems.  You can download the latest release of the Multicast Gateway from [here](https://github.com/argoDevelopment/multicastGateway/releases/latest)

##Prerequisites

You *must* have Java 1.7 or higher installed and the java command *must* be on the on the path.

##Installing the Gateway via rpm or yum

After downloading the `argo-gateway-1.0.0-1.noarch.rpm` perform the following command:

```
yum localinstall argo-gateway-1.0.0-1.noarch.rpm
```

or 

```
rpm -ivh argo-gateway-1.0.0-1.noarch.rpm
```
 
##Installing the Gateway view the tar.gz or zip file

If you don't have `rpm` or you are installing on a Windows platform, you can use the tar.gz/zip files.  They both contain the same content.

Just unzip the content into your target installation directory (e.g. C:/gateway).  Then you need to edit the .bat files.

###Modify Responder Configuration Files

In each `.bat` file, replace the @INSTALL_DIR@ text with the path of the Argo install directory. Sorry about this. The RPM install is easier as it makes an assumption about installation location. I can’t do that in Windows with a zip file.

#Usage
 
 The following section provides the details on how to launch the Multicast Gateway Sender and Receiver processes.  These processes are executed via a command line executable.
 
##The Gateway Sender

The Gateway Sender component *listens* for mulitcast UDP packets and then *sends* them via unicast TCP over the WAN to a Gateway Receiver.

In order to start the Gateway Sender, you need to provide a couple of configuration parameters.  Theses include the multicast group and port on which to listen and the unicast TCP/IP address to send the packets to.

The Gateway Sender needs to be launched with a number of command line configuration options.

###Command Line Options

***All command line options are required***


| Option  | Description |
|:---|---|
| -ma | The multicast group to listen to.  The standard Argo group is 230.0.0.1 |
| -mp | The multicast port associated with the group.  The standard Argo port is 4003 |
| -ua | The unicast IP address of the gateway receiver on the other side of the VPN, GRE Tunnel, SDN or whatever |
| -up | The unicast port of the gateway receiver on the other side |
| -ni | The name of the network interface to listen and send on.  e.g. eth0, en0, utun1 or wherever network NIC you like (see [ifconfig](http://net-tools.sourceforge.net/man/ifconfig.8.html) in unix and [ipconfig](https://technet.microsoft.com/en-us/library/dd197434\(WS.10\).aspx) in Windows) |

### Examples

####Argo Protocol Example

The following example shows how to start a Gateway Sender on the standard Argo address

```
startSender -ma 230.0.0.1 -mp 4003 -ua _\<some destination IP for the receiver\>_ -mp _\<the port of the receiver/>_ -ni eth0
```

####mDNS Protocol Example

The following example shows how to start a Gateway Sender on the standard Multicast DNS (Bonjour) address

```
startSender -ma 224.0.0.251 -mp 5353 -ua _\<some destination IP for the receiver\>_ -up _\<the port of the receiver/>_ -ni eth0
```

####WS-Discovery Protocol Example

The following example shows how to start a Gateway Sender on the standard WS-Discovery address

```
startSender -ma 239.255.255.250 -mp 3702  -ua _\<some destination IP for the receiver\>_ -up _\<the port of the receiver/>_ -ni eth0
```


##The Gateway Receiver

The Gateway Receiver component *receives* unicast TCP over the WAN and then*sends* them via multicast UDP packets to the configured multicast group and port.

In order to start the Gateway Receiver, you need to provide a couple of configuration parameters.  Theses include the port on which to listen to incoming transmissions from senders and the multicast group and port on which to rebroadcast the packets.

The Gateway Receiver needs to be launched with a number of command line configuration options.

###Command Line Options

***All command line options are required***


| Option  | Description |
|:---|---|
| -dnr | if this switch is set then "do not repeat" the traffic onto the multicast group - you need to know what you're doing here |
| -ma | The multicast group to rebroadcast incoming TCP packets to.  The standard Argo group is 230.0.0.1 |
| -mp | The multicast port associated with the group.  The standard Argo port is 4003 |
| -up | The unicast port of the gateway receiver server (must be open on the firewall to this machine) |
| -ni | The name of the network interface to listen and send on.  e.g. eth0, en0, utun1 or wherever network NIC you like (see [ifconfig](http://net-tools.sourceforge.net/man/ifconfig.8.html) in unix and [ipconfig](https://technet.microsoft.com/en-us/library/dd197434\(WS.10\).aspx) in Windows) |

### Examples

####Argo Protocol Example

The following example shows how to start a Gateway Receiver on the standard Argo address.  The port you specify on which the Receiver will listen is up to you.  However, make sure that your firewall allows incoming traffic on that port.

```
startReceiver -up _\<some port\>_ -ma 230.0.0.1 -mp 4003 -ni eth0
```

####mDNS Protocol Example

The following example shows how to start a Gateway Receiver on the standard Multicast DNS (Bonjour) address

```
startReceiver -up _\<some port\>_ -ma 224.0.0.251 -mp 5353 -ni eth0
```

####WS-Discovery Protocol Example

The following example shows how to start a Gateway Sender on the standard WS-Discovery address

```
startReceiver -up _\<some port\>_ -ma 239.255.255.250 -mp 3702 -ni eth0
```
