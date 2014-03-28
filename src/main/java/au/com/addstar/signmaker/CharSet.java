package au.com.addstar.signmaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;

public class CharSet
{
	private HashMap<Character, CharDef> mChars = new HashMap<Character, CharDef>();
	private String mName;
	private int mHeight;
	
	public CharSet(String name, int height)
	{
		mName = name;
		mHeight = height;
	}
	
	public String getName()
	{
		return mName;
	}
	
	public int getHeight()
	{
		return mHeight;
	}
	
	public CharDef getChar(char c)
	{
		return mChars.get(c);
	}
	
	
	public static CharSet load(File file) throws IOException
	{
		FileInputStream stream = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
		
		CharSet set;
		
		try
		{
			String line = reader.readLine();
			String[] def = line.split("\\|");
			
			if(def.length != 2)
				throw new IllegalArgumentException("Invalid header. Expected <name>|<height>. Got " + line);
			
			int height;
			
			try
			{
				height = Integer.parseInt(def[1]);
				if(height <= 0)
					throw new IllegalArgumentException("Height too small");
			}
			catch(NumberFormatException e)
			{
				throw new IllegalArgumentException("Invalid header. Expected <name>|<height>. Got " + line);
			}
			
			set = new CharSet(def[0], height);
			
			int lineNo = 1;
			
			while(reader.ready())
			{
				++lineNo;
				line = reader.readLine();
				if(line.trim().isEmpty())
					continue;
				
				def = line.split("\\|");
				
				if(def.length != 2)
					throw new IllegalArgumentException("Invalid char def at line " + lineNo + ". Expected <char>|<width>. Got " + line);
				
				int width;
				
				try
				{
					width = Integer.parseInt(def[1]);
					if(width <= 0)
						throw new IllegalArgumentException("Width too small at line " + lineNo);
				}
				catch(NumberFormatException e)
				{
					throw new IllegalArgumentException("Invalid char def at line " + lineNo + ". Expected <char>|<width>. Got " + line);
				}
				
				CharDef charDef = new CharDef(width, height);
				
				for(int y = 0; y < height; ++y)
				{
					line = reader.readLine();
					
					for(int x = 0; x < width; ++x)
					{
						char c = ' ';
						if(x < line.length())
							c = line.charAt(x);
						
						BlockType type = BlockType.from(c);
						if(type == null)
							throw new IllegalArgumentException("Invalid block type '" + c + "' on line " + lineNo);
						
						charDef.setType(type, x, height - y - 1);
					}
				}
				
				set.mChars.put(def[0].charAt(0), charDef);
			}
		}
		finally
		{
			stream.close();
		}
		
		return set;
	}
}
