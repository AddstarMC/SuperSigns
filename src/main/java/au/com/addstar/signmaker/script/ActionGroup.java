package au.com.addstar.signmaker.script;

import java.util.List;
import java.util.Set;

import au.com.addstar.signmaker.SignMakerPlugin;
import au.com.addstar.signmaker.TextSign;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ActionGroup
{
	private long mDelay;
	private List<Action> mActions;
	
	public ActionGroup(long delay)
	{
		mDelay = delay;
		
		mActions = Lists.newArrayList();
	}
	
	public void addAction(Action action)
	{
		mActions.add(action);
	}
	
	public boolean isEmpty()
	{
		return mActions.isEmpty();
	}
	
	public void execute(SignMakerPlugin plugin)
	{
		Set<TextSign> signs = Sets.newHashSet();
		
		for (Action action : mActions)
		{
			action.execute(plugin);
			if (action.getSign() != null)
			{
				if (action.getType() == ActionType.Draw || action.getType() == ActionType.Change)
					signs.remove(action.getSign());
				else
					signs.add(action.getSign());
			}
		}
		
		for (TextSign sign : signs)
			sign.redraw();
	}
	
	public long getDelay()
	{
		return mDelay;
	}
}
