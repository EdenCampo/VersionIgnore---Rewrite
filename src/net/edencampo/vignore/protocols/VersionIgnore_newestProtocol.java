package net.edencampo.vignore.protocols;

import net.edencampo.vignore.VersionIgnore;
import net.edencampo.vignore.util.VersionIgnore_Configreader;
import net.edencampo.vignore.util.VersionIgnore_Logger;

import com.comphenix.packetwrapper.WrapperHandshakeClientSetProtocol;
import com.comphenix.packetwrapper.WrapperStatusServerOutServerInfo;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;

public class VersionIgnore_newestProtocol
{
	private VersionIgnore_Logger viLogger;
	private VersionIgnore_Configreader viCfg;
	private VersionIgnore plugin;
	
	private int lClientProto = 0;
	
	public VersionIgnore_newestProtocol(VersionIgnore instance)
	{
		plugin = instance;
		viLogger = plugin.viLogger;
		viCfg = plugin.viCfg;
	}
	
	public void startLoginPacketListener()
	{	
		plugin.protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Handshake.Client.SET_PROTOCOL)
		{		
			@Override
			public void onPacketReceiving(PacketEvent event)
			{				
				if (event.getPacketType() == PacketType.Handshake.Client.SET_PROTOCOL)
				{			
					PacketContainer packet = event.getPacket();
					WrapperHandshakeClientSetProtocol wHPacket = new WrapperHandshakeClientSetProtocol(packet);
					String MCServIP;
					short MCServPort;
					int MCProtocol;
					
					MCProtocol = wHPacket.getProtocolVersion(); // Minecraft client protocol through wrapper
					MCServIP = wHPacket.getServerHostname(); // Minecraft server hostname through wrapper
					MCServPort = wHPacket.getServerPort(); // Minecraft server port through wrapper
					
					/*
						//MCProtocol = packet.getIntegers().read(0); // Minecraft user client protocol field
						//MCServIP = packet.getStrings().read(0); // Minecraft user client typed IP
						//MCServPort = packet.getIntegers().read(1); // Minecraft user client typed port
					 */

					viLogger.logDebug("MCProtocol: " + MCProtocol + ", MCServIP: " + MCServIP + ", MCServPort:" + MCServPort);
					
					if(viCfg.getServerProtocol() == MCProtocol)
					{
						viLogger.logDebug("Client protocol equals to server protocol, manipulation not required, skipping.");
						return;
					}
					
					lClientProto = MCProtocol;
					packet.getIntegers().write(0, viCfg.getServerProtocol()); // Server protocol field
					
					viLogger.logDebug("lClientProto set to: " + MCProtocol + " for later usage.");
					viLogger.logDebug("Wrote integer field 0 and matched to requested protocol so client should be able to connect.");
		        }
		    }
		});
	}
	
	public void startServlistPacketListener()
	{
		plugin.protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Status.Server.OUT_SERVER_INFO)
		{
			@Override
			public void onPacketSending(PacketEvent event)
			{	
				if (event.getPacketType() == PacketType.Status.Server.OUT_SERVER_INFO)
				{	
					if(viCfg.autoConfigureProto() == true)
					{
						PacketContainer packet = event.getPacket();
						WrappedServerPing wServerPing = packet.getServerPings().read(0);
						
						VersionIgnore.SERVER_PROTO = wServerPing.getVersionProtocol();
					}
					
					if(viCfg.applyServerlistFix() == false)
					{
						return;
					}
					
					PacketContainer packet = event.getPacket();
					WrapperStatusServerOutServerInfo wServerInfo = new WrapperStatusServerOutServerInfo(packet);
					WrappedServerPing wServerPing = packet.getServerPings().read(0);
					
					viLogger.logDebug("wServerPing.toJson(): " + wServerPing.toJson());
					
					if(wServerPing.getVersionProtocol() == lClientProto)
					{
						viLogger.logDebug("Client protocol equals to server protocol, server-list manipulation not required, skipping.");
						return;
					}
					else
					{		
						wServerPing.setVersionProtocol(lClientProto);
						
						viLogger.logDebug("wServerPing version protocol set to lClientProto: " + lClientProto);
						
						wServerInfo.setServerPing(wServerPing);
						
						viLogger.logDebug("wServerInfo refreshed data.");	
					}
				}
			}
			
		});
	}
}
