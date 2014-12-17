package au.com.addstar.signmaker.commands;

public class SignMakerCommandRoot extends RootCommandDispatcher
{
	public SignMakerCommandRoot()
	{
		super("Allows you to create and remove text signs;");
		registerCommand(new WriteCommand());
		registerCommand(new UndoCommand());
		
		registerCommand(new CreateCommand());
		registerCommand(new RedrawCommand());
		registerCommand(new RemoveCommand());
		registerCommand(new SetFontCommand());
		registerCommand(new SetJustificationCommand());
		registerCommand(new SetMaterialCommand());
		registerCommand(new SetTextCommand());
		registerCommand(new ListFontsCommand());
		registerCommand(new ChangeTextCommand());
		registerCommand(new CancelCommand());
		registerCommand(new PlayCommand());
	}
}
