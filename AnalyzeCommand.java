import java.util.List;

public class AnalyzeCommand implements Command {
    @Override
    public void execute(String[] args, Container container) {
        if (args.length == 0) {
            System.out.println("Usage: analyze <id> | analyze - all | analyze <id> - details | analyze <id> - details - hints");
            return;
        }

        String option = args[0];
        if ("- all".equals(option)) {
            analyzeAll(container);
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
                // Debug: Print the quality score to ensure it's correct
                System.out.println("Debug: Quality Score is " + qualityScore);

                // Check the quality score and print the corresponding comment
                if (qualityScore <= 100 && qualityScore >= 90) {
                    System.out.println("Very Good");
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

                // Now handle -details and -hints options (strip any spaces between '-' and the option)
            boolean detailsShown = false;
            for (int i = 1; i < args.length; i++) {
                String optionPart = args[i].replaceAll("\\s+", ""); // Remove any spaces
                switch (optionPart) {
                    case "details":
                        showDetails(story, qualityScore);
                        detailsShown = true;
                        break;
                    case "hints":
                        if (detailsShown) {
                            showHints(story);
                        } else {
                            System.out.println("Please input -details first before -hints.");
                        }
                        break;
                    default:
                        System.out.println("Invalid option: " + args[i]);
                        System.out.println("Usage: analyze <id> - details | analyze <id> - details - hints");
                        return;
                }
            }
            } catch (NumberFormatException e) {
                System.out.println("Invalid ID. Use an integer.");
            }
        }
    }

    private void analyzeAll(Container container) {
        List<UserStory> stories = container.getUserStories();
        double totalQuality = 0;
        for (UserStory story : stories) {
            totalQuality += story.analyzeStoryQuality();
        }
        double averageQuality = totalQuality / stories.size();
        System.out.println("Average quality of all stories: " + String.format("%.2f", averageQuality) + "%");
    }

    private void showDetails(UserStory story, double qualityScore) {
        System.out.println("Quality details for story ID " + story.getId() + ":");
        if (qualityScore < 100) {
            System.out.println("- Missing components reduce quality.");
        } else {
            System.out.println("Everything OK.");
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
}
