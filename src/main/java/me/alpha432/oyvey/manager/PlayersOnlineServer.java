package me.alpha432.oyvey.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import me.alpha432.oyvey.manager.playeronlineutils.Timer;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class PlayersOnlineServer implements IManager {

    private final Timer pingTimer = new Timer();
    private List<String> onlinePlayers = new ArrayList<>();
    private List<String> allPlayers = new ArrayList<>();

    public void onUpdate() {
        if (pingTimer.every(90000))
            fetchData();
    }

    public void fetchData() {
        if (true)//se puede o no poner en configuracion ekjemplo clickgui 
            pingServerOnline("Luis");
        onlinePlayers = getPlayers(true);
        allPlayers = getPlayers(false);
    }

    public static void main(String[] args) {
        PlayersOnlineServer server = new PlayersOnlineServer();
        server.onUpdate();
        server.fetchData();
        System.out.println(server.getOnlinePlayers());
        server.pingServerOffline("Luis");
    }

    public final HttpClient client1 = HttpClient.newHttpClient();
    public void pingServerOnline(String name) {
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:3000/v1/users/online?name=" + name))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
            try {
                client1.send(req, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    public final HttpClient client3 = HttpClient.newHttpClient();
    public void pingServerOffline(String name) {
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:3000/v1/users/offline?name=" + name))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
            try {
                client3.send(req, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
    }

    public static final HttpClient client2 = HttpClient.newHttpClient();
    public static HttpResponse<String> response;;
    public static List<String> getPlayers(boolean online) {
        final HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:3000/v1/users" + (online ? "/online" : "")))
                .GET()
                .build();
        final List<String> names = new ArrayList<>();

            //HttpResponse<String> response;
            try {
                response = client2.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
            array.forEach(e -> names.add(e.getAsJsonObject().get("name").getAsString()));

        return names;
    }

    public List<String> getOnlinePlayers() {
        return onlinePlayers;
    }

    public List<String> getAllPlayers() {
        return allPlayers;
    }
/*
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import me.alpha432.oyvey.manager.playeronlineutils.Timer;
import org.apache.commons.compress.utils.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PlayersOnlineServer implements IManager {

    private final Timer pingTimer = new Timer();
    private List<String> onlinePlayers = new ArrayList<>();
    private List<String> allPlayers = new ArrayList<>();

    public void onUpdate() {
        if (pingTimer.every(90000))
            fetchData();
    }

    public void fetchData() {
        if (true)//se puede o no poner en configuracion ekjemplo clickgui 
            pingServer("mc.getSession().getUsername()");
        onlinePlayers = getPlayers(true);
        allPlayers = getPlayers(false);
    }

    public static void main(String[] args) {
        PlayersOnlineServer server = new PlayersOnlineServer();
        server.fetchData();
    }

    
    public void pingServer(String name) {
        HttpRequest req = HttpRequest.newBuilder(URI.create("http://localhost:3000/v1/users/online?name=" + name))
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try (HttpClient client = HttpClient.newHttpClient()) {
            client.send(req, HttpResponse.BodyHandlers.ofString());
        } catch (Throwable ignored) {
        }
    }

    public static List<String> getPlayers(boolean online) {
        final HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:3000/v1/users" + (online ? "/online" : "")))
                .GET()
                .build();
        final List<String> names = new ArrayList<>();

        try ( final HttpClient client = HttpClient.newHttpClient()) {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
            array.forEach(e -> names.add(e.getAsJsonObject().get("name").getAsString()));
        } catch (Throwable ignored) {
        }
        return names;
    }

    public List<String> getOnlinePlayers() {
        return Lists.newArrayList(onlinePlayers.iterator());
    }

    public List<String> getAllPlayers() {
        return Lists.newArrayList(allPlayers.iterator());
    }*/
}