package au.com.addstar.signmaker.script;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;

import au.com.addstar.signmaker.Justification;
import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.TextSign;
import au.com.addstar.signmaker.TextWriter;
import au.com.addstar.signmaker.TransitionRunner;
import au.com.addstar.signmaker.TransitionType;

public class Action
{
	private Script mScript;
	
	private ActionType mType;
	private String mSignId;
	private TextSign mSign;
	private Object[] mParams;
	
	public Action(Script script)
	{
		mScript = script;
	}
	
	public void load(String[] args) throws IllegalArgumentException
	{
		validateLength(args, 2, "There is no action specified");
		
		if (args[0].equalsIgnoreCase("exec"))
		{
			mType = ActionType.Exec;
			mParams = new Object[] {StringUtils.join(args, ' ', 1, args.length)};
			return;
		}
		
		mSignId = args[0];
		
		mType = null;
		for(ActionType type : ActionType.values())
		{
			if (type == ActionType.Exec)
				continue;
			if (type.name().equalsIgnoreCase(args[1]))
			{
				mType = type;
				break;
			}
		}
		
		if (mType == null)
			throw new IllegalArgumentException("Unknown action type " + args[1]);
		
		switch(mType)
		{
		case Align:
			validateLength(args, 3, "Expected: align <left|center|right>");
			mParams = new Object[] {loadJustification(args[2])};
			break;
		case Font:
			validateLength(args, 3, "Expected: font <name>");
			if (TextWriter.getFont(args[2]) == null)
				throw new IllegalArgumentException("Unknown font " + args[2]);
			mParams = new Object[] {args[2]};
			break;
		case Material:
			validateLength(args, 3, "Expected: material <type>");
			
			mParams = new Object[] {loadMaterial(args[2])};
			break;
		case Text:
			validateLength(args, 3, "There is no text specified");
			mParams = new Object[] {StringUtils.join(args, ' ', 2, args.length).replace("&v", "\n")};
			break;
		case Change:
			validateLength(args, 5, "Expected: change <effect> <speed> <text>");
			loadTransition(args);
			break;
		default:
			break;
		}
	}
	
	private void validateLength(String[] array, int length, String message) throws IllegalArgumentException
	{
		if (array.length < length)
			throw new IllegalArgumentException(message);
	}
	
	private Justification loadJustification(String value)
	{
		for(Justification just : Justification.values())
		{
			if(value.equalsIgnoreCase(just.name()))
				return just;
		}
		
		throw new IllegalArgumentException("Unknown justification " + value);
	}
	
	@SuppressWarnings( "deprecation" )
	private Material loadMaterial(String value)
	{
		Material type;
		type = Material.valueOf(value.toUpperCase());
		
		if(type == null)
			throw new IllegalArgumentException("Unknown material " + value);
		if(!type.isBlock() || type.hasGravity() || !type.isSolid())
			throw new IllegalArgumentException("Material cannot be an item, a block that falls under gravity, or not a full block");
		
		return type;
	}
	
	private void loadTransition(String[] args)
	{
		TransitionType type = null;
		for(TransitionType t : TransitionType.values())
		{
			if(args[2].equalsIgnoreCase(t.name()))
			{
				type = t;
				break;
			}
		}
		
		if(type == null)
			throw new IllegalArgumentException("Unknown transition type " + args[2]);
		
		int speed;
		try
		{
			speed = Integer.parseInt(args[3]);
			if(speed <= 0)
				throw new IllegalArgumentException("Minimum value for speed is 1");
		}
		catch(NumberFormatException e)
		{
			throw new IllegalArgumentException("Speed must be number 1 or greater");
		}
		
		String text = StringUtils.join(args, ' ', 4, args.length);
		text = text.replace("&v", "\n");
		
		mParams = new Object[] {type, speed, text};
	}
	
	public void execute(SignMakerPlugin plugin)
	{
		if (mSignId != null)
		{
			mSign = plugin.getSign(mSignId);
			if (mSign == null)
			{
				mScript.logError(String.format("Attempted to perform action %s on non existant sign %s", mType, mSignId));
				return;
			}
		}
		
		switch(mType)
		{
		case Clear:
			mSign.clear();
			break;
		case Draw:
			mSign.redraw();
			break;
		case Align:
			mSign.setJustification((Justification)mParams[0]);
			break;
		case Font:
			mSign.setFont((String)mParams[0]);
			break;
		case Material:
			mSign.setMaterial((Material) mParams[0]);
			break;
		case Text:
			mSign.setText((String)mParams[0]);
			break;
		case Change:
		{
			TransitionRunner runner = mSign.getCurrentTransition();
			if (runner != null)
				runner.stop();
			
			String oldText = mSign.getText();
			mSign.setText((String)mParams[2]);
			
			runner = new TransitionRunner((TransitionType)mParams[0], mSign, oldText, (Integer)mParams[1]);
			runner.start(plugin);
			break;
		}
		case Exec:
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (String)mParams[0]);
			break;
		}
	}
	
	public ActionType getType()
	{
		return mType;
	}
	
	public TextSign getSign()
	{
		return mSign;
	}
}
