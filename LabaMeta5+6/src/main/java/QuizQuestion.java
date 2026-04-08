public class QuizQuestion {
    private final Song song;
    private final QuestionType type;

    public QuizQuestion(Song song, QuestionType type) {
        this.song = song;
        this.type = type;
    }

    public boolean isCorrect(String userAnswer) {
        String correct = (type == QuestionType.GUESS_ARTIST) ? song.getArtist() : song.getTitle();
        return userAnswer.trim().equalsIgnoreCase(correct);
    }

    public String getCorrectAnswer() {
        return (type == QuestionType.GUESS_ARTIST) ? song.getArtist() : song.getTitle();
    }

    public Song getSong() { return song; }
    public QuestionType getType() { return type; }
}