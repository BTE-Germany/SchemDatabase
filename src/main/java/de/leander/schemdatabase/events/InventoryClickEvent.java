package de.leander.schemdatabase.events;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.registry.WorldData;
import de.leander.schemdatabase.inventories.*;
import de.leander.schemdatabase.util.MySQL;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import de.leander.schemdatabase.SchemDatabase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.SQLException;

public class InventoryClickEvent implements Listener {

    SchemDatabase schemDatabase;
    static Player player;
    VehiclesScreen vehiclesScreen;
    TreesScreen treesScreen;
    TrafficSignsScreen trafficSignsScreen;
    TrainsScreen trainsScreen;
    MiscScreen miscScreen;
    InfrastructureScreen infrastructureScreen;
    AirplanesScreen airplanesScreen;


    public InventoryClickEvent(SchemDatabase schemDatabase) {
        this.schemDatabase = schemDatabase;

    }

    @EventHandler
    public void onClick(org.bukkit.event.inventory.InventoryClickEvent e)  {

        player = (Player) e.getWhoClicked();

    if(e.getClickedInventory() != null){
        if (e.getClickedInventory().getTitle().startsWith("§cSchemDB §r§8- §9")) {
            e.setCancelled(true);

            if(e.getCurrentItem().getItemMeta() != null) {
                if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lVehicles")) {
                    this.vehiclesScreen = new VehiclesScreen(this.schemDatabase, player);
                    vehiclesScreen.getPagedInv().open(player);
                }
                else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lTrees")) {
                    this.treesScreen = new TreesScreen(this.schemDatabase, player);
                    treesScreen.getPagedInv().open(player);
                }
                else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lTraffic Signs")) {
                    this.trafficSignsScreen = new TrafficSignsScreen(this.schemDatabase, player);
                    trafficSignsScreen.getPagedInv().open(player);
                }
                else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lTrains")) {
                    this.trainsScreen = new TrainsScreen(this.schemDatabase, player);
                    trainsScreen.getPagedInv().open(player);
                }
                else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lMiscellaneous")) {
                    this.miscScreen = new MiscScreen(this.schemDatabase, player);
                    miscScreen.getPagedInv().open(player);
                }
                else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lInfrastructure")) {
                    this.infrastructureScreen = new InfrastructureScreen(this.schemDatabase, player);
                    infrastructureScreen.getPagedInv().open(player);
                }
                else if (e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lAirplanes")) {
                    this.airplanesScreen = new AirplanesScreen(this.schemDatabase, player);
                    airplanesScreen.getPagedInv().open(player);
                }
                else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lSearch")){
                    player.closeInventory();
                    player.sendMessage("§b§lBTEG §7» §fUsage: /sdb search <Schematic>");
                }
                else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lSubmit")){
                    player.closeInventory();
                    player.sendMessage("§b§lBTEG §7» §7Usage: /sdb submit <Name> <category:/sdb categories> <Head-ID> <isHead>");
                }
                else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lPlugin info")){
                    player.closeInventory();
                    player.chat("/norms info");
                }
                else if (e.getCurrentItem().getType() == Material.BARRIER) { player.closeInventory(); }

            }
        }
        else if(e.getClickedInventory().getTitle().contains("Page")) {

            if(!e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lNext Page") && !e.getCurrentItem().getItemMeta().getDisplayName().contains("§4§lBack to menu") && !e.getCurrentItem().getItemMeta().getDisplayName().contains("§a§lPrevious Page") && !e.getCurrentItem().getItemMeta().getDisplayName().contains("§4§lClose Menu")){
                player.closeInventory();
            try {
                String uuid = MySQL.downloadSchematic(e.getCurrentItem().getItemMeta().getDisplayName());
                File file = new File(uuid+".schematic");
                loadClipboardToPlayer(player, loadClipboard(file,e.getCurrentItem().getItemMeta().getDisplayName()));


               file.delete();


            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    }

    }

    static void loadClipboardToPlayer(Player player, Clipboard clipboard){
        World pasteWorld = new BukkitWorld(player.getWorld());
        WorldData pasteWorldData = pasteWorld.getWorldData();
        Actor actor = getWorldEditPlugin().wrapCommandSender(player);
        getWorldEditPlugin().getWorldEdit().getSessionManager().get(actor).setClipboard(new ClipboardHolder(clipboard, pasteWorldData));
    }

    private static WorldEditPlugin getWorldEditPlugin() {
        return (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
    }

    public static Clipboard loadClipboard(File file, String name) {
        World pasteWorld = new BukkitWorld(player.getWorld());
        WorldData pasteWorldData = pasteWorld.getWorldData();
        Clipboard clipboard = null;
        ClipboardFormat format = ClipboardFormat.findByFile(file);
        try {
            assert format != null;
            FileInputStream inputStream = new FileInputStream(file);
            ClipboardReader reader = format.getReader(inputStream);
            clipboard = reader.read(pasteWorldData);
            player.sendMessage("§b§lBTEG §7» §aSuccessfully loaded §6" + name + "§a! §7Paste it with //paste -a");
            inputStream.close();
        } catch (IOException e) {
            player.sendMessage("§b§lBTEG §7» §4Failed to load §6"+name+"§4! Please report this in the support!");
            System.out.println("BTEG » Failed to load "+name+"!");
        }
        return clipboard;
    }


}