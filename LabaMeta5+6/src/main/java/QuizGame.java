import java.util.*;
import java.io.*;
import java.util.Random;
import java.nio.charset.StandardCharsets;

public class QuizGame {
    private final Map<String, List<Song>> database = new HashMap<>();
    private final List<Song> allSongs = new ArrayList<>();
    private final Random random = new Random();

    public QuizGame() {
        List<Song> loadedSongs = QuizDataLoader.loadFromFile("music.txt");

        for (Song song : loadedSongs) {
            allSongs.add(song);

            database.computeIfAbsent(song.getArtist(), k -> new ArrayList<>()).add(song);
        }
        System.out.println("Загружено песен: " + allSongs.size());

    }

    public QuizQuestion generateQuestion(UserSession session) {
        List<Song> available = allSongs.stream()
                .filter(s -> !session.getAskedQuestions().contains(s.getTitle()))
                .toList();

        if (available.isEmpty()) return null;

        Song randomSong = available.get(new Random().nextInt(available.size()));
        session.getAskedQuestions().add(randomSong.getTitle());

        QuestionType type = random.nextBoolean() ? QuestionType.GUESS_ARTIST : QuestionType.GUESS_TITLE;
        return new QuizQuestion(randomSong, type);
    }
}

