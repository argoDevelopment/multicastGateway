/*
 * Copyright 2015 Jeff Simpson.
 *
 * Argo Multicast Gateway is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Foobar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package ws.argo.mcg;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * The GSHandlerThread actually fields UDP multicast packets and processes
 * outbound TCP packets to a paired receiver that is on another network that
 * multicast cannot naturally route to.
 * 
 * <p>
 * It is implemented as a thread to allow scalability for processing.
 * 
 * @author jmsimpson
 *
 */
public class GSHandlerThread extends Thread {
  private static final Logger LOGGER = Logger.getLogger(GSHandlerThread.class.getName());

  private DatagramPacket packet         = null;
  private Socket         outboundSocket = null;
  String                 unicastAddress;
  Integer                unicastPort;
  boolean                allowLoopback;

  /**
   * Creates an instance of the GSHandlerThread.
   * 
   * @param packet the UDP packet to push out on the unicast socket
   * @param ua the unicast socket address
   * @param up the unicast socket port
   * @param allowLoopback flog that will allow loopback in the message - this
   *          can cause bad feedback in the network
   */
  public GSHandlerThread(DatagramPacket packet, String ua, Integer up, boolean allowLoopback) {
    super("GSMulticastSenderHandlerThread");
    this.packet = packet;
    this.unicastAddress = ua;
    this.unicastPort = up;
    this.allowLoopback = allowLoopback;
  }

  /**
   * Process the UDP packet.
   */
  public void run() {

    try {
      InetAddress source = packet.getAddress();

      // Get localhost IP address
      InetAddress localhostIP = InetAddress.getLocalHost();

      // if this packet is sourced locally, then don't send it. This avoids
      // loopbacks with a 2-way gateway.
      // The downside of this is that a 2-way gateway cannot have a probe
      // sender (Argo client) on it.
      boolean isFromLocalhost = source.equals(localhostIP);
      if (!allowLoopback & isFromLocalhost) {
        LOGGER.info("Ignoring packet sourced from localhost");
        return;
      }

      if (allowLoopback & isFromLocalhost)
        LOGGER.info("Loopback prevention disabled. Sending packet sourced from localhost.  I hope you know what you are doing.");

      // Setup for outbound unicast
      // Connect to the remote gateway
      outboundSocket = new Socket(unicastAddress, unicastPort.intValue());

      LOGGER.fine("Received packet");
      LOGGER.fine("Packet contents:");
      // Get the string
      String xferPacket = new String(packet.getData(), 0, packet.getLength());
      LOGGER.info("xferPacket: \n" + xferPacket);

      try {
        outboundSocket.getOutputStream().write(packet.getData(), 0, packet.getLength());
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        outboundSocket.close();
      }

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
