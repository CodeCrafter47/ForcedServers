/*
 * Copyright (C) 2014 florian
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package codecrafter47.forcedservers;

import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.RejectedExecutionException;

public class PingTask
        implements Runnable {

    private final ServerInfo server;
    private final Plugin plugin;
    private boolean online = true;
    private int maxPlayers = Integer.MAX_VALUE;
    private int onlinePlayers = 0;

    public PingTask(Plugin plugin, ServerInfo server) {
        this.server = server;
        this.plugin = plugin;
    }

    public boolean isOnline() {
        return online;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getOnlinePlayers() {
        return onlinePlayers;
    }

    @Override
    public void run() {
        try {
            server.ping(new Callback<ServerPing>() {

                @Override
                public void done(ServerPing v, Throwable thrwbl) {
                    maxPlayers = Integer.MAX_VALUE;
                    if (thrwbl != null) {
                    /*
                    if ((!(thrwbl instanceof ConnectException)) && (!(thrwbl instanceof SocketTimeoutException))) {
                        plugin.getLogger().log(Level.WARNING, null, thrwbl);
                    }
                    */
                        online = false;
                        return;
                    }
                    if (v == null) {
                        PingTask.this.online = false;
                        return;
                    }
                    maxPlayers = v.getPlayers().getMax();
                    online = true;
                    onlinePlayers = v.getPlayers().getOnline();
                }

            });
        } catch(RejectedExecutionException ex){
            // BungeeCord has been shut down
        }
    }
}
