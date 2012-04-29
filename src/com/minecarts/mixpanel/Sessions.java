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
    protected static final Map<OfflinePlayer, String> sessions = new HashMap<OfflinePlayer, String>();
    
    protected Sessions() { }
    public static Sessions getInstance() {
        return instance == null
                ? instance = new Sessions()
                : instance;
    }
    
    public static String getSession(OfflinePlayer player) {
        return sessions.get(player);
    }
    public static String getSession(OfflinePlayer player, boolean startIfNull) {
        String session = getSession(player);
        return startIfNull && session == null
                ? startSession(player)
                : session;
    }
    public static String setSession(OfflinePlayer player, String session) {
        return session == null
                ? sessions.remove(player)
                : sessions.put(player, session);
    }
    
    public static String startSession(OfflinePlayer player) {
        String session = UUID.randomUUID().toString();
        setSession(player, session);
        return session;
    }
    public static String endSession(OfflinePlayer player) {
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
