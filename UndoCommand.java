import java.util.Stack;

public class UndoCommand implements Command {
    private final Stack<Runnable> undoStack = new Stack<>();

    public void addUndoAction(Runnable action) {
        undoStack.push(action);
    }

    @Override
    public void execute(String[] args, Container container) {
        if (!undoStack.isEmpty()) {
            undoStack.pop().run();
            System.out.println("Undo successful.");
        } else {
            System.out.println("Nothing to undo.");
        }
    }

    private static void undoEnter(String data, Container container) {
        try {
            String[] parts = data.split("\"", 2);
            int id = Integer.parseInt(parts[0].trim());
    
            if (container.removeUserStory(id)) {
                System.out.println("Undo successful: Removed user story with ID " + id);
            } else {
                System.out.println("Undo failed: Story with ID " + id + " not found.");
            }
        } catch (Exception e) {
            System.out.println("Error during undo: " + e.getMessage());
        }
    }
}


