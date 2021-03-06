package au.com.addstar.signmaker.commands;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.TextSign;

public class RemoveCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "remove";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "signmaker.remove";
	}

	@Override
	public String getUsageString( String label, CommandSender sender )
	{
		return label + " <name>";
	}

	@Override
	public String getDescription()
	{
		return "Removes the sign";
	}

	@Override
	public EnumSet<CommandSenderType> getAllowedSenders()
	{
		return EnumSet.of(CommandSenderType.Player);
	}

	@Override
	public boolean onCommand( CommandSender sender, String parent, String label, String[] args ) throws BadArgumentException
	{
		if(args.length != 1)
			return false;
		
		SignMakerPlugin plugin = SignMakerPlugin.instance;
		
		TextSign sign = plugin.getSign(args[0]);
		if(sign == null)
			throw new BadArgumentException(0, "That sign does not exist");
		
		sign.clear();
		
		plugin.removeSign(args[0]);
		plugin.saveSign(args[0]);
		sender.sendMessage(ChatColor.GOLD + "Sign Removed");
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String parent, String label, String[] args )
	{
		if(args.length == 1)
			return SignMakerPlugin.matchString(args[0], SignMakerPlugin.instance.getSignNames());
		
		return null;
	}

}
