package au.com.addstar.signmaker;

import java.util.ArrayList;
import java.util.List;

import au.com.addstar.signmaker.transitions.Cover;
import au.com.addstar.signmaker.transitions.Reveal;
import au.com.addstar.signmaker.transitions.Transition;
import au.com.addstar.signmaker.transitions.VScroll;

public enum TransitionType
{
	None,
	ScrollDown,
	ScrollUp,
	CoverDown,
	CoverUp,
	RevealDown,
	RevealUp;
	
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
		case CoverDown:
			return new Cover(false);
		case CoverUp:
			return new Cover(true);
		case RevealDown:
			return new Reveal(false);
		case RevealUp:
			return new Reveal(true);
		default:
			return null;
		}
	}
}
