package com.minecarts.mixpanel;

import java.util.logging.Logger;
import java.util.logging.Level;

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.codec.binary.Base64;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONAware;
import org.json.simple.parser.JSONParser;

import org.bukkit.entity.Player;
import org.bukkit.Location;


public class Events implements Runnable  {
    protected static final Logger logger = Logger.getLogger(Events.class.getCanonicalName());
    protected static final String endpoint = "http://api.mixpanel.com/track?test=1";
    protected static final JSONParser parser = new JSONParser();
    
    protected final String token;
    protected final URL url;
    
    protected final List events = new ArrayList<HashMap<String, Object>>();
    
    
    public Events(String token) {
        this.token = token;
        
        try {
            this.url = new URL(endpoint);
        }
        catch(MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
    
    public void track(final String event) {
        track(event, new HashMap<String, Object>());
    }
    
    public void track(final String event, final Map<String, Object> properties) {
        if(!properties.containsKey("token")) properties.put("token", this.token);
        if(!properties.containsKey("time")) properties.put("time", Long.toString(System.currentTimeMillis() / 1000));
        
        events.add(new HashMap<String, Object>() {{
            put("event", event);
            put("properties", properties);
        }});
    }
    
    
        
    public void run() {
        List batch;
        synchronized(events) {
            if(events.isEmpty()) {
                return;
            }
            else {
                batch = new ArrayList(events.subList(0, Math.min(50, events.size())));
                events.removeAll(batch);
            }
        }

        Date start = new Date();
        String json = JSONArray.toJSONString(batch);
        String encoded = Base64.encodeBase64String(json.getBytes());
        logger.finer(String.format("\nJSON:\n%s\n\nBase64:\n%s\n", json, encoded));

        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            conn.setDoOutput(true);
            conn.setDoInput(true);

            OutputStreamWriter or = new OutputStreamWriter(conn.getOutputStream());
            or.write(String.format("data=%s", encoded));
            or.flush();
            or.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getResponseCode() >= 400 ? conn.getErrorStream() : conn.getInputStream()));
            String line;
            List<String> lines = new ArrayList<String>();
            while((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();

            int responseCode = conn.getResponseCode();
            String response = StringUtils.join(lines, "\n");

            logger.fine(String.format("Got response (%d)\n%s", responseCode, response));

            if(responseCode == 200 && response.equals("1")) {
                logger.info(String.format("Successfully sent %d events", batch.size()));
            }
            else {
                logger.warning(String.format("Failed response? Requeueing %d events...", batch.size()));
                // requeue events
                synchronized(events) {
                    events.addAll(batch);
                }
            }
        }
        catch(IOException e) {
            logger.warning(String.format("Connection or stream failure, requeueing %d events...", batch.size()));
            e.printStackTrace();

            // requeue events
            synchronized(events) {
                events.addAll(batch);
            }
        }
        finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        logger.fine(String.format("Response took %d ms", new Date().getTime() - start.getTime()));
    }
    
}
