public class EnterCommand implements Command {
    @Override
    public void execute(String[] args, Container container) {
        try {
            // Ensure the argument length matches exactly
            if (args.length != 7) {
                System.out.println("Usage: enter <id> <description> <effort> <value> <risk> <penalty>");
                return;
            }

            // Parse input arguments
            int id = Integer.parseInt(args[1]); // First argument: ID
            String description = args[2];      // Second argument: Description
            double effort = Double.parseDouble(args[3]); // Third argument: Effort
            double value = Double.parseDouble(args[4]);  // Fourth argument: Value
            double risk = Double.parseDouble(args[5]);   // Fifth argument: Risk
            double penalty = Double.parseDouble(args[6]); // Sixth argument: Penalty

            // Check if ID already exists
            if (container.getUserStoryById(id) != null) {
                System.out.println("Error: A user story with ID " + id + " already exists. Please use a unique ID.");
                return;
            }
            
            // Validate input values
            if (effort <= 0) {
                throw new IllegalArgumentException("Effort must be positive");
            }
            if (value < 1 || value > 5) {
                throw new IllegalArgumentException("Value must be from 1 to 5");
            }
            if (risk < 0 || risk > 5) {
                throw new IllegalArgumentException("Risk must be from 0 to 5");
            }
            if (penalty < 1 || penalty > 5) {
                throw new IllegalArgumentException("Penalty must be from 1 to 5");
            }

            // Create the UserStory
            UserStory userStory = new UserStory(id, description, effort, value, risk, penalty);

            // Add the user story to the container
            container.addUserStory(userStory);
            System.out.println("User story added: " + userStory);

        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please check your input.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace(); // For debugging unexpected issues
        }
    }
}


