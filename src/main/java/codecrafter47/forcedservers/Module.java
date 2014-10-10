package codecrafter47.forcedservers;

import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import net.md_5.bungee.config.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

public abstract class Module {
    @Getter
    private String name;

    @Getter
    private ForcedServers plugin;

    @Getter
    private Configuration config;

    public Module(Configuration config) {
        this.name = getClass().getSimpleName();
        this.plugin = ForcedServers.getInstance();

        this.config = config.getSection(name);

        if (isEnabled()) {
            loadConfig();
            onEnable();
        }
    }

    public Module(@NonNull ForcedServers plugin) {
        this.name = getClass().getSimpleName();
        this.plugin = plugin;

        config = plugin.getConfig().getSection(name);

        if (isEnabled()) {
            loadConfig();
            onEnable();
        }
    }

    @SneakyThrows
    private void loadConfig() {
        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Setting.class)) {
                field.setAccessible(true);
                field.set(this, getConfig().get(field.getName(), field.getType()));
            }
        }
    }

    public boolean isEnabled() {
        return config.getBoolean("enabled");
    }

    public abstract void onEnable();

    public abstract void onDisable();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public static @interface Setting {

    }

    public <T extends Module> T getModule(Class<T> clasz){
        return ((T)getPlugin().getModule(clasz.getSimpleName()));
    }
}
