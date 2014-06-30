package net.edencampo.vignore.util;

import net.edencampo.vignore.VersionIgnore;

public class VersionIgnore_Configreader
{
	VersionIgnore plugin;
	
	public VersionIgnore_Configreader(VersionIgnore instance)
	{
		plugin = instance;
	}
	
	public String getProtocolMethod()
	{
		return plugin.getConfig().getString("protocolMethod");
	}
	
	public int getServerProtocol()
	{
		return plugin.getConfig().getInt("server-protocol");
	}
	
	public boolean logClientProtocols()
	{
		return Boolean.valueOf(plugin.getConfig().getString("log-client-protocols"));
	}
	
	public boolean autoConfigureProto()
	{
		return Boolean.valueOf(plugin.getConfig().getString("autoConfigureProto"));
	}
	
	public boolean applyServerlistFix()
	{
		return Boolean.valueOf(plugin.getConfig().getString("enable-serverList-fix"));
	}
	
	public String getServerlistName()
	{
		if(plugin.getConfig().getString("serverList-fix-ping-message").equalsIgnoreCase("default"))
		{
			return "A version-ignored server";
		}
		
		return plugin.getConfig().getString("serverList-fix-ping-message");
	}
	
	public boolean enableGhosts()
	{
		return Boolean.valueOf(plugin.getConfig().getString("oldVersion-ghost"));
	}
	
	public boolean logDebug()
	{
		return Boolean.valueOf(plugin.getConfig().getString("logDebug"));
	}
	
	public boolean autoUpdate()
	{
		return Boolean.valueOf(plugin.getConfig().getString("autoUpdate"));
	}
}
