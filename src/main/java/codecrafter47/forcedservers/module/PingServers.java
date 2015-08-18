package codecrafter47.forcedservers.module;

import codecrafter47.forcedservers.ForcedServers;
import codecrafter47.forcedservers.Module;
import codecrafter47.util.bungee.PingTask;
import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by florian on 05.10.14.
 */
public class PingServers extends Module{

    Map<String, PingTask> serverState;

    @Setting
    int interval;

    @Setting
    boolean dontConnectToFullServers;

    public PingServers(@NonNull ForcedServers plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        serverState = new HashMap<>();
        for (ServerInfo server : getPlugin().getProxy().getServers().values()) {
            addServer(server);
        }
    }

    private void addServer(@NonNull ServerInfo server) {
        PingTask task = new PingTask(server);
        serverState.put(server.getName(), task);
        getPlugin().getProxy().getScheduler().schedule(getPlugin(), task, interval,
                interval, TimeUnit.SECONDS);
    }

    @Override
    public void onDisable() {

    }

    public boolean canConnectToServer(String server){
        if(!isEnabled())return true;
        if(serverState.containsKey(server)){
            return serverState.get(server).isOnline() && (!dontConnectToFullServers || serverState.get(server)
                    .getMaxPlayers() > serverState.get(server).getOnlinePlayers());
        } else {
            addServer(getPlugin().getProxy().getServerInfo(server));
            return true;
        }
    }

    public  boolean canConnectToServer(ServerInfo server){
        return canConnectToServer(server.getName());
    }
}
