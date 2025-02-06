public class DumpCommand implements Command {
    @Override
    public void execute(String[] args, Container container) {
        if (args.length == 3 && "--state".equals(args[1])) {
            String state = args[2].toLowerCase();
            dumpStoriesByState(container, state);
        } else {
            dumpAllStories(container);
        }
    }

    private void dumpAllStories(Container container) {
        System.out.println("All User Stories:");
        container.printUserStories();
    }

    private void dumpStoriesByState(Container container, String state) {
        container.getUserStories().stream()
            .filter(story -> state.equals(story.getState()))
            .forEach(story -> {
                System.out.println("Story ID: " + story.getId() + ", State: " + story.getState());
            });
    }    
}
