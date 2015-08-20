package codecrafter47.forcedservers.module;

import codecrafter47.util.chat.BBCodeChatParser;
import codecrafter47.util.chat.ChatParser;
import codecrafter47.forcedservers.ForcedServers;
import codecrafter47.forcedservers.Module;
import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.plugin.Command;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Created by florian on 03.10.14.
 */
public class DefaultServerCommand extends Module {
    Map<String, String> defaultServers;

    @Setting
    String command;
    @Setting
    String permission;

    @Setting
    String commandRemove;
    @Setting
    String permissionRemove;

    @Setting
    List<String> blackList;
    @Setting
    boolean useBlacklistAsWhitelist;

    @Setting
    String msgForbidden;
    @Setting
    String msgSuccess;
    @Setting
    String msgRemoved;
    @Setting
    String msgNoDefaultServer;

    ChatParser chatParser;

    public DefaultServerCommand(@NonNull ForcedServers plugin) {
        super(plugin);
    }

    @Override
    public void onEnable() {
        // load last servers
        defaultServers = new HashMap<>();
        File file = new File(getPlugin().getDataFolder(), "defaultServer.txt");
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        String[] arr = line.split(":");
                        defaultServers.put(arr[0], arr[1]);
                    }
                }
            } catch (FileNotFoundException ex) {
                getPlugin().getLogger().log(Level.SEVERE, "Database File not found", ex);
            } catch (IOException ex) {
                getPlugin().getLogger().log(Level.SEVERE, "Unknown error", ex);
            }
        }

        chatParser = new BBCodeChatParser();

        // register command
        getPlugin().getProxy().getPluginManager().registerCommand(getPlugin(), new Command(command, permission) {
            @Override
            public void execute(CommandSender commandSender, String[] strings) {
                if (!(commandSender instanceof ProxiedPlayer)) {
                    commandSender.sendMessage(new ComponentBuilder("This command may only be executed by a player.")
                            .color(ChatColor.RED).create());
                    return;
                }

                ProxiedPlayer player = (ProxiedPlayer) commandSender;
                Server server = player.getServer();

                if(server == null){
                    player.sendMessage(new ComponentBuilder("To execute that command you must be connected to a server.")
                            .color(ChatColor.RED).create());
                    return;
                }

                if (!(useBlacklistAsWhitelist ^ blackList.contains(server.getInfo().getName()))) {
                    setDefaultServer(player, server.getInfo().getName());
                    player.sendMessage(chatParser.parse(msgSuccess.replaceAll("%server%", server.getInfo().getName())));
                } else {
                    player.sendMessage(chatParser.parse(msgForbidden.replaceAll("%server%", server.getInfo().getName())));
                }
            }
        });
        getPlugin().getProxy().getPluginManager().registerCommand(getPlugin(), new Command(commandRemove, permissionRemove) {
            @Override
            public void execute(CommandSender commandSender, String[] strings) {
                if (!(commandSender instanceof ProxiedPlayer)) {
                    commandSender.sendMessage(new ComponentBuilder("This command may only be executed by a player.")
                            .color(ChatColor.RED).create());
                    return;
                }

                ProxiedPlayer player = (ProxiedPlayer) commandSender;

                if(getDefaultServer(player) != null) {
                    removeDefaultServer(player);
                    player.sendMessage(chatParser.parse(msgRemoved));
                } else {
                    player.sendMessage(chatParser.parse(msgNoDefaultServer));
                }
            }
        });
    }

    @Override
    public void onDisable() {
        File file = new File(getPlugin().getDataFolder(), "defaultServer.txt");
        try (PrintWriter writer = new PrintWriter(file, "UTF-8")) {
            for (Map.Entry<String, String> entry : defaultServers.entrySet()) {
                writer.println(entry.getKey() + ":" + entry.getValue());
            }
        } catch (FileNotFoundException | UnsupportedEncodingException ex) {
            getPlugin().getLogger().log(Level.SEVERE, null, ex);
        }
    }

    public ServerInfo getDefaultServer(@NonNull String uuid) {
        if (!isEnabled())return null;
        if (!defaultServers.containsKey(uuid)) {
            return null;
        }
        return getPlugin().getProxy().getServerInfo(defaultServers.get(uuid));
    }

    public ServerInfo getDefaultServer(@NonNull ProxiedPlayer player) {
        return getDefaultServer(player.getUniqueId().toString());
    }

    public void setDefaultServer(@NonNull String uuid, @NonNull String server){
        defaultServers.put(uuid, server);
    }

    public void setDefaultServer(@NonNull ProxiedPlayer player, @NonNull String server){
        setDefaultServer(player.getUniqueId().toString(), server);
    }

    public void removeDefaultServer(@NonNull ProxiedPlayer player){
        defaultServers.remove(player);
    }
}
