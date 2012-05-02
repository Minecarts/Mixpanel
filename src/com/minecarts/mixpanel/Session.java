package com.minecarts.mixpanel;

import java.util.UUID;
import java.util.Date;

import org.bukkit.OfflinePlayer;

public class Session {
    protected OfflinePlayer player;
    protected String id;
    protected Date start;
    
    public Session(OfflinePlayer player) {
        this(player, UUID.randomUUID().toString());
    }
    public Session(OfflinePlayer player, String id) {
        this(player, id, new Date());
    }
    public Session(OfflinePlayer player, String id, Date start) {
        this.player = player;
        this.id = id;
        this.start = start;
    }
    
    public OfflinePlayer getPlayer() {
        return player;
    }
    public String getId() {
        return id;
    }
    public long elapsed() {
        return new Date().getTime() - start.getTime();
    }
}
