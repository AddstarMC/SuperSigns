package au.com.addstar.signmaker.commands;

import java.io.File;
import java.util.EnumSet;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.script.Script;

public class PlayCommand implements ICommand
{
	@Override
	public String getName()
	{
		return "play";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "signmaker.play";
	}

	@Override
	public String getUsageString( String label, CommandSender sender )
	{
		return label + " <file>";
	}

	@Override
	public String getDescription()
	{
		return "Plays the script at <file>. <file> is relative to the SuperSigns/scripts directory";
	}

	@Override
	public EnumSet<CommandSenderType> getAllowedSenders()
	{
		return EnumSet.allOf(CommandSenderType.class);
	}

	@Override
	public boolean onCommand( CommandSender sender, String parent, String label, String[] args ) throws BadArgumentException
	{
		if (args.length == 0)
			return false;
		
		File file = new File(SignMakerPlugin.getScriptsFolder(), StringUtils.join(args, ' '));
		
		if (!file.exists())
			throw new BadArgumentException(0, "Unknown script file " + file.getPath());
		
		Script script = new Script(SignMakerPlugin.instance);
		script.load(file);
		script.start();
		
		sender.sendMessage(ChatColor.GREEN + "Script started");
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String parent, String label, String[] args )
	{
		return null;
	}

}
