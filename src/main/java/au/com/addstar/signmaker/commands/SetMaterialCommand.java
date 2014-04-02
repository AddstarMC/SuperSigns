package au.com.addstar.signmaker.commands;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.TextSign;

public class SetMaterialCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "material";
	}

	@Override
	public String[] getAliases()
	{
		return null;
	}

	@Override
	public String getPermission()
	{
		return "signmaker.set.material";
	}

	@Override
	public String getUsageString( String label, CommandSender sender )
	{
		return label + " <name> <material>";
	}

	@Override
	public String getDescription()
	{
		return "Sets the material of the sign.";
	}

	@Override
	public EnumSet<CommandSenderType> getAllowedSenders()
	{
		return EnumSet.of(CommandSenderType.Player);
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
		
		Material material = Material.valueOf(args[1].toUpperCase());
		if(material == null)
			throw new BadArgumentException(1, "Unknown material " + args[1]);
		
		sign.setMaterial(material);
		
		sign.redraw();
		plugin.saveSign(args[0]);
		sender.sendMessage(ChatColor.GREEN + "Sign Edited");
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String parent, String label, String[] args )
	{
		return null;
	}

}
