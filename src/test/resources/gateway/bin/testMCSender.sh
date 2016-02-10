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
# -nm ->The number of messages to send.  If not specified it will send infinite number.
# -nilist -> list of the names of the network interfaces to listen for multicast packages. Not including this will listen on ALL network interfaces. 
#
# to change the logging level please edit logging.properties

java -cp $GW_HOME/lib/@JAR_NAME@.jar -Djava.util.logging.config.file="$GW_HOME/bin/logging.properties" -Dnet.java.preferIPv4Stack=true ws.argo.mcg.ws.argo.mcg.comms.MCastMultihomeSender "$@"
