package au.com.addstar.signmaker;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import au.com.addstar.signmaker.transitions.Transition;

public class TransitionRunner implements Runnable
{
	private Transition mEffect;
	private TextSign mSign;

	private BukkitTask mTask;
	
	private int mTime;
	
	public TransitionRunner(TransitionType effect, TextSign sign, String lastText, int time)
	{
		Validate.isTrue(sign.getCurrentTransition() == null, "Sign already has a running transition");
		CharSet font = TextWriter.getFont(sign.getFont());
		Validate.notNull(font, "Cannot find font");
		
		mEffect = effect.newTransition();
		Validate.notNull(mEffect, "Cannot use Transition runner on " + effect);
		mEffect.setOriginal(TextWriter.makeText(lastText, sign.getOrigin(), sign.getFacing(), sign.getJustification(), font, sign.getMaterial()));
		mEffect.setNew(TextWriter.makeText(sign.getText(), sign.getOrigin(), sign.getFacing(), sign.getJustification(), font, sign.getMaterial()));
		mEffect.setMaterial(sign.getMaterial());
		
		mSign = sign;
		mTime = time;
	}
	
	public void start(Plugin plugin)
	{
		Validate.isTrue(mSign.getCurrentTransition() == null, "Sign already has a running transition");
		mTask = Bukkit.getScheduler().runTaskTimer(plugin, this, mTime, mTime);
		mSign.setCurrentTransition(this);
	}
	
	public void stop()
	{
		mTask.cancel();
		mTask = null;
		mSign.setCurrentTransition(null);
	}
	
	@Override
	public void run()
	{
		if(!mEffect.isDone())
			mEffect.doStep();
		else
		{
			mTask.cancel();
			mTask = null;
			mSign.setCurrentTransition(null);
		}
	}

}
