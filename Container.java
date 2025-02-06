import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Container implements Serializable {
    private static final long serialVersionUID = 1L;
    private Vector<UserStory> userStories = new Vector<>();
    private static Container instance;
    private List<String> actors = new ArrayList<>();

    public void addUserStory(UserStory story) {
        userStories.addElement(story); // Using addElement
    }

    public boolean removeUserStory(int id) {
        return userStories.removeIf(story -> story.getId() == id);
    }

    public Vector<UserStory> getUserStories() {
        return userStories;
    }

       // Public method to provide access to the instance
    public static Container getInstance() {
        if (instance == null) {
            instance = new Container();
        }
        return instance;
    }

    public UserStory getUserStoryById(int id) {
        return userStories.stream()
            .filter(story -> story.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    public void printUserStories() {
        userStories.forEach(System.out::println);
    }

    public void setUserStories(Vector<UserStory> stories) {
        this.userStories = stories;
    }

    public void cleanUserStories() {
        userStories.clear();
        System.out.println("All user stories have been cleaned.");
    }
    
    public void dumpStories() {
        if (userStories.isEmpty()) {
            System.out.println("No user stories available.");
        } else {
            userStories.sort((s1, s2) -> Double.compare(s2.calculatePriority(), s1.calculatePriority()));
            userStories.forEach(System.out::println);
        }
    }

    public List<String> getActors() {
        return new ArrayList<>(actors);
    }
    
    public void addActor(String actor) {
        if (!actors.contains(actor)) {
            actors.add(actor);
        }
    }
    
    public boolean removeActor(String actor) {
        return actors.remove(actor);
    }
    
    public boolean isActorUsed(String actor) {
        for (UserStory story : userStories) {
            if (actor.equalsIgnoreCase(story.getActor())) {
                return true; // Actor is still in use
            }
        }
        return false; // Actor is not used in any story
    }
}
