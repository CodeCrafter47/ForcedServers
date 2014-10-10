package codecrafter47.forcedservers.module;

import codecrafter47.forcedservers.ForcedServers;
import codecrafter47.forcedservers.Module;
import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.List;

/**
 * Created by florian on 05.10.14.
 */
public class MoveOnLobbyConnect extends Module implements Listener{

    @Setting
    String mainLobby;

    @Setting
    List<String> otherLobbyServers;

    @Setting
    boolean connectToDefaultServer;

    @Setting
    boolean connectToLastServer;

    @Setting
    boolean connectToPermissionServer;

    public MoveOnLobbyConnect(@NonNull ForcedServers plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        getPlugin().getProxy().getPluginManager().registerListener(getPlugin(), this);
        if(getPlugin().getProxy().getServerInfo(mainLobby) == null){
            getPlugin().getLogger().warning("Server specified as mainLobby does not exist");
        }
    }

    @Override
    public void onDisable() {

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerConnect(ServerConnectEvent event){
        if(!event.getTarget().getName().equalsIgnoreCase(mainLobby)){
            return;
        }
        if(event.getPlayer().getServer() != null){
            if(otherLobbyServers.contains(event.getPlayer().getServer().getInfo().getName())){
                return;
            }
            if(event.getPlayer().getServer().getInfo().getName().equalsIgnoreCase(mainLobby)){
                return;
            }
        }
        ServerInfo server;
        if(connectToDefaultServer &&
                (server = getModule(DefaultServerCommand.class).getDefaultServer(event.getPlayer())) != null &&
                getModule(PingServers.class).canConnectToServer(server) &&
                (event.getPlayer().getServer() == null  || !event.getPlayer().getServer().getInfo().equals(server))){
            getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
            event.setTarget(server);
            return;
        }
        if(connectToPermissionServer &&
                (server = getModule(PermissionServers.class).getPermissionServer(event.getPlayer())) != null &&
                getModule(PingServers.class).canConnectToServer(server) &&
                (event.getPlayer().getServer() == null  || !event.getPlayer().getServer().getInfo().equals(server))){
            getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
            event.setTarget(server);
            return;
        }
        if(connectToLastServer &&
                (server = getModule(LastServerDB.class).getLastServer(event.getPlayer())) != null &&
                getModule(PingServers.class).canConnectToServer(server) &&
                (event.getPlayer().getServer() == null  || !event.getPlayer().getServer().getInfo().equals(server))){
            getPlugin().getLogger().info(String.format("Connecting %s to %s", event.getPlayer().getName(), server.getName()));
            event.setTarget(server);
            return;
        }
    }
}
