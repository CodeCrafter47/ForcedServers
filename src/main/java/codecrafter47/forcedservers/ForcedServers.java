package codecrafter47.forcedservers;

import codecrafter47.forcedservers.module.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class ForcedServers extends Plugin{

    @Getter(AccessLevel.PROTECTED)
    private Configuration config;

    Map<String, Module> modules = new HashMap<>();

    @Getter(AccessLevel.PUBLIC)
    private static ForcedServers instance;

    @Override
    @SneakyThrows
    public void onEnable(){
        instance = this;
        saveDefaultConfig();
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        addModule(new LastServerDB(this));
        addModule(new DefaultServerCommand(this));
        addModule(new PingServers(this));
        addModule(new PermissionServers(this));
        addModule(new MoveOnJoin(this));
        addModule(new MoveOnLobbyConnect(this));
        addModule(new MoveOnServerKick(this));
        addModule(new MoveOnServerConnect(this));
    }

    @Override
    public void onDisable() {
        for(Module module: modules.values()){
            if(module.isEnabled())module.onDisable();
        }
    }

    public void addModule(Module module){
        modules.put(module.getName(),module);
    }

    public Module getModule(@NonNull String name) {
        return modules.get(name);
    }

    @SneakyThrows
    private void saveDefaultConfig(){
        if (!getDataFolder().exists())
            getDataFolder().mkdir();

        File file = new File(getDataFolder(), "config.yml");

        if (!file.exists()) {
            Files.copy(getResourceAsStream("config.yml"), file.toPath());
        }
    }
}
