package codecrafter47.forcedservers.module;

import codecrafter47.forcedservers.ForcedServers;
import codecrafter47.forcedservers.Module;
import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * Created by florian on 05.10.14.
 */
public class MoveOnJoin extends Module implements Listener {

    @Setting
    boolean connectToDefaultServer;

    @Setting
    boolean connectToLastServer;

    @Setting
    boolean connectToPermissionServer;

    public MoveOnJoin(@NonNull ForcedServers plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), this);
    }

    @Override
    public void onDisable() {

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onServerConnect(ServerConnectEvent event) {
        if (event.getPlayer().getServer() != null) {
            return;
        }
        ServerInfo server;
        if (connectToDefaultServer &&
                (server = getModule(DefaultServerCommand.class).getDefaultServer(event.getPlayer())) != null &&
                getModule(PingServers.class).canConnectToServer(server)) {
            getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
            event.setTarget(server);
            return;
        }
        if (connectToPermissionServer &&
                (server = getModule(PermissionServers.class).getPermissionServer(event.getPlayer())) != null &&
                getModule(PingServers.class).canConnectToServer(server)) {
            getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
            event.setTarget(server);
            return;
        }
        if (connectToLastServer &&
                (server = getModule(LastServerDB.class).getLastServer(event.getPlayer())) != null &&
                getModule(PingServers.class).canConnectToServer(server)) {
            getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
            event.setTarget(server);
            return;
        }
    }
}
