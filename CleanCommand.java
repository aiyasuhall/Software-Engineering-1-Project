public class CleanCommand implements Command {
    @Override
    public void execute(String[] args, Container container) {
        container.cleanUserStories();
    }
}
