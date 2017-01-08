package me.jaimemartz.lobbybalancer.ping;

public abstract class PingCallback {
    public abstract void onPong(ServerStatus info);
}
