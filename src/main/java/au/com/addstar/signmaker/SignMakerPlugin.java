package au.com.addstar.signmaker;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SignMakerPlugin extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		
	}
	
	@Override
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		if(command.getName().equals("signmaker"))
		{
			if(args.length < 4)
				return false;
			
			String font = args[0];
			CharSet set = null;
			
			File file = new File(getDataFolder(), "fonts/" + font + ".txt");
			if(!file.exists())
			{
				sender.sendMessage(ChatColor.RED + "Unknown font " + font);
				return true;
			}
			
			try
			{
				set = CharSet.load(file);
			}
			catch(IllegalArgumentException e)
			{
				sender.sendMessage(ChatColor.RED + "Could not load font file. " + e.getMessage());
				return true;
			}
			catch(IOException e)
			{
				sender.sendMessage(ChatColor.RED + "Could not load font file. Error reading file.");
				return true;
			}
			
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
			{
				sender.sendMessage(ChatColor.RED + "Unknown value for justification. Valid values are: Left, Center, and Right");
				return true;
			}
			
			Material material = Material.valueOf(args[2].toUpperCase());
			if(material == null)
			{
				sender.sendMessage(ChatColor.RED + "Unknown material " + args[2]);
				return true;
			}
			
			if(!material.isBlock() || material.hasGravity() || !material.isSolid())
			{
				sender.sendMessage(ChatColor.RED + "Material cannot be an item, a block that falls under gravity, or not a full block");
				return true;
			}
			
			String text = "";
			for(int i = 3; i < args.length; ++i)
			{
				if(!text.isEmpty())
					text += " ";
				text += args[i];
			}
			
			BlockFace face = TextWriter.rotateRight(TextWriter.lookToFace(((Player)sender).getEyeLocation().getYaw()));
			
			sender.sendMessage(ChatColor.GREEN + "Creating text '" + text + "' at your location using justification " + justification.name() + " with font " + set.getName() + " using material " + material.name());
			TextWriter.writeText(text, ((Player)sender).getLocation(), face, justification, set, material);
			
			return true;
		}
		
		
		return false;
	}
}
