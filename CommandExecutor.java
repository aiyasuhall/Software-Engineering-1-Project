import java.util.HashMap;
import java.util.Map;

public class CommandExecutor {
    private final Map<String, Command> commands = new HashMap<>();
    private final UndoCommand undoCommand = new UndoCommand();

    public CommandExecutor(Container container) {
        registerCommands(container);
    }

    private void registerCommands(Container var1) {
        this.commands.put("enter", new EnterCommand());
        this.commands.put("store", new StoreCommand());
        this.commands.put("load", new LoadCommand());
        this.commands.put("dump", new DumpCommand());
        this.commands.put("analyze", new AnalyzeCommand());
        this.commands.put("undo", new UndoCommand());
        this.commands.put("clean", new CleanCommand());
        this.commands.put("addelement", new AddElementCommand());
        this.commands.put("state", new StateCommand());
        this.commands.put("help", new HelpCommand());
     }

    public void execute(String input) {
        String[] tokens = input.split(" ", 2);
        String command = tokens[0].toLowerCase();

        if (commands.containsKey(command)) {
            commands.get(command).execute(tokens, Container.getInstance());
        } else {
            System.out.println("Invalid command. Type 'help' for available commands.");
        }
    }

    public void addUndoAction(Runnable action) {
        undoCommand.addUndoAction(action);
    }
}
