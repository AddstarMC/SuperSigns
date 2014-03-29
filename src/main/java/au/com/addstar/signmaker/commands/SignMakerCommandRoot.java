package au.com.addstar.signmaker.commands;

public class SignMakerCommandRoot extends RootCommandDispatcher
{
	public SignMakerCommandRoot()
	{
		super("Allows you to create and remove text signs;");
		registerCommand(new WriteCommand());
	}
}
