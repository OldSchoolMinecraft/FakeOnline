package me.moderator_man.fo;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class PlayerHandler extends PlayerListener {
    private FakeOnline fo;

    public PlayerHandler(FakeOnline fo) {
        this.fo = fo;
    }

    public void onPlayerPreLogin(PlayerPreLoginEvent event) {
        final String username = event.getName().toLowerCase();
        if(fo.getBetaEVOAuth().contains(username)) {
            fo.getBetaEVOAuth().remove(username);
        }

        //Add connection pause
        try {
            //Connection pause for Beta Evolutions Support
            event.getLoginProcessHandler().addConnectionPause(fo);
            final String url = "https://auth.johnymuffin.com/serverAuth.php?method=1&username=" + URLEncoder.encode(event.getName(), "UTF-8") + "&userip=" + URLEncoder.encode(event.getAddress().getHostAddress(), "UTF-8");
            Bukkit.getServer().getScheduler().scheduleAsyncDelayedTask(fo, () -> {
                final JSONObject res = new JSONObject(get(url));
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(fo, () -> {
                    if (res.has("verified")) {
                        if (res.get("verified").equals(true)) {
                            //User is verified
                            System.out.println(event.getName() + " Has been authenticated with Beta Evolutions");
                            fo.getBetaEVOAuth().add(username);
                        }
                    }
                    event.getLoginProcessHandler().removeConnectionPause(fo);
                }, 0L);
            }, 0L);


        } catch (Exception e) {
            System.out.println("Beta Evolutions Failure :(" + e + ": " + e.getMessage());
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(fo, () -> {
                event.getLoginProcessHandler().removeConnectionPause(fo);
            }, 0L);


        }
    }

    public void onPlayerLogin(PlayerLoginEvent event) {
        try {
            Player player = event.getPlayer();
            String name = player.getName();

            if(fo.getBetaEVOAuth().contains(name.toLowerCase())) {
                if (fo.um.isRegistered(name)) {
                    logAuth(false, String.format("Authenticated user: '%s' with Beta Evolutions.", name));
                    event.allow();
                    fo.um.authenticateUser(name);
                    return;
                }
            }

            String serverHash = fo.hash(fo.getServer().getIp() + fo.getServer().getPort());
            JSONObject res = new JSONObject(get(String.format("http://api.oldschoolminecraft.com:8080/checkserver?username=%s&serverHash=%s", name, serverHash)));
            if (res.has("error")) {
                event.disallow(Result.KICK_OTHER, res.getString("error"));
            } else {
                if (res.getString("response").equalsIgnoreCase("yes")) {
                    if (fo.cm.getBoolean("log-successful-auths", true))
                        logAuth(false, String.format("Authenticated user: '%s'.", name));
                    fo.um.put_userHasSession(name);
                    event.allow();
                    fo.um.authenticateUser(name);
                    if (fo.um.isRegistered(name)) {
                        if (!fo.um.isApproved(name))
                            fo.um.freezePlayer(name);
                    } else {
                        fo.um.freezePlayer(name);
                    }
                } else {
                    if (fo.cm.getBoolean("log-failed-auths", true))
                        logAuth(true, String.format("Authentication failed for user: '%s'.", name));
                    String policy = fo.cm.getString("reject-policy", "default");
                    switch (policy) {
                        default:
                            event.disallow(Result.KICK_OTHER, fo.cm.getString("reject-message", "Not authenticated with oldschoolminecraft.com"));
                            break;
                        case "default": // default is "supplement"
                            event.allow();
                            break;
                        case "supplement": // allow, but don't add them to authenticated users (require them to enter password when they login)
                            event.allow();
                            break;
                        case "relaxed": // allow everyone to join, without authentication (should be called insecure tbh)
                            event.allow();
                            fo.um.authenticateUser(name);
                            break;
                        case "strict": // disallow everyone who isn't authenticated with the session server
                            event.disallow(Result.KICK_OTHER, fo.cm.getString("reject-message", "Not authenticated with oldschoolminecraft.com"));
                            break;
                    }
                }
            }

            if (event.getResult() == Result.ALLOWED) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(fo, new Runnable() {
                    public void run() {
                        if (fo.um.isAuthenticated(name)) {
                            if (fo.um.isRegistered(name)) {
                                if (fo.um.isApproved(name)) {
                                    fo.sendSuccess(player, "You are authenticated, no login required!");
                                } else {
                                    fo.sendError(player, "Login with /login <password>");
                                    player.sendMessage(FakeOnline.adminlog_prefix + "You will only have to do this once!");
                                }
                            } else {
                                fo.sendError(player, "Register with /register <password> <confirm>");
                                player.sendMessage(FakeOnline.adminlog_prefix + "You will NOT be required to login when you join!");
                            }
                        } else {
                            if (fo.um.isRegistered(name)) {
                                fo.sendError(player, "Login with /login <password>");
                            } else {
                                fo.sendError(player, "Register with /register <password> <confirm>");
                                fo.sendError(player, "You WILL be required to login when you join!");
                            }
                        }
                    }
                }, 1);
            }
        } catch (
                Exception ex) {
            event.disallow(Result.KICK_OTHER, "Connection rejected due to backend fault.");
            if (fo.cm.getBoolean("debug-mode", false))
                ex.printStackTrace();
        }

    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            fo.um.deauthenticateUser(event.getPlayer().getName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location fromLoc = event.getFrom();
        final Location toLoc = event.getTo();

        if (fromLoc.getX() == toLoc.getX() && fromLoc.getZ() == toLoc.getZ() && fromLoc.getY() > toLoc.getY())
            return;

        if (!fo.um.isAuthenticated(player.getName()) || fo.um.isFrozen(player.getName())) {
            warn(event.getPlayer());

            event.setCancelled(true);
            player.teleport(fromLoc);
        }
    }

    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            event.setMessage("");
            event.setCancelled(true);
            warn(event.getPlayer());
        }
    }

    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            String label = event.getMessage().split(" ")[0];
            if (label.equalsIgnoreCase("/register"))
                return;
            if (label.equalsIgnoreCase("/login"))
                return;
            event.setMessage("/help");
            event.setCancelled(true);
            warn(event.getPlayer());
        }
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    public void onPlayerPortal(PlayerPortalEvent event) {
        if (!fo.um.isAuthenticated(event.getPlayer().getName()) || fo.um.isFrozen(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    public void onPlayerDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!fo.um.isAuthenticated(player.getName()) || fo.um.isFrozen(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    public void onEntityTargetPlayer(EntityTargetEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (!fo.um.isAuthenticated(player.getName()) || fo.um.isFrozen(player.getName())) {
                event.setCancelled(true);
            }
        }
    }

    private void logAuth(boolean failed, String msg) {
        System.out.println(msg);
    }

    private void warn(Player player) {
        if (fo.um.isRegistered(player.getName()))
            player.sendMessage(FakeOnline.error_prefix + "Login with /login <password>");
        else
            player.sendMessage(FakeOnline.error_prefix + "Register with /register <password> <confirm>");
    }

    private static String get(String url) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            return response.toString();
        } catch (Exception ex) {
            JSONObject obj = new JSONObject();
            obj.append("error", ex.getMessage());
            return obj.toString();
        }
    }
}
