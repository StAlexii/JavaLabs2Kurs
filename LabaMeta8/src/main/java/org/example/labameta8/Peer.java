package org.example.labameta8;

import java.util.Objects;

public class Peer {
    private final String nickname;
    private final String ip;
    private final int port;
    private long lastSeen;
    private boolean isOnline;

    public Peer(String nickname, String ip, int port) {
        this.nickname = nickname;
        this.ip = ip;
        this.port = port;
        this.lastSeen = System.currentTimeMillis();
    }

    public String getNickname() { return nickname; }
    public String getIp() { return ip; }
    public int getPort() { return port; }

    public void setOnline(boolean online) { isOnline = online; }
    public boolean isOnline() { return isOnline; }
    public void updateLastSeen() {
        this.lastSeen = System.currentTimeMillis();
        this.isOnline = true;
    }
    public long getLastSeen() { return lastSeen; }

    public static Peer parsePeer(String data) {
        try {
            String[] parts = data.split("\\s+");
            if (parts.length < 4 || !parts[0].equals("HELLO")) return null;
            return new Peer(parts[1], parts[2], Integer.parseInt(parts[3]));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Peer peer = (Peer) o;
        return port == peer.port && Objects.equals(ip, peer.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @Override
    public String toString() {
        return nickname + " (" + ip + ":" + port + ")";
    }


}
