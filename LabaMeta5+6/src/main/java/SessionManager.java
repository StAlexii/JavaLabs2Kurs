import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
    private final ConcurrentHashMap<Long, UserSession> sessions = new ConcurrentHashMap<>();

    public UserSession getSession(long chatId) {
        return sessions.computeIfAbsent(chatId, id -> new UserSession());
    }
}