package com.abhiram.abhilogin.event;

import com.abhiram.abhilogin.Main;
import com.abhiram.abhilogin.login.Account;
import com.abhiram.abhilogin.login.Session;
import com.abhiram.abhilogin.util.Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;


public class PlayerControlEvent implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent e)
    {
        if(!Util.getPlayerLoginManager().isPlayerRegistered(e.getPlayer()))
        {
            // Send Message To Player
            for(String str : Main.getInstance().messageConfig.getConfig().getStringList("Player-register-message"))
            {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',str));
            }
            return;
        }


        if(!Util.getPlayerLoginManager().getPlayerAccount(e.getPlayer()).getLoginstatus())
        {
            for(String str : Main.getInstance().messageConfig.getConfig().getStringList("Player-login-message"))
            {
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',str));
            }
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e)
    {
        if(Util.getPlayerLoginManager().isPlayerRegistered(e.getPlayer()))
        {
            Account account = Util.getPlayerLoginManager().getPlayerAccount(e.getPlayer());
            if(account.getLoginstatus())
            {
                return;
            }

            if(Util.getPlayerLoginManager().VerifyPassword(e.getMessage(),account.getPassword(),Util.getEncyptionType()))
            {
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',Main.getInstance().messageConfig.getConfig().getString("Login-success")));
                account.SetLoginStatus(true);

                Session ses = new Session(e.getPlayer().getAddress());

                int Intervel = Main.getInstance().config.getConfig().getInt("Session-Interval");

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(),ses, 20 * Intervel);
                return;
            }
            else {
                if (Main.getInstance().config.getConfig().getBoolean("Kick-playeronwrongpassword"))
                {
                    final Player p = e.getPlayer();
                    e.setCancelled(true);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            p.kickPlayer(ChatColor.translateAlternateColorCodes('&',Main.getInstance().messageConfig.getConfig().getString("Kick-reason")));
                        }
                    },10);
                    return;
                }
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',Main.getInstance().messageConfig.getConfig().getString("Invalid-Password")));
                return;
            }
        }

        if(!Util.getPlayerLoginManager().isPlayerRegistered(e.getPlayer()))
        {
            e.setCancelled(true);
            Util.getPlayerLoginManager().StartRegister(e.getPlayer(),e.getMessage());
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',Main.getInstance().messageConfig.getConfig().getString("Register-done-message")));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e)
    {
        if(!Util.getPlayerLoginManager().isPlayerRegistered(e.getPlayer()))
        {
            if(Main.getInstance().config.getConfig().getBoolean("Disable-Player-Movement"))
            {
                e.setCancelled(true);
            }
            return;
        }

        if(!Util.getPlayerLoginManager().getPlayerAccount(e.getPlayer()).getLoginstatus())
        {
            if(Main.getInstance().config.getConfig().getBoolean("Disable-Player-Movement"))
            {
                e.setCancelled(true);
            }
            return;
        }
    }


    @EventHandler
    public void onPlayerBlockPlace(BlockPlaceEvent e)
    {
        if(!Util.getPlayerLoginManager().isPlayerRegistered(e.getPlayer()))
        {
            e.setCancelled(true);
            return;
        }

        if(!Util.getPlayerLoginManager().getPlayerAccount(e.getPlayer()).getLoginstatus())
        {
            e.setCancelled(true);
            return;
        }
    }


    @EventHandler
    public void onPlayerBlockBreak(BlockBreakEvent e)
    {
        if(!Util.getPlayerLoginManager().isPlayerRegistered(e.getPlayer()))
        {
            e.setCancelled(true);
            return;
        }

        if(!Util.getPlayerLoginManager().getPlayerAccount(e.getPlayer()).getLoginstatus())
        {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e)
    {
        if(Util.getPlayerLoginManager().isPlayerRegistered(e.getPlayer()))
        {
            Account account = Util.getPlayerLoginManager().getPlayerAccount(e.getPlayer());
            account.SetLoginStatus(false);
        }
    }

    @EventHandler
    public void onPlayerCommandrun(PlayerCommandPreprocessEvent e)
    {
        if(!Util.getPlayerLoginManager().isPlayerRegistered(e.getPlayer()))
        {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',Main.getInstance().messageConfig.getConfig().getString("No-commands")));
            return;
        }

        if(!Util.getPlayerLoginManager().getPlayerAccount(e.getPlayer()).getLoginstatus())
        {
            e.setCancelled(true);
            e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',Main.getInstance().messageConfig.getConfig().getString("No-commands")));
        }
    }
}
