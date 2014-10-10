package codecrafter47.forcedservers.module;

import codecrafter47.forcedservers.ForcedServers;
import codecrafter47.forcedservers.Module;
import lombok.NonNull;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

/**
 * Created by florian on 05.10.14.
 */
public class PermissionServers extends Module {
    public PermissionServers(@NonNull ForcedServers plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public ServerInfo getPermissionServer(ProxiedPlayer player){
        if(!isEnabled())return null;
        for(Map.Entry<String, ServerInfo> server: getPlugin().getProxy().getServers().entrySet()){
            if(player.hasPermission("forceserver." + server.getKey())){
                return server.getValue();
            }
        }
        return null;
    }
}
