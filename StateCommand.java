import java.util.Arrays;

public class StateCommand implements Command {
    @Override
    public void execute(String[] args, Container container) {
        // Debugging: Print received arguments to understand what is passed
        System.out.println("DEBUG: Received arguments: " + Arrays.toString(args));
        System.out.println("DEBUG: args.length = " + args.length);

        // Ensure we have at least 3 arguments: ["state", "<id>", "<state>"]
        if (args.length < 3) {
            System.out.println("Usage: state <id> <state>");
            return;
        }

        try {
            // Print the value of the id and the state from args[1] and args[2]
            System.out.println("DEBUG: ID: " + args[1] + ", State: " + args[2]);

            int id = Integer.parseInt(args[1]); // Parse user story ID
            String newState = args[2].trim(); // Get the state and remove extra spaces

            // Debugging: Check if the state contains any quotes or unexpected characters
            System.out.println("DEBUG: Trimmed newState: " + newState);

            // Remove quotes from input state if they are present
            if (newState.startsWith("\"") && newState.endsWith("\"")) {
                newState = newState.substring(1, newState.length() - 1); // Remove quotes
            }

            // Print the final newState after trimming
            System.out.println("DEBUG: Final newState after trimming: " + newState);

            // Fetch the user story
            UserStory story = container.getUserStoryById(id);
            if (story == null) {
                System.out.println("User story with ID " + id + " not found.");
                return;
            }

            // Validate and update the state
            if (newState.equals("todo") || newState.equals("progress") || newState.equals("done")) {
                story.setState(newState);
                System.out.println("User story ID " + id + " state changed to \"" + newState + "\".");
            } else {
                System.out.println("Invalid state. Use 'todo', 'progress', or 'done'.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please use an integer.");
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }
}