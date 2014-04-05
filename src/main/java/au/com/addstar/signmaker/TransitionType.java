package au.com.addstar.signmaker;

import java.util.ArrayList;
import java.util.List;

import au.com.addstar.signmaker.transitions.Transition;
import au.com.addstar.signmaker.transitions.VScroll;

public enum TransitionType
{
	None,
	ScrollDown,
	ScrollUp;
	
	public static final List<String> names;
	
	static
	{
		names = new ArrayList<String>();
		for(TransitionType value : values())
			names.add(value.name());
	}
	
	public Transition newTransition()
	{
		switch(this)
		{
		case ScrollDown:
			return new VScroll(false);
		case ScrollUp:
			return new VScroll(true);
		default:
			return null;
		}
	}
}
