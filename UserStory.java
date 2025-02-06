import java.io.Serializable;

class UserStory implements Serializable {
    private static final long serialVersionUID = 1L;  // Add this for serialization
    private int id;
    private String description;
    private String actor;
    private double effort;
    private double value;
    private double risk;
    private double penalty;
    private String goal;
    private String benefit;
    private String state;

    public UserStory(int id, String description, double effort, double value, double risk, double penalty) {
        this.id = id;
        this.description = description;
        this.actor = "";
        this.effort = effort;
        this.value = value;
        this.risk = risk;
        this.penalty = penalty;
        this.goal = "";
        this.benefit = "";
        this.state = "todo";
    }

    public double calculatePriority() {
        return (value + penalty) / (effort + risk); // effort and risk is lower, the higher quality score is
    }

    public int getId() {
        return id;
    }

    public String getActor(){
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
    
    public String getGoal() {
        return goal;
    }

    public String getBenefit() {
        return benefit;
    }

    // Analyze the quality of the user story
    public double analyzeStoryQuality() {
        double clarityScore = analyzeClarity();
        double completenessScore = analyzeCompleteness();
        double feasibilityScore = analyzeFeasibility();
        double valueScore = analyzeValue();
        double priorityScore = normalizePriority(calculatePriority()); // Normalize priority to a score out of 100
    
        // Calculate the overall quality as a weighted average
        double overallScore = (clarityScore * 0.2 + completenessScore * 0.2 +
                               feasibilityScore * 0.2 + valueScore * 0.2 +
                               priorityScore * 0.2);
        return overallScore;
    }
    
    // Normalize the priority score to a scale of 0 to 100
    private double normalizePriority(double priority) {
        // Assuming priorities usually range from 0 to 5; adjust as needed
        double maxPriority = 5.0; 
        double minPriority = 0.0;
        return ((priority - minPriority) / (maxPriority - minPriority)) * 100;
    }
    
    private double analyzeClarity() {
        // Example: Score based on length and readability
        if (description == null || description.isEmpty()) return 0;
        int wordCount = description.split("\\s+").length;
        return (wordCount >= 5 && wordCount <= 50) ? 100 : 50;
    }

    private double analyzeCompleteness() {
        // Check if essential attributes are filled and valid
        boolean validEffort = effort > 0;
        boolean validValue = value > 0;
        boolean validRisk = risk >= 0 && risk <= 1;
        boolean validPenalty = penalty > 0;

        return (validEffort && validValue && validRisk && validPenalty) ? 100 : 50;
    }

    private double analyzeFeasibility() {
        // Effort should be within a reasonable range
        return (effort > 0 && effort <= 100) ? 100 : 50;
    }

    private double analyzeValue() {
        // High value and low risk are preferred
        if (value > 0) {
            double normalizedRisk = (1 - risk);  // Lower risk increases score
            return (value * normalizedRisk) > 50 ? 100 : 75;
        }
        return 50;
    }
    
    public void setGoal(String goal) {
        this.goal = goal;
    }

    public void setBenefit(String benefit) {
        this.benefit = benefit;
    }

    // analyzing the description as a single text
    public void parseDescription() {
        if (description == null || description.isEmpty()) {
            actor = "";
            goal = "";
            benefit = "";
            return;
        }
    
        String normalizedDescription = description.toLowerCase();
    
        // Extract actor
        if (normalizedDescription.contains("as a")) {
            int actorStart = normalizedDescription.indexOf("as a") + 5;
            int actorEnd = normalizedDescription.indexOf(",", actorStart);
            if (actorEnd == -1) actorEnd = description.length();
            actor = description.substring(actorStart, actorEnd).trim();
        }
    
        // Extract goal
        if (normalizedDescription.contains("i want to")) {
            int goalStart = normalizedDescription.indexOf("i want to") + 9;
            int goalEnd = normalizedDescription.length(); // Default to end of description
            // Look for benefit indicators
            if (normalizedDescription.contains("so that")) {
                goalEnd = normalizedDescription.indexOf("so that", goalStart);
            } else if (normalizedDescription.contains("in order to")) {
                goalEnd = normalizedDescription.indexOf("in order to", goalStart);
            } else if (normalizedDescription.contains("so i can")) {
                goalEnd = normalizedDescription.indexOf("so i can", goalStart);
            }
            goal = description.substring(goalStart, goalEnd).trim();
        }
    
        // Extract benefit
        if (normalizedDescription.contains("so that")) {
            int benefitStart = normalizedDescription.indexOf("so that") + 8;
            benefit = description.substring(benefitStart).trim();
        } else if (normalizedDescription.contains("in order to")) {
            int benefitStart = normalizedDescription.indexOf("in order to") + 12;
            benefit = description.substring(benefitStart).trim();
        } else if (normalizedDescription.contains("so i can")) {
            int benefitStart = normalizedDescription.indexOf("so i can") + 9;
            benefit = description.substring(benefitStart).trim();
        }
    
        // Validate goal and benefit
        if (goal == null || goal.isEmpty() || 
            goal.equalsIgnoreCase("use the website") || 
            goal.equalsIgnoreCase("do something") || 
            goal.equalsIgnoreCase("interact with the system") ||
            goal.length() < 10) {
            goal = "";
        }
    
        if (benefit == null || benefit.isEmpty() || 
            benefit.equalsIgnoreCase("for convenience") || 
            benefit.equalsIgnoreCase("to make it easier") || 
            benefit.equalsIgnoreCase("to be more efficient") ||
            benefit.length() < 10) {
            benefit = "";
        }
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        if (state.equals("todo") || state.equals("progress") || state.equals("done")) {
            this.state = state;
        } else {
            throw new IllegalArgumentException("Invalid state: " + state);
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + 
            ", Description: " + description + 
            ", Priority: " + String.format("%.2f", calculatePriority()) + 
            ", State: " + state;
    }
}
