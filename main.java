import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class main {
    static Container container = new Container();
    static Stack<String[]> commandHistory = new Stack<>();
    static final String FILE_NAME = "userStories.dat";  // File to store user stories

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the PrioTool CLI.");
        System.out.println("Type 'help' for a list of commands.");

        CommandExecutor executor = new CommandExecutor(container);

        // Load stored user stories at the start
        loadStories();

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();
            String[] tokens = input.split(" ", 2);

            if (tokens.length < 1 || tokens[0].isEmpty()) {
                System.out.println("Invalid input. Please try again.");
                continue;
            }

            String command = tokens[0].toLowerCase();

            switch (command) {
                case "help":
                    printHelp();
                    break;
                case "enter":
                    if (tokens.length < 2) {
                        System.out.println("Invalid input. Usage: enter <id> \"<description>\" <effort> <value> <risk> <penalty>");
                    } else {
                        enter(tokens[1]);
                    }
                    break;
                case "dump":
                    if (tokens.length == 3 && tokens[1].equalsIgnoreCase("--state")) {
                        dumpByState(tokens[2]);
                    } else {
                        container.dumpStories();
                    }
                    break;
                case "state":
                    String[] stateTokens = input.split("\\s+"); // Fix token splitting
                    if (stateTokens.length != 3) {
                        System.out.println("Usage: state <id> <state>");
                        break;
                    }
                    try {
                        int id = Integer.parseInt(stateTokens[1]);  // Correctly extract ID
                        String newState = stateTokens[2].toLowerCase(); // Extract state

                        UserStory story = container.getUserStoryById(id);
                        if (story == null) {
                            System.out.println("User story with ID " + id + " not found.");
                            break;
                        }

                        if (newState.equals("todo") || newState.equals("progress") || newState.equals("done")) {
                            story.setState(newState);
                            System.out.println("The user story with ID " + id + " was set to state \"" + newState + "\".");
                        } else {
                            System.out.println("Invalid state. Use 'todo', 'progress', or 'done'.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid ID format. Please use an integer.");
                    }
                    break;
                case "load":
                    loadStories();  // Load stories before exitting if turn on tool, it will display
                    break;
                case "store":
                    storeStories();
                    break;
                case "analyze":
                    analyzeStories(input);
                    break;
                case "clean":
                    cleanUserStories();
                    break;
                case "undo":
                    undo();
                    break;
                case "addelement":
                    if (tokens.length<2 || !tokens[1].startsWith("-actor ")){
                        System.out.println("Invalid input. Usage: addElement -actor <name>");
                    }
                    else {
                        String[] addArgs = tokens[1].split(" ", 2);
                        AddElementCommand addElementCommand = new AddElementCommand();
                        addElementCommand.execute(addArgs, container);
                    }
                    break;
                case "actors":
                    showActors();
                    break;
                case "exit":
                    System.out.println("Exiting the PrioTool. Goodbye!");
                    storeStories();  // Save before exiting
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid command. Type 'help' for available commands.");
            }
        }
    }

    public static void enter(String input) {
        try {
            String[] parts = input.split("\"", 3);
    
            if (parts.length < 3 || parts[1].isEmpty()) {
                throw new IllegalArgumentException("Invalid input format. Usage: enter <id> \"<description>\" <effort> <value> <risk> <penalty>");
            }
    
            int id = Integer.parseInt(parts[0].trim());
            String description = parts[1].trim();
            String[] values = parts[2].trim().split("\\s+");
    
            if (values.length != 4) {
                throw new IllegalArgumentException("Expected effort, value, risk, and penalty fields.");
            }
    
            double effort = Double.parseDouble(values[0].trim());
            double value = Double.parseDouble(values[1].trim());
            double risk = Double.parseDouble(values[2].trim());
            double penalty = Double.parseDouble(values[3].trim());
    
            // Check if ID is unique
            if (container.getUserStoryById(id) != null) {
                System.out.println("Error: A user story with ID " + id + " already exists. Please use a unique ID.");
                return;
            }

            //Validation
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

            UserStory story = new UserStory(id, description, effort, value, risk, penalty);
            story.parseDescription(); // Automatically populate actor, goal, and benefit

            // Add actor from the story to the Container
            if (story.getActor() != null && !story.getActor().isEmpty()) {
                container.addActor(story.getActor()); // Register actor in the Container
            }
    
            container.getUserStories().addElement(story);
            System.out.println("User story added: " + story);
    
            commandHistory.push(new String[]{"enter", input});
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public static void dumpByState(String state) {
        System.out.println("User stories with state \"" + state + "\":");
        container.getUserStories().stream()
                .filter(story -> story.getState().equalsIgnoreCase(state))
                .forEach(System.out::println);
    }

    public static void loadStories() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            List<UserStory> loadedStories = (List<UserStory>) in.readObject();
            loadedStories.forEach(story -> {
                container.getUserStories().addElement(story);  // Using addElement
                System.out.println("Loaded and added story using addElement: " + story);  // Confirmation message
            });
            System.out.println("User stories loaded successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("No previous data found. Starting fresh.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading user stories: " + e.getMessage());
        }
    }

    public static void storeStories() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            out.writeObject(container.getUserStories());
            System.out.println("User stories stored successfully.");
        } catch (IOException e) {
            System.out.println("Error saving user stories: " + e.getMessage());
        }
    }

    public static void analyzeStories(String input) {
        String[] tokens = input.split("\\s+");
    
        if (tokens.length < 2) {
            System.out.println("Usage: analyze <id> | analyze - all | analyze <id> - details | analyze <id> - details - hints");
            return;
        }
    
        String option = tokens[1].toLowerCase();
    
        if (option.equals("-all")) {
            // Explicitly reject "-all"
            System.out.println("Invalid option: '-all' is not a valid parameter.");
            return;
        }
    
        if (option.equals("-") && tokens.length > 2 && tokens[2].toLowerCase().equals("all")) {
            analyzeAll();
        } else {
            try {
                int id = Integer.parseInt(option);
                UserStory story = container.getUserStoryById(id);
                if (story == null) {
                    System.out.println("User story with ID " + id + " not found.");
                    return;
                }
    
                double qualityScore = story.analyzeStoryQuality();
                System.out.println("User story ID " + id + " quality: " + String.format("%.2f", qualityScore) + "%");

                boolean detailsShown = false;
            for (int i = 2; i < tokens.length; i++) {
                String currentOption = tokens[i].trim().toLowerCase();

                if ("-".equals(currentOption) && i + 1 < tokens.length) {
                    i++; // Move to the next token
                    currentOption = tokens[i].toLowerCase();
                }

                switch (currentOption) {
                    case "details":
                        System.out.println("Running showDetails...");
                        showDetails(story, qualityScore);
                        detailsShown = true;
                        break;
                    case "hints":
                        if (detailsShown) {
                            System.out.println("Running showHints...");
                            showHints(story);
                        } else {
                            System.out.println("Please input 'details' first before 'hints'.");
                        }
                        break;
                    default:
                        System.out.println("Invalid option: " + currentOption);
                        System.out.println("Usage: analyze <id> - details | analyze <id> - details - hints");
                        return;
                }
            }
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID. Please provide a valid integer.");
            } catch (Exception e) {
                System.out.println("An error occurred while analyzing the user story: " + e.getMessage());
            }
        }
    }
    
    private static void analyzeAll() {
        if (container.getUserStories().isEmpty()) {
            System.out.println("No user stories available to analyze.");
            return;
        }
    
        double totalQuality = 0;
        for (UserStory story : container.getUserStories()) {
            double qualityScore = story.analyzeStoryQuality();
            System.out.println("User story ID " + story.getId() + " quality: " + String.format("%.2f", qualityScore) + "%");
            totalQuality += qualityScore;
        }
        double averageQuality = totalQuality / container.getUserStories().size();
        System.out.println("Average quality of all user stories: " + String.format("%.2f", averageQuality) + "%");
    }
    
    private static void showDetails(UserStory story, double qualityScore) {
        System.out.println("Quality details for story ID " + story.getId() + ":");
         // Check the quality score and print the corresponding comment
         if (qualityScore <= 100 && qualityScore >= 90) {
            System.out.println("Very Good");
            //System.out.println("Details: Everything is ok. Good jobs!");
            } 
        else if (qualityScore < 90 && qualityScore >= 80) {
            System.out.println("Good"); 
            } 
        else if (qualityScore < 80 && qualityScore >= 70) {
            System.out.println("Satisfactory");
            }
        else if (qualityScore < 70){
            System.out.println("Need improvement");
        }        
    }
    
    private static void showHints(UserStory story) {
        System.out.println("Hints for improving story ID " + story.getId() + ":");
    
        String actor = story.getActor() != null ? story.getActor().trim() : "";
        String goal = story.getGoal() != null ? story.getGoal().trim() : "";
        String benefit = story.getBenefit() != null ? story.getBenefit().trim() : "";
    
        // Define common vague phrases
        boolean vagueActor = actor.isEmpty() || actor.equalsIgnoreCase("user") || actor.length() < 3;
        boolean vagueGoal = goal.isEmpty() || goal.length() < 10 || 
                            goal.equalsIgnoreCase("use the website") || 
                            goal.equalsIgnoreCase("do something") || 
                            goal.equalsIgnoreCase("interact with the system");
        boolean vagueBenefit = benefit.isEmpty() || benefit.length() < 10 || 
                               benefit.equalsIgnoreCase("for convenience") || 
                               benefit.equalsIgnoreCase("to make it easier") || 
                               benefit.equalsIgnoreCase("to be more efficient");
    
        boolean hintsDisplayed = false;
    
        // Provide hints based on vague or missing components
        if (vagueActor) {
            System.out.println("- Add a specific actor to the user story (e.g., 'As a customer, ...').");
            hintsDisplayed = true;
        }
        if (vagueGoal) {
            System.out.println("- Clearly define the goal (e.g., 'I want to ...').");
            hintsDisplayed = true;
        }
        if (vagueBenefit) {
            System.out.println("- Specify the benefit to make the story impactful (e.g., 'so that I can...').");
            hintsDisplayed = true;
        }
    
        // If no hints are needed, confirm the story is well-defined
        if (!hintsDisplayed) {
            System.out.println("- The user story is complete with well-defined components.");
        }
    }
    
    private static void showActors() {
        List<String> actors = container.getActors();
        if (actors.isEmpty()) {
            System.out.println("No actors registered.");
        } else {
            System.out.println("Registered actors:");
            actors.forEach(System.out::println);
        }
    }
    
    public static void undo() {
        if (commandHistory.isEmpty()) {
            System.out.println("Nothing to undo.");
            return;
        }

        String[] lastCommand = commandHistory.pop();
        String command = lastCommand[0];
        String data = lastCommand[1];

        switch (command) {
            case "enter":
                undoEnter(data);
                break;
            case "addelement":
                undoAddElement(data);
                break;    
            default:
                System.out.println("Undo not supported for the last command.");
        }
    }
    
    // Undo logic for addelement
    private static void undoAddElement(String actorName) {
        if (container.removeActor(actorName)) {
        System.out.println("Undo successful: Removed actor '" + actorName + "'");
       } else {
        System.out.println("Undo failed: Actor '" + actorName + "' not found.");
       }
    }

    private static void undoEnter(String data) {
        try {
            String[] parts = data.split("\"", 2);
            int id = Integer.parseInt(parts[0].trim());
    
            UserStory removedStory = container.getUserStoryById(id);
            if (removedStory != null) {
                // Remove the user story
                container.removeUserStory(id);
                System.out.println("Undo successful: Removed user story with ID " + id);
    
                // Check if the actor is still used in other stories
                String actor = removedStory.getActor();
                if (actor != null && !container.isActorUsed(actor)) {
                    container.removeActor(actor);
                    System.out.println("Actor '" + actor + "' was removed as it is no longer used.");
                }
            } else {
                System.out.println("Undo failed: Story with ID " + id + " not found.");
            }
        } catch (Exception e) {
            System.out.println("Error during undo: " + e.getMessage());
        }
    }
    
    public static void cleanUserStories(){
        container.cleanUserStories();
    }

    public static void printHelp() {
        System.out.println("Available commands:");
        System.out.println("enter <id> \"<description>\" <effort> <value> <risk> <penalty> - Enter a new user story");
        System.out.println("state <id> <state> - Set the state of a user story (todo, progress, done)");
        System.out.println("dump --state <state> - Display user stories filtered by state (todo, progress, done)");
        System.out.println("dump - Display all user stories");
        System.out.println("load - Load user stories from the file");
        System.out.println("store - Store user stories to the file");
        System.out.println("analyze <id> | analyze - all | analyze <id> - details | analyze <id> - details - hints - Analyze the quality of all user stories");
        System.out.println("undo - Undo the last action");
        System.out.println("clean - Clean old data");
        System.out.println("addelement -actor \"<name>\" - Register a new actor");
        System.out.println("actors - Show all registered actors");
        System.out.println("exit - Exit the program");
    }   
}
