package au.com.addstar.signmaker.script;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;

import au.com.addstar.signmaker.SignMakerPlugin;

public class Script
{
	private SignMakerPlugin mPlugin;
	private List<ActionGroup> mGroups;
	
	private Iterator<ActionGroup> mIterator;
	private long mWaitEndTime;
	private ActionGroup mCurrent;
	
	public Script(SignMakerPlugin plugin)
	{
		mPlugin = plugin;
	}
	
	public void logError(String message)
	{
		mPlugin.getLogger().warning("[Script] Error: " + message);
	}
	
	public void load(File file) throws IllegalArgumentException
	{
		mGroups = Lists.newArrayList();
		
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			
			String line;
			int lineNumber = 0;
			ActionGroup group = new ActionGroup(0);
			while((line = reader.readLine()) != null)
			{
				++lineNumber;
				line = line.trim();
				
				if (line.isEmpty() || line.startsWith("#"))
					continue;
				
				if (line.startsWith("@"))
				{
					try
					{
						long delay = Long.parseLong(line.substring(1));
						if (delay < 0)
							throw new IllegalArgumentException("Error (line " + lineNumber + "): Delay must be a positive integer");
						
						if (!group.isEmpty())
							mGroups.add(group);
						group = new ActionGroup(delay);
					}
					catch(NumberFormatException e)
					{
						throw new IllegalArgumentException("Error (line " + lineNumber + "): Delay must be a positive integer");
					}
				}
				else
				{
					// Allow carrying over to new line by ending with a \
					String full;
					if (line.endsWith("\\"))
					{
						full = line.substring(0, line.length()-1);
						while((line = reader.readLine()) != null)
						{
							++lineNumber;
							line = line.trim();
							if (line.endsWith("\\"))
								full += "\n" + line.substring(0, line.length()-1);
							else
							{
								full += "\n" + line;
								break;
							}
						}
					}
					else
						full = line;
					
					try
					{
						Action action = new Action(this);
						action.load(full.split(" "));
						group.addAction(action);
					}
					catch(IllegalArgumentException e)
					{
						throw new IllegalArgumentException("Error (line " + lineNumber + "): " + e.getMessage());
					}
				}
			}
			
			if (!group.isEmpty())
				mGroups.add(group);
		}
		catch ( IOException e )
		{
			e.printStackTrace();
			throw new IllegalArgumentException("An internal error occured while loading the script");
		}
		finally
		{
			Closeables.closeQuietly(reader);
		}
	}
	
	public void start()
	{
		if (mGroups.isEmpty())
			return;
		
		mIterator = mGroups.iterator();
		mCurrent = mIterator.next();
		
		mWaitEndTime = System.currentTimeMillis() + mCurrent.getDelay();
		
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				if (System.currentTimeMillis() < mWaitEndTime)
					return;
				
				mCurrent.execute(mPlugin);
				
				if (mIterator.hasNext())
				{
					mCurrent = mIterator.next();
					mWaitEndTime = System.currentTimeMillis() + mCurrent.getDelay();
				}
				else
					cancel();
			}
		}.runTaskTimer(mPlugin, 1, 1);
	}
}
