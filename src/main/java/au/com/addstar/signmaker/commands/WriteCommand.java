package au.com.addstar.signmaker.commands;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.addstar.signmaker.CharSet;
import au.com.addstar.signmaker.Justification;
import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.TextSign;
import au.com.addstar.signmaker.TextWriter;

public class WriteCommand implements ICommand
{
	@Override
	public String getName()
	{
		return "write";
	}

	@Override
	public String[] getAliases()
	{
		return new String[] { "writetext" };
	}

	@Override
	public String getPermission()
	{
		return "signmaker.write";
	}

	@Override
	public String getUsageString( String label, CommandSender sender )
	{
		return label + " <font> <justification> <material> <text>";
	}

	@Override
	public String getDescription()
	{
		return "Writes text at your location";
	}

	@Override
	public EnumSet<CommandSenderType> getAllowedSenders()
	{
		return EnumSet.of(CommandSenderType.Player);
	}

	@Override
	public boolean onCommand( CommandSender sender, String parent, String label, String[] args ) throws BadArgumentException
	{
		if(args.length != 4)
			return false;
		
		CharSet set = TextWriter.getFont(args[0]);
		if(set == null)
			throw new BadArgumentException(0, "Unknown font");
		
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
		
		Material material = Material.valueOf(args[2].toUpperCase());
		if(material == null)
			throw new BadArgumentException(2, "Unknown material " + args[2]);
		
		if(!material.isBlock() || material.hasGravity() || !material.isSolid())
			throw new BadArgumentException(2, "Material cannot be an item, a block that falls under gravity, or not a full block");
		
		String text = "";
		for(int i = 3; i < args.length; ++i)
		{
			if(!text.isEmpty())
				text += " ";
			text += args[i];
		}
		
		BlockFace face = TextWriter.rotateRight(TextWriter.lookToFace(((Player)sender).getLocation().getYaw()));
		
		sender.sendMessage(ChatColor.GREEN + "Creating text '" + text + "' at your location using justification " + justification.name() + " with font " + set.getName() + " using material " + material.name());
		
		TextSign temp = new TextSign(((Player)sender).getLocation(), face, set.getName());
		temp.setMaterial(material);
		temp.setJustification(justification);
		temp.setText(text);
		temp.redraw();

		SignMakerPlugin.lastSign.put((Player)sender, temp);
		SignMakerPlugin.lastSignName.remove((Player)sender);
		
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String parent, String label, String[] args )
	{
		return null;
	}

}
