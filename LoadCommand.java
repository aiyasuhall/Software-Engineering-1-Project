import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class LoadCommand implements Command {
    @Override
    public void execute(String[] args, Container container) {
        try (FileInputStream fileIn = new FileInputStream("user_stories.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)) {

            Container loadedContainer = (Container) in.readObject();
            container.getUserStories().clear();
            container.getUserStories().addAll(loadedContainer.getUserStories());
            System.out.println("User stories loaded successfully!");

        } catch (Exception e) {
            System.out.println("Error loading user stories: " + e.getMessage());
        }
    }
}
