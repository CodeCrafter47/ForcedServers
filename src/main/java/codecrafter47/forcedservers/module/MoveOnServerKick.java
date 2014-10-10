package codecrafter47.forcedservers.module;

import codecrafter47.forcedservers.ForcedServers;
import codecrafter47.forcedservers.Module;
import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by florian on 05.10.14.
 */
public class MoveOnServerKick extends Module implements Listener {



    @Setting
    boolean connectToDefaultServer;

    @Setting
    boolean connectToPermissionServer;

    @Setting
    String targetServer;

    public MoveOnServerKick(@NonNull ForcedServers plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), this);
        if(getPlugin().getProxy().getServerInfo(targetServer) == null){
            getPlugin().getLogger().warning("Server specified as target for kicked players does not exist");
        }
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onServerKick(ServerKickEvent event){
        ServerInfo server;
        if(connectToDefaultServer &&
                (server = getModule(DefaultServerCommand.class).getDefaultServer(event.getPlayer())) != null &&
                getModule(PingServers.class).canConnectToServer(server) &&
                !event.getKickedFrom().equals(server)){
            getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
            event.setCancelServer(server);
            event.setCancelled(true);
            return;
        }
        if(connectToPermissionServer &&
                (server = getModule(PermissionServers.class).getPermissionServer(event.getPlayer())) != null &&
                getModule(PingServers.class).canConnectToServer(server) &&
                !event.getKickedFrom().equals(server)){
            getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
            event.setCancelServer(server);
            event.setCancelled(true);
            return;
        }
        if((server = getPlugin().getProxy().getServerInfo(targetServer)) != null &&
                getModule(PingServers.class).canConnectToServer(server) &&
                !event.getKickedFrom().equals(server)){
            getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
            event.setCancelServer(server);
            event.setCancelled(true);
        }
    }
}
