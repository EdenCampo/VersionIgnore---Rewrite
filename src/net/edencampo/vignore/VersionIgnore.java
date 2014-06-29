package net.edencampo.vignore;

import java.io.IOException;

import net.edencampo.vignore.protocols.VersionIgnore_newestProtocol;
import net.edencampo.vignore.protocols.VersionIgnore_oldProtocol;
import net.edencampo.vignore.util.Metrics;
import net.edencampo.vignore.util.VersionIgnore_Configreader;
import net.edencampo.vignore.util.VersionIgnore_Logger;
import net.edencampo.vignore.util.VersionIgnore_Updater;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class VersionIgnore extends JavaPlugin implements Listener
{
	// TODO: Add detection to server-protocol via listening to the GET_INFO aka. OUT_SERVER_INFO packet?
	
	/*
	 * PacketType.Handshake.Client.SET_PROTOCOL [1.7.5]:
	 * { 
	 *   a = 4,
	 *   b = "localhost",
	 *   c = 25565,
	 *   d = { g = 1, h = [0: "class net.minecraft.server.v1_7_R2.PacketStatusInStart", 1: "class net.minecraft.server.v1_7_R2.PacketStatusInPing"],
	 *   i = [0: "class net.minecraft.server.v1_7_R2.PacketStatusOutServerInfo", 1: "class net.minecraft.server.v1_7_R2.PacketStatusOutPong"],
	 *   name = "STATUS",
	 *   ordinal = 2 }
	 *  }
	 * 
	 * 
	 * PacketType.Handshake.Client.SET_PROTOCOL [1.7.9]:
	 * 
	 * { 
	 * a = 5,
	 * b = "79.179.19.195",
	 * c = 25565,
	 * d = { g = 1, h = [0: "class net.minecraft.server.v1_7_R2.PacketStatusInStart", 1: "class net.minecraft.server.v1_7_R2.PacketStatusInPing"],
	 * i = [0: "class net.minecraft.server.v1_7_R2.PacketStatusOutServerInfo", 1: "class net.minecraft.server.v1_7_R2.PacketStatusOutPong"],
	 * name= "STATUS", 
	 * ordinal = 2 }
	 *  }
	 *  
	 *  [20:10:25 INFO]: [ProtocolLib] Received PacketLoginInStart[0, legacy: 231] from
		UNKNOWN[/127.0.0.1:18671]:
		{ 
			a = net.minecraft.util.com.mojang.authlib.GameProfile@4cdad15[id=<null>,
			name= edencampo,
			properties={},
			legacy=false] 
		}
	 */
	
	public VersionIgnore_Logger viLogger = new VersionIgnore_Logger(this);
	public VersionIgnore_Configreader viCfg = new VersionIgnore_Configreader(this);

	public ProtocolManager protocolManager;
	
	public static int SERVER_PROTO = 0;
	
	public void onEnable()
	{
		new VersionIgnore_Updater(this, 81924, getFile(), VersionIgnore_Updater.UpdateType.DEFAULT, true);
		
		Metrics metrics;
		try
		{
			metrics = new Metrics(this);
			metrics.start();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		saveDefaultConfig();
		saveConfig();
		reloadConfig();
		
		protocolManager = ProtocolLibrary.getProtocolManager();
		
		protocolManager.removePacketListeners(this);
		
		if(viCfg.getProtocolMethod().equalsIgnoreCase("newest"))
		{
			VersionIgnore_newestProtocol viNewProtocol = new VersionIgnore_newestProtocol(this);
			viNewProtocol.startLoginPacketListener();
			viNewProtocol.startServlistPacketListener();
		}
		else
		{
			VersionIgnore_oldProtocol viOldProtocol = new VersionIgnore_oldProtocol(this);
			viOldProtocol.startLoginPacketListener();
			viOldProtocol.startServlistPacketListener();
		}
		
		viLogger.logInfo("Successfully enabled!");
	}
	
	public void onDisable()
	{
		viLogger.logInfo("Successfully disabled!");
	}
}
