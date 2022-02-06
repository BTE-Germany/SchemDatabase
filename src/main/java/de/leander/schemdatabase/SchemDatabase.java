package de.leander.schemdatabase;

import de.leander.schemdatabase.events.InventoryClickEvent;
import de.leander.schemdatabase.util.MySQL;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import de.leander.schemdatabase.commands.SDBCommand;
import de.leander.schemdatabase.util.FileBuilder;

public final class SchemDatabase extends JavaPlugin implements Listener{

    @Override
    public void onEnable() {


        this.getServer().getPluginManager().registerEvents(this, this);
        getCommand("norms").setExecutor(new SDBCommand(this));


        Bukkit.getPluginManager().registerEvents(new InventoryClickEvent(this), this);


        new FileBuilder("plugins/SchemDatabase", "mysql.yml")
                .addDefault("mysql.host", "localhost")
                .addDefault("mysql.port", "3306")
                .addDefault("mysql.database", "schemdatabase")
                .addDefault("mysql.user", "root")
                .addDefault("mysql.password", "")
                .copyDefaults(true).save();

        MySQL.connect();
    }

    @Override
    public void onDisable() {
        MySQL.disconnect();
    }

    @EventHandler
    public void onDatabaseLoad(DatabaseLoadEvent event) {
        for(Player player : Bukkit.getOnlinePlayers()){
            if (player.hasPermission("sdb.admin")) {
                SDBCommand.sendMessage("§aHeads von HeadDatabase geladen!", player);
            }
        }

    }



    public Plugin getPlugin(){
        return this;
    }

}
