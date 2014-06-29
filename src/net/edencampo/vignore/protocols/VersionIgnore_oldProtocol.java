package net.edencampo.vignore.protocols;

import com.comphenix.protocol.Packets;
import com.comphenix.protocol.events.ConnectionSide;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;
import com.comphenix.protocol.reflect.StructureModifier;

import net.edencampo.vignore.VersionIgnore;
import net.edencampo.vignore.util.VersionIgnore_Configreader;
import net.edencampo.vignore.util.VersionIgnore_Logger;

@SuppressWarnings("deprecation")
public class VersionIgnore_oldProtocol
{
	private VersionIgnore_Logger viLogger;
	private VersionIgnore_Configreader viCfg;
	private VersionIgnore plugin;
	
	
	public VersionIgnore_oldProtocol(VersionIgnore instance)
	{
		plugin = instance;
		viLogger = plugin.viLogger;
		viCfg = plugin.viCfg;
	}

	public void startLoginPacketListener()
	{
		plugin.protocolManager.addPacketListener(new PacketAdapter(plugin, ConnectionSide.CLIENT_SIDE, ListenerPriority.HIGHEST, GamePhase.LOGIN, Packets.Client.HANDSHAKE)
		{
			String username = "UNKNOWN";
		
			@Override
			public void onPacketReceiving(PacketEvent event)
			{	
				if (event.getPacketID() == Packets.Client.HANDSHAKE)
				{
					viLogger.logDebug("Recieved a handshake packet.");
					
					PacketContainer packet = event.getPacket();
		         
					int MCProtocol = packet.getIntegers().read(0);
					username = packet.getStrings().read(0);
					
					viLogger.logDebug("Read packet, username = " + username);

					if(viCfg.getServerProtocol() == MCProtocol)
					{
						viLogger.logDebug("Client protocol equals to server protocol, manipulation not required, skipping.");
						return;
					}
					
					packet.getIntegers().write(0, viCfg.getServerProtocol());
					
					viLogger.logDebug("Wrote field 0 in handshake, set to " + viCfg.getServerProtocol());
					
					/*
					if(getConfig().getString("warnPlayers").equalsIgnoreCase("true") || getConfig().getString("warnPlayers").equalsIgnoreCase("yes"))
					{
						viLogger.logDebug("Started a runnable for " + username + " to send a warning!");
						
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
						{
							public void run()
							{
								Player p = Bukkit.getPlayerExact(username);
								
								if(p == null)
								{
									viLogger.logDebug("For some reason; " + username + " is still null, probably connecting issue?");
									
									return;
								}
								
								p.sendMessage(prefix + ChatColor.YELLOW + "Hello, " + username + "!");
								p.sendMessage(ChatColor.DARK_GREEN + "Looks like you have logged in from a version that differs to this servers version, you have been let in because of VersionIgnore!");
								p.sendMessage(ChatColor.DARK_GREEN + "I just wanted to inform you, that crashes and buggy stuff may occur if you keep playing on this version..");
								p.sendMessage(ChatColor.DARK_GREEN + "Be sure to watchout from this kind of stuff!");
								
								viLogger.logDebug("Sent warning messages to " + username + "!");
							}
							
						}, 90L);
					}
					
					if(getConfig().getString("oldVersion-ghost").equalsIgnoreCase("true"))
					{
						Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
						{
							public void run()
							{
								Player p = Bukkit.getPlayerExact(username);
								
								if(p == null)
								{
									viLogger.logDebug("For some reason; " + username + " is still null, probably connecting issue?");
									
									return;
								}
								
								if(!ghosts.contains(p.getName()))
								{
									ghosts.add(p.getName());
									p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 30*20));
									
									viLogger.logDebug("Added " + p.getName() + " to ghosts arraylist");
									p.sendMessage(prefix + ChatColor.DARK_RED + "You are now a ghost!");
								}
							}
							
						}, 90L);
					}
					*/
					
					viLogger.logDebug("Finished process for " + username);
		        }
		    }
		});
	}
	
	public void startServlistPacketListener()
	{
		plugin.protocolManager.addPacketListener(new PacketAdapter(plugin, ConnectionSide.BOTH, ListenerPriority.HIGHEST, GamePhase.LOGIN, new Integer[] { Integer.valueOf(255), Integer.valueOf(Packets.Client.GET_INFO) })
		{
			int proto;
			
			@Override
			public void onPacketReceiving(PacketEvent event)
			{	
				try
				{
					PacketContainer packet = event.getPacket();
					
					int clientInfoProto = packet.getIntegers().read(0); // Minecraft client pinger protocol
					proto = clientInfoProto;
				}
				catch (Exception localException)
				{
					viLogger.logError("ERROR: localException fired from onPacketReceiving (GET_INFO)!");
				}
			}
			
			public void onPacketSending(PacketEvent event)
			{
				try
				{
					PacketContainer packet = event.getPacket();

					StructureModifier<String> packetString = packet.getSpecificModifier(String.class);
					
					viLogger.logDebug("Current packetString: " + packet.getStrings().read(0));
					
					if(packet.getStrings().read(0).equalsIgnoreCase("Protocol error"))
					{
						viLogger.logError("Received protocol error from KICK_DISCONNECT!");
						return;
					}
					
					String packetFix = packet.getStrings().read(0).replaceFirst(String.valueOf(viCfg.getServerProtocol()), String.valueOf(proto)); // Rewrite the packet
					packetString.write(0, packetFix); // Apply fix
				}
				catch (Exception localException)
				{
					viLogger.logError("ERROR: localException fired from onPacketSending (KICK_DISCONNECT)!");
				}
		    }
		});
	}
}
