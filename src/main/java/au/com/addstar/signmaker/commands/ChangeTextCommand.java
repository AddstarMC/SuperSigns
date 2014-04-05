package au.com.addstar.signmaker.commands;

import java.util.EnumSet;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.TextSign;
import au.com.addstar.signmaker.TransitionRunner;
import au.com.addstar.signmaker.TransitionType;

public class ChangeTextCommand implements ICommand
{

	@Override
	public String getName()
	{
		return "change";
	}

	@Override
	public String[] getAliases()
	{
		return new String[] {"changetext", "transition"};
	}

	@Override
	public String getPermission()
	{
		return "signmaker.transition";
	}

	@Override
	public String getUsageString( String label, CommandSender sender )
	{
		return label + " <name> <effect> <speed> <text>";
	}

	@Override
	public String getDescription()
	{
		return "Sets the text on the sign using a transition effect. Speed: lower value is faster";
	}

	@Override
	public EnumSet<CommandSenderType> getAllowedSenders()
	{
		return EnumSet.allOf(CommandSenderType.class);
	}

	@Override
	public boolean onCommand( CommandSender sender, String parent, String label, String[] args ) throws BadArgumentException
	{
		if(args.length < 4)
			return false;
		
		SignMakerPlugin plugin = SignMakerPlugin.instance;
		
		TextSign sign = plugin.getSign(args[0]);
		if(sign == null)
			throw new BadArgumentException(0, "That sign does not exist");
		
		if(sign.getCurrentTransition() != null)
		{
			sender.sendMessage(ChatColor.RED + "A transition is already in progress for that sign.");
			sender.sendMessage(ChatColor.GRAY + "Either wait, or use " + ChatColor.YELLOW + parent + "cancel " + args[0]);
			return true;
		}
		
		TransitionType type = null;
		for(TransitionType t : TransitionType.values())
		{
			if(args[1].equalsIgnoreCase(t.name()))
			{
				type = t;
				break;
			}
		}
		
		if(type == null)
			throw new BadArgumentException(1, "Unknown transition type");
		
		int speed;
		try
		{
			speed = Integer.parseInt(args[2]);
			if(speed <= 0)
				throw new BadArgumentException(2, "Minimum value for speed is 1");
		}
		catch(NumberFormatException e)
		{
			throw new BadArgumentException(2, "Speed must be number 1 or greater");
		}
		
		String text = "";
		for(int i = 3; i < args.length; ++i)
		{
			if(!text.isEmpty())
				text += " ";
			text += args[i];
		}
		
		text = text.replaceAll("&v", "\n");
		
		String lastText = sign.getText();
		sign.setText(text);
		
		if(type == TransitionType.None)
			sign.redraw();
		else
			new TransitionRunner(type, sign, lastText, speed).start(SignMakerPlugin.instance);
		
		plugin.saveSign(args[0]);
		if(sender instanceof Player)
			sender.sendMessage(ChatColor.GREEN + "Sign Changed");
		return true;
	}

	@Override
	public List<String> onTabComplete( CommandSender sender, String parent, String label, String[] args )
	{
		if(args.length == 1)
			return SignMakerPlugin.matchString(args[0], SignMakerPlugin.instance.getSignNames());
		else if(args.length == 2)
			return SignMakerPlugin.matchString(args[1], TransitionType.names);
		
		return null;
	}

}
