package au.com.addstar.signmaker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.WeakHashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.addstar.signmaker.commands.SignMakerCommandRoot;

public class SignMakerPlugin extends JavaPlugin
{
	private HashMap<String, TextSign> mSigns = new HashMap<String, TextSign>();
	private File mSignFolder;
	
	public static WeakHashMap<Player, TextSign> lastSign = new WeakHashMap<Player, TextSign>();
	
	@Override
	public void onEnable()
	{
		mFontFolder = new File(getDataFolder(), "fonts");
		mFontFolder.mkdirs();
		
		mSignFolder = new File(getDataFolder(), "signs");
		mSignFolder.mkdirs();
		
		TextWriter.reloadFonts();
		reloadSigns();
		
		SignMakerCommandRoot cmd = new SignMakerCommandRoot();
		cmd.registerAs(getCommand("signmaker"));
	}
	
	public void reloadSigns()
	{
		mSigns.clear();
		
		for(File file : mSignFolder.listFiles())
		{
			try
			{
				mSigns.put(file.getName(), TextSign.load(file));
			}
			catch(IOException e)
			{
				getLogger().warning("Could not load sign " + file.getName() + " (" + file.getPath() + ")");
				e.printStackTrace();
			}
			catch(InvalidConfigurationException e)
			{
				getLogger().warning("Could not load sign " + file.getName() + " (" + file.getPath() + ")");
				getLogger().warning(e.getMessage());
			}
		}
	}
	
	private static File mFontFolder;
	public static File getFontFolder()
	{
		return mFontFolder;
	}
}
