package net.edencampo.vignore.util;

import net.edencampo.vignore.VersionIgnore;

public class VersionIgnore_Logger
{
	VersionIgnore plugin;
	
	public VersionIgnore_Logger(VersionIgnore instance)
	{
		plugin = instance;
	}
	
	public void logInfo(String msg)
	{
		plugin.viLogger.logDebug("Logging 'info': " + msg);
		
		plugin.getLogger().info(msg);
	}
	
	public void logWarning(String msg)
	{
		plugin.viLogger.logDebug("Logging 'warning': " + msg);
		
		plugin.getLogger().warning(msg);
	}
	
	public void logError(String msg)
	{
		plugin.viLogger.logDebug("Logging 'severe error': " + msg);
		
		plugin.getLogger().severe(msg);
	}
	
	public void logDebug(String msg)
	{
		if(plugin.viCfg.logDebug() == true)
		{
			System.out.println("[DEBUG] " + msg);
		}
	}
}
