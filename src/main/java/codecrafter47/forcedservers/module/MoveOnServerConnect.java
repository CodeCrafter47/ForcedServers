package codecrafter47.forcedservers.module;

import codecrafter47.forcedservers.ForcedServers;
import codecrafter47.forcedservers.Module;
import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveOnServerConnect extends Module implements Listener{

    Map<String, ServerConfig> servers;

    public MoveOnServerConnect(@NonNull ForcedServers plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        // load config
        servers = new HashMap<>();
        Configuration config = getConfig().getSection("servers");
        for(String serverName: config.getKeys()){
            Configuration config2 = config.getSection(serverName);
            ServerConfig serverConfig = new ServerConfig();
            serverConfig.blackList = config2.getStringList("blackList");
            serverConfig.useBlacklistAsWhitelist = config2.getBoolean("useBlacklistAsWhitelist");
            serverConfig.connectToDefaultServer = config2.getBoolean("connectToDefaultServer");
            serverConfig.connectToLastServer = config2.getBoolean("connectToLastServer");
            serverConfig.connectToPermissionServer = config2.getBoolean("connectToPermissionServer");
            servers.put(serverName, serverConfig);
        }
        getPlugin().getProxy().getPluginManager().registerListener(getPlugin(),this);
    }

    @Override
    public void onDisable() {

    }

    public static class ServerConfig{
        public List<String> blackList;
        public boolean useBlacklistAsWhitelist;

        boolean connectToDefaultServer = false;

        boolean connectToLastServer = false;

        boolean connectToPermissionServer = true;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerConnect(ServerConnectEvent event){
        for(String serverName: servers.keySet()) {
            if (!event.getTarget().getName().equalsIgnoreCase(serverName)) {
                continue;
            }
            if (event.getPlayer().getServer() != null) {
                if (event.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(serverName)) {
                    continue;
                }
            }
            ServerInfo server;
            if (servers.get(serverName).connectToDefaultServer &&
                    (server = getModule(DefaultServerCommand.class).getDefaultServer(event.getPlayer())) != null &&
                    getModule(PingServers.class).canConnectToServer(server) &&
                    (event.getPlayer().getServer() == null || !event.getPlayer().getServer().getInfo().equals(server)) &&
                    !(servers.get(serverName).useBlacklistAsWhitelist ^ servers.get(serverName).blackList.contains(server.getName()))) {
                getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
                event.setTarget(server);
                return;
            }
            if (servers.get(serverName).connectToPermissionServer &&
                    (server = getModule(PermissionServers.class).getPermissionServer(event.getPlayer())) != null &&
                    getModule(PingServers.class).canConnectToServer(server) &&
                    (event.getPlayer().getServer() == null || !event.getPlayer().getServer().getInfo().equals(server)) &&
                    !(servers.get(serverName).useBlacklistAsWhitelist ^ servers.get(serverName).blackList.contains(server.getName()))) {
                getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
                event.setTarget(server);
                return;
            }
            if (servers.get(serverName).connectToLastServer &&
                    (server = getModule(LastServerDB.class).getLastServer(event.getPlayer())) != null &&
                    getModule(PingServers.class).canConnectToServer(server) &&
                    (event.getPlayer().getServer() == null || !event.getPlayer().getServer().getInfo().equals(server)) &&
                    !(servers.get(serverName).useBlacklistAsWhitelist ^ servers.get(serverName).blackList.contains(server.getName()))) {
                getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
                event.setTarget(server);
                return;
            }
        }
    }
}
