public class HelpCommand implements Command {
    @Override
    public void execute(String[] args, Container container) {
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
