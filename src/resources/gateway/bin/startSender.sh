#!/bin/sh
# 
#  Copyright 2015, 2016 Jeff Simpson.
#
#  This program is free software: you can redistribute it and/or modify
#  it under the terms of the GNU General Public License as published by
#  the Free Software Foundation, either version 3 of the License, or
#  (at your option) any later version.
#
#  This program is distributed in the hope that it will be useful,
#  but WITHOUT ANY WARRANTY; without even the implied warranty of
#  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#  GNU General Public License for more details.
#
#  You should have received a copy of the GNU General Public License
#  along with this program.  If not, see <http://www.gnu.org/licenses/>.
# 
# Expected arguments on the command line include:
# -ma -> (required) The multicast group to send to.  The standard Argo group is 230.0.0.1 
# -mp -> (required) The multicast port associated with the group.  The standard Argo port is 4003
# -ua -> (required) The unicast IP address of the gateway receiver on the other side of the VPN 
# -up -> (required) The unicast port of the gateway receiver on the other side of the VPN 
# -ni -> (required) The name of the network interface to listen and send on.  e.g. eth0, en0, utun1 or wherever network NIC you like (see ifconfig in unix and ipconfig in Windows)
#
# to change the logging level please edit logging.properties

java -cp @INSTALL_DIR@/gateway/lib/@JAR_NAME@.jar -Djava.util.logging.config.file="@INSTALL_DIR@/gateway/bin/logging.properties" -Dnet.java.preferIPv4Stack=true ws.argo.mcg.GatewaySender "$@"
