package au.com.addstar.signmaker.commands;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import au.com.addstar.signmaker.CharSet;
import au.com.addstar.signmaker.TextWriter;

public class ListFontsCommand implements ICommand
{
	@Override
	public String getName()
	{
		return "fonts";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "signmaker.list.fonts";
	}

	@Override
	public String getUsageString( String label, CommandSender sender )
	{
		return label;
	}

	@Override
	public String getDescription()
	{
		return "Lists all fonts available";
	}

	@Override
	public EnumSet<CommandSenderType> getAllowedSenders()
	{
		return EnumSet.allOf(CommandSenderType.class);
	}

	@Override
	public boolean onCommand( CommandSender sender, String parent, String label, String[] args ) throws BadArgumentException
	{
		if(args.length != 0)
			return false;
		
		List<String> fontNames = TextWriter.getFonts();
		
		sender.sendMessage(ChatColor.GOLD + "Fonts available: " + ChatColor.RED + fontNames.size());
		
		for(String fontName : fontNames)
		{
			CharSet font = TextWriter.getFont(fontName);
			sender.sendMessage(ChatColor.GRAY + " " + fontName + " " + ChatColor.YELLOW + "height: " + ChatColor.GRAY + font.getHeight() + ChatColor.YELLOW);
		}
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String parent, String label, String[] args )
	{
		return null;
	}

}
