import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class StoreCommand implements Command {
    @Override
    public void execute(String[] args, Container container) {
        try (FileOutputStream fileOut = new FileOutputStream("user_stories.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

            out.writeObject(container);
            System.out.println("User stories saved successfully!");

        } catch (Exception e) {
            System.out.println("Error saving user stories: " + e.getMessage());
        }
    }
}
