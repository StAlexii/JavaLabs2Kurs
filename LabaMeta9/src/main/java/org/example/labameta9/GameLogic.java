package org.example.labameta9;

import java.util.*;
import java.util.concurrent.*;

public class GameLogic {

    private final Map<String, Queue<String>> userBoxes = new ConcurrentHashMap<>();

    // Список всех зарегистрированных (для проверки существования)
    private final Set<String> registeredUsers = ConcurrentHashMap.newKeySet();

    private final Map<String, Set<String>> authorsLog = new ConcurrentHashMap<>();

    public String addHint(String sender, String target, String hint) {
        if (!userBoxes.containsKey(target)) return "ERROR|Пользователь не найден";

        userBoxes.get(target).add(hint);
        authorsLog.computeIfAbsent(target, k -> ConcurrentHashMap.newKeySet()).add(sender);

        return "OK|Подсказка доставлена анонимно";
    }

    public String register(String nickname) {
        if (nickname == null || nickname.isEmpty()) return "ERROR|Пустое имя";
        registeredUsers.add(nickname);
        userBoxes.putIfAbsent(nickname, new ConcurrentLinkedQueue<>());
        return "OK|Зарегестирован: " + nickname;
    }

    public String checkGuess(String myName, String targetUser, String guessedName) {
        Set<String> realAuthors = authorsLog.get(myName);
        if (realAuthors != null && realAuthors.contains(guessedName)) {
            return "WIN|Да! " + guessedName + " действительно присылал вам подсказку!";
        }
        return "LOSE|Нет, этот человек вам не писал.";
    }

    public List<String> getInbox(String nickname) {
        Queue<String> box = userBoxes.get(nickname);
        List<String> messages = new ArrayList<>();
        if (box != null) {
            while (!box.isEmpty()) {
                messages.add(box.poll());
            }
        }
        return messages;
    }
}
