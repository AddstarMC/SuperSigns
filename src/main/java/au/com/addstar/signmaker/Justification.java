package au.com.addstar.signmaker;

import java.util.ArrayList;
import java.util.List;

public enum Justification
{
	Left,
	Center,
	Right;
	
	public static final List<String> names;
	
	static
	{
		names = new ArrayList<String>();
		for(Justification value : values())
			names.add(value.name());
	}
}
