package codecrafter47.forcedservers.module;

import codecrafter47.forcedservers.ForcedServers;
import codecrafter47.forcedservers.Module;
import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class LastServerDB extends Module implements Listener {
    Map<String, String> lastServer;

    @Setting List<String> blackList;
    @Setting boolean useBlacklistAsWhitelist;

    public LastServerDB(@NonNull ForcedServers plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        // load last servers
        lastServer = new HashMap<>();
        File file = new File(getPlugin().getDataFolder(), "lastServer.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        String[] arr = line.split(":");
                        lastServer.put(arr[0], arr[1]);
                    }
                }
            } catch (FileNotFoundException ex) {
                getPlugin().getLogger().log(Level.SEVERE, "Database File not found", ex);
            } catch (IOException ex) {
                getPlugin().getLogger().log(Level.SEVERE, "Unknown error", ex);
            }
        }

        // register listener
        getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), this);
    }

    @Override
    public void onDisable() {
        File file = new File(getPlugin().getDataFolder(), "lastServer.txt");
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            for (Map.Entry<String, String> entry : lastServer.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            getPlugin().getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public ServerInfo getLastServer(@NonNull String uuid) {
        if (!isEnabled())return null;
        if (!lastServer.containsKey(uuid)) {
            return null;
        }
        return getPlugin().getProxy().getServerInfo(lastServer.get(uuid));
    }

    public ServerInfo getLastServer(@NonNull ProxiedPlayer player) {
        return getLastServer(player.getUniqueId().toString());
    }

    @EventHandler(priority = Byte.MAX_VALUE)
    public void onServerConnected(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
        Server server = event.getServer();
        if (lastServer != null && !(useBlacklistAsWhitelist ^ blackList.contains(server.getInfo().getName()))) {
            lastServer.put(player.getUniqueId().toString(), server.getInfo().
                    getName());
        }
    }

}
