package com.minecarts.mixpanel;

import java.util.logging.Level;
import java.text.MessageFormat;

import java.util.Map;
import java.util.HashMap;

import org.bukkit.Bukkit;

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
    protected Events events;
    
    @Override
    public void onEnable() {
        events = new Events("d8bae573e57b9ef7bcb2468efcfee734");
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, events, 20, 20);
        
        // start sessions for online players
        for(Player player : Bukkit.getOnlinePlayers()) {
            Sessions.getSession(player, true);
        }
        
        Bukkit.getPluginManager().registerEvents(Sessions.getInstance(), this);
        Bukkit.getPluginManager().registerEvents(this, this);
    }
    
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void serverPing(final ServerListPingEvent event) {
        events.track("Server ping", new HashMap<String, Object>() {{
            // Mixpanel properties
            put("ip", event.getAddress().getHostAddress());
            put("mp_name_tag", event.getAddress().getHostAddress());
            // Custom properties
            put("IP", event.getAddress().getHostAddress());
            put("Online", event.getNumPlayers());
            put("Slots", event.getMaxPlayers());
            put("MOTD", event.getMotd());
        }});
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerLogin(final PlayerLoginEvent event) {
        events.track("Player login", new HashMap<String, Object>() {{
            // Mixpanel properties
            put("distinct_id", Sessions.getSession(event.getPlayer()).getId());
            put("ip", event.getAddress().getHostAddress());
            put("mp_name_tag", event.getPlayer().getName());
            // Custom properties
            put("Player", event.getPlayer().getName());
            put("World", event.getPlayer().getWorld().getName());
            put("IP", event.getAddress().getHostAddress());
            put("Session", Sessions.getSession(event.getPlayer()).getId());
            put("Hostname", event.getHostname());
            put("Result", event.getResult().toString());
            if(event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
                put("Kick message", event.getKickMessage());
            }
        }});
    }
    
    @EventHandler
    public void playerJoin(final PlayerJoinEvent event) {
        events.track("Player join", new HashMap<String, Object>() {{
            // Mixpanel properties
            put("distinct_id", Sessions.getSession(event.getPlayer()).getId());
            put("ip", event.getPlayer().getAddress().getAddress().getHostAddress());
            put("mp_name_tag", event.getPlayer().getName());
            // Custom properties
            put("Player", event.getPlayer().getName());
            put("World", event.getPlayer().getWorld().getName());
            put("IP", event.getPlayer().getAddress().getAddress().getHostAddress());
            put("Session", Sessions.getSession(event.getPlayer()).getId());
        }});
    }
    
    @EventHandler
    public void playerQuit(final PlayerQuitEvent event) {
        events.track("Player quit", new HashMap<String, Object>() {{
            // Mixpanel properties
            put("distinct_id", Sessions.getSession(event.getPlayer()).getId());
            put("ip", event.getPlayer().getAddress().getAddress().getHostAddress());
            put("mp_name_tag", event.getPlayer().getName());
            // Custom properties
            put("Player", event.getPlayer().getName());
            put("World", event.getPlayer().getWorld().getName());
            put("IP", event.getPlayer().getAddress().getAddress().getHostAddress());
            put("Session", Sessions.getSession(event.getPlayer()).getId());
            put("Session Duration", Sessions.getSession(event.getPlayer()).elapsed());
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