import java.util.HashSet;
import java.util.Set;

public class UserSession {
    private int score = 0;
    private int totalAttempts = 0;
    private final Set<String> askedQuestions = new HashSet<>(); // Чтобы не повторяться
    private QuizQuestion currentQuestion;
    private int gamesPlayed = 0;
    private boolean isQuizActive = false;

    public void addPoint() { this.score++; }
    public void addAttempt() { this.totalAttempts++; }

    public void incrementGamesPlayed() { this.gamesPlayed++; }
    public int getGamesPlayed() { return gamesPlayed; }

    public void reset() {
        this.score = 0;
        this.totalAttempts = 0;
        this.askedQuestions.clear();
        this.isQuizActive = false;
    }

    public int getScore() { return score; }
    public int setScore(int score) {
        this.score = score;
        return score;
    }
    public int getTotalAttempts() { return totalAttempts; }
    public int setTotalAttempts(int totalAttempts) {
        this.totalAttempts = totalAttempts;
        return totalAttempts;
    }
    public Set<String> getAskedQuestions() { return askedQuestions; }
    public QuizQuestion getCurrentQuestion() { return currentQuestion; }
    public void setCurrentQuestion(QuizQuestion currentQuestion) { this.currentQuestion = currentQuestion; }
    public boolean isQuizActive() { return isQuizActive; }
    public void setQuizActive(boolean quizActive) { isQuizActive = quizActive; }
}