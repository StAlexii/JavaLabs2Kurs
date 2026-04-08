public class Song {
    private final String artist;
    private final String title;
    private final String lyricsSnippet; // Та самая строчка из песни

    public Song(String artist, String title, String lyricsSnippet) {
        this.artist = artist;
        this.title = title;
        this.lyricsSnippet = lyricsSnippet;
    }
    public String getArtist() { return artist; }
    public String getTitle() { return title; }
    public String getLyricsSnippet() { return lyricsSnippet; }
}

