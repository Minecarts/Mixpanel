package com.minecarts.mixpanel;

import java.util.logging.Level;
import java.text.MessageFormat;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.world.*;
import org.bukkit.event.weather.*;
import org.bukkit.event.server.*;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Creature;


public class Plugin extends JavaPlugin implements Listener {
    protected final Events events = new Events("d8bae573e57b9ef7bcb2468efcfee734");
    
    @Override
    public void onEnable() {
        // start sessions for online players
        for(Player player : getServer().getOnlinePlayers()) {
            Sessions.getSession(player, true);
        }
        
        getServer().getPluginManager().registerEvents(Sessions.getInstance(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }
    
    
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void serverPing(final ServerListPingEvent event) {
        events.track("Server ping", new HashMap<String, Object>() {{
            put("ip", event.getAddress().getHostAddress());
        }});
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerLogin(final PlayerLoginEvent event) {
        events.track("Player login", new HashMap<String, Object>() {{
            put("player", event.getPlayer().getName());
            put("ip", event.getAddress().getHostAddress());
            put("session", Sessions.getSession(event.getPlayer()));
            put("distinct_id", Sessions.getSession(event.getPlayer()));
            put("hostname", event.getHostname());
            put("result", event.getResult().toString());
            put("message", event.getKickMessage());
        }});
    }
    
    @EventHandler
    public void playerJoin(final PlayerJoinEvent event) {
        events.track("Player join", new HashMap<String, Object>() {{
            put("player", event.getPlayer().getName());
            put("ip", event.getPlayer().getAddress().getAddress().getHostAddress());
            put("session", Sessions.getSession(event.getPlayer()));
            put("distinct_id", Sessions.getSession(event.getPlayer()));
        }});
    }
    
    @EventHandler
    public void playerQuit(final PlayerQuitEvent event) {
        events.track("Player quit", new HashMap<String, Object>() {{
            put("player", event.getPlayer().getName());
            put("ip", event.getPlayer().getAddress().getAddress().getHostAddress());
            put("session", Sessions.getSession(event.getPlayer()));
            put("distinct_id", Sessions.getSession(event.getPlayer()));
        }});
    }
    
    
    public void log(String message) {
        log(Level.INFO, message);
    }
    public void log(Level level, String message) {
        getLogger().log(level, message);
    }
    public void log(String message, Object... args) {
        log(MessageFormat.format(message, args));
    }
    public void log(Level level, String message, Object... args) {
        log(level, MessageFormat.format(message, args));
    }
    
    public void debug(String message) {
        log(Level.FINE, message);
    }
    public void debug(String message, Object... args) {
        debug(MessageFormat.format(message, args));
    }
}