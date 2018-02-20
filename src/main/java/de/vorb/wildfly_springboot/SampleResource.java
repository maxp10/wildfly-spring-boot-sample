package de.vorb.wildfly_springboot;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleResource {

	@RequestMapping("/")
	public String hello() {
		return "response at Request '/'";
	}

	@RequestMapping("/rest")
	public String rest() {
		return "response at Request '/rest'";
	}

	@RequestMapping("/ipl")
	public String ipl() {

		String out = "";
		InetAddress ip;
		try {

			ip = InetAddress.getLocalHost();
			out = "Current IP address : " + ip.getHostAddress();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "response at Request '/ipl' = " + out;
	}

	@RequestMapping("/ip1")
	public String ip1() {

		String out = "";
		Enumeration<NetworkInterface> nets = null;

		try {
			nets = NetworkInterface.getNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (NetworkInterface netint : Collections.list(nets))
			try {
				out = displayInterfaceInformation(netint);
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return "response at Request '/ip1' : " + out;
	}

	static String displayInterfaceInformation(NetworkInterface netint) throws SocketException {

		String out = "";
		out = out + "Display name:" + netint.getDisplayName();
		out = out + "Name:" + netint.getName();
		Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();

		for (InetAddress inetAddress : Collections.list(inetAddresses)) {
			out = out + "InetAddress:" + inetAddress;
		}

		out = out + "Up:" + netint.isUp();
		out = out + "Loopback:" + netint.isLoopback();
		out = out + "PointToPoint:" + netint.isPointToPoint();
		out = out + "Supports multicast:" + netint.supportsMulticast();
		out = out + "Virtual:" + netint.isVirtual();
		out = out + "Hardware address:" + Arrays.toString(netint.getHardwareAddress());
		out = out + "MTU:" + netint.getMTU();

		return out;
	}

	@RequestMapping("/ip")
	public String ip() {

		String out = "";
		try {
			out = getLocalHostLANAddress().toString();
			out = out.substring(1,out.length());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "response at Request '/ip' : " + out;
	}

	private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
		try {
			InetAddress candidateAddress = null;
			// Iterate all NICs (network interface cards)...
			for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
				NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
				// Iterate all IP addresses assigned to each card...
				for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
					InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
					if (!inetAddr.isLoopbackAddress()) {

						if (inetAddr.isSiteLocalAddress()) {
							// Found non-loopback site-local address. Return it immediately...
							return inetAddr;
						} else if (candidateAddress == null) {
							// Found non-loopback address, but not necessarily site-local.
							// Store it as a candidate to be returned if site-local address is not
							// subsequently found...
							candidateAddress = inetAddr;
							// Note that we don't repeatedly assign non-loopback non-site-local addresses as
							// candidates,
							// only the first. For subsequent iterations, candidate will be non-null.
						}
					}
				}
			}
			if (candidateAddress != null) {
				// We did not find a site-local address, but we found some other non-loopback
				// address.
				// Server might have a non-site-local address assigned to its NIC (or it might
				// be running
				// IPv6 which deprecates the "site-local" concept).
				// Return this non-loopback candidate address...
				return candidateAddress;
			}
			// At this point, we did not find a non-loopback address.
			// Fall back to returning whatever InetAddress.getLocalHost() returns...
			InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
			if (jdkSuppliedAddress == null) {
				throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
			}
			return jdkSuppliedAddress;
		} catch (Exception e) {
			UnknownHostException unknownHostException = new UnknownHostException(
					"Failed to determine LAN address: " + e);
			unknownHostException.initCause(e);
			throw unknownHostException;
		}
	}

}
