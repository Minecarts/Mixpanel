package com.minecarts.mixpanel;

import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Sessions implements Listener {
    private static Sessions instance = null;
    protected static final Map<OfflinePlayer, Session> sessions = new HashMap<OfflinePlayer, Session>();
    
    protected Sessions() { }
    public static Sessions getInstance() {
        return instance == null
                ? instance = new Sessions()
                : instance;
    }
    
    public static Session getSession(OfflinePlayer player) {
        return sessions.get(player);
    }
    public static Session getSession(OfflinePlayer player, boolean startIfNull) {
        Session session = getSession(player);
        return startIfNull && session == null
                ? startSession(player)
                : session;
    }
    public static Session setSession(OfflinePlayer player, Session session) {
        return session == null
                ? sessions.remove(player)
                : sessions.put(player, session);
    }
    
    public static Session startSession(OfflinePlayer player) {
        Session session = new Session(player);
        setSession(player, session);
        return session;
    }
    public static Session endSession(OfflinePlayer player) {
        return setSession(player, null);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerLogin(PlayerLoginEvent event) {
        if(event.getResult() != PlayerLoginEvent.Result.ALLOWED) return;
        startSession(event.getPlayer());
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerQuit(PlayerQuitEvent event) {
        endSession(event.getPlayer());
    }
    
}
