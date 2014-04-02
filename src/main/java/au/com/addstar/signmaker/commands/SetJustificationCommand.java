package au.com.addstar.signmaker.commands;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.addstar.signmaker.Justification;
import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.TextSign;

public class SetJustificationCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "justification";
	}

	@Override
	public String[] getAliases()
	{
		return new String[] { "align" };
	}

	@Override
	public String getPermission()
	{
		return "signmaker.set.justification";
	}

	@Override
	public String getUsageString( String label, CommandSender sender )
	{
		return label + " <name> <justification>";
	}

	@Override
	public String getDescription()
	{
		return "Sets the justification of the sign.";
	}

	@Override
	public EnumSet<CommandSenderType> getAllowedSenders()
	{
		return EnumSet.allOf(CommandSenderType.class);
	}

	@Override
	public boolean onCommand( CommandSender sender, String parent, String label, String[] args ) throws BadArgumentException
	{
		if(args.length != 2)
			return false;
		
		SignMakerPlugin plugin = SignMakerPlugin.instance;
		
		TextSign sign = plugin.getSign(args[0]);
		if(sign == null)
			throw new BadArgumentException(0, "That sign does not exist");
		
		Justification justification = null;
		
		for(Justification just : Justification.values())
		{
			if(args[1].equalsIgnoreCase(just.name()))
			{
				justification = just;
				break;
			}
		}
		
		if(justification == null)
			throw new BadArgumentException(1, "Unknown value for justification. Valid values are: Left, Center, and Right");
		
		sign.setJustification(justification);
		
		sign.redraw();
		plugin.saveSign(args[0]);
		if(sender instanceof Player)
			sender.sendMessage(ChatColor.GREEN + "Sign Edited");
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String parent, String label, String[] args )
	{
		if(args.length == 1)
			return SignMakerPlugin.matchString(args[0], SignMakerPlugin.instance.getSignNames());
		else if(args.length == 2)
			return SignMakerPlugin.matchString(args[1], Justification.names);
		
		return null;
	}

}
