public class AddElementCommand implements Command {
    @Override
    public void execute(String[] args, Container container) {
        if (args.length < 2 || !"-actor".equals(args[0])) {
            System.out.println("Usage: addElement -actor <name>");
            return;
        }

        String actorName = args[1].trim();
        if (actorName.isEmpty()) {
            System.out.println("Actor name cannot be empty.");
            return;
        }

        container.addActor(actorName);
        System.out.println("Actor '" + actorName + "' registered.");

        // Register undo action for removing the actor
        main.commandHistory.push(new String[]{"addelement", actorName}); // Add to command history

        // Add undo action
        UndoCommand undoCommand = new UndoCommand();
        undoCommand.addUndoAction(() -> {
            container.removeActor(actorName);
            System.out.println("Actor '" + actorName + "' removed.");
        });
    }
}
