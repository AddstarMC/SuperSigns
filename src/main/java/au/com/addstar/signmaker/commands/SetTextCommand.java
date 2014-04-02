package au.com.addstar.signmaker.commands;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.TextSign;

public class SetTextCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "text";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "signmaker.set.text";
	}

	@Override
	public String getUsageString( String label, CommandSender sender )
	{
		return label + " <name> <text>";
	}

	@Override
	public String getDescription()
	{
		return "Sets the text on the sign.";
	}

	@Override
	public EnumSet<CommandSenderType> getAllowedSenders()
	{
		return EnumSet.of(CommandSenderType.Player);
	}

	@Override
	public boolean onCommand( CommandSender sender, String parent, String label, String[] args ) throws BadArgumentException
	{
		if(args.length < 2)
			return false;
		
		SignMakerPlugin plugin = SignMakerPlugin.instance;
		
		TextSign sign = plugin.getSign(args[0]);
		if(sign == null)
			throw new BadArgumentException(0, "That sign does not exist");
		
		String text = "";
		for(int i = 1; i < args.length; ++i)
		{
			if(!text.isEmpty())
				text += " ";
			text += args[i];
		}
			
		sign.setText(text);
		
		sign.redraw();
		
		sender.sendMessage(ChatColor.GREEN + "Sign Edited");
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String parent, String label, String[] args )
	{
		return null;
	}

}
