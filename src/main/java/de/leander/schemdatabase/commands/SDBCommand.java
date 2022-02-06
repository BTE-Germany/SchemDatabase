package de.leander.schemdatabase.commands;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardWriter;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import de.leander.schemdatabase.SchemDatabase;
import de.leander.schemdatabase.inventories.SearchScreen;
import de.leander.schemdatabase.inventories.StandardScreen;
import de.leander.schemdatabase.util.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SDBCommand implements CommandExecutor {

    SchemDatabase schemDatabase;
    static Player player;

    public SDBCommand(SchemDatabase schemDatabase) {
        this.schemDatabase = schemDatabase;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        player = (Player) sender;

        // Get number of seconds from wherever you want


        if (cmd.getName().equalsIgnoreCase("norms") || cmd.getName().equalsIgnoreCase("normen") || cmd.getName().equalsIgnoreCase("sdb")) {

            if (args.length == 0) {

                if (player.hasPermission("sdb.open")) {

                    StandardScreen screen = new StandardScreen(this.schemDatabase);
                    player.openInventory(screen.getCategories());

                    return true;
                } else {
                    player.sendMessage("§b§lBTEG §7» §4No permission for /sdb!");
                }

            }

            if (args.length == 1) {
                if (args[0].matches("help")) {
                    player.chat("/help SchemDatabase");
                } else if (args[0].matches("info")) {
                    player.sendMessage("§b§lBTEG §7» §fPlugin version: 1.0.0");
                    player.sendMessage("§b§lBTEG §7» §fMore than 275 norms");
                    player.sendMessage("§b§lBTEG §7» §fDeveloped by BTE Germany Dev Team");
                }else if (args[0].matches("submit")) {
                    player.sendMessage("§b§lBTEG §7» §7/sdb submit <Name> <category:/sdb categories> <Head-ID/Item-Name> <isHead>");
                }else if (args[0].matches("categories")) {
                    player.sendMessage("§b§lBTEG §7» vehicles");
                    player.sendMessage("§b§lBTEG §7» trees");
                    player.sendMessage("§b§lBTEG §7» trafficsigns");
                    player.sendMessage("§b§lBTEG §7» trains");
                    player.sendMessage("§b§lBTEG §7» misc");
                    player.sendMessage("§b§lBTEG §7» infrastructure");
                    player.sendMessage("§b§lBTEG §7» airplanes");

                } else {
                    player.sendMessage("§b§lBTEG §7» §4Usage: §r§7/sdb search §7<Norm>");
                }
            } else if (args.length > 1) {
                if (args[0].matches("search")) {
                    if (player.hasPermission("sdb.search")) {
                        if (args[1].length() >= 3) {
                            //  gui9 = new SearchScreen(args[1]);
                            //  player.openInventory(gui9.getInventory());
                            SearchScreen screen = new SearchScreen(this.schemDatabase, player);
                            if(screen.getPagedInv(args[1]) == null){
                                player.sendMessage("§b§lBTEG §7» §4No schematic with name "+args[1]+" found!");
                            }else{
                                screen.getPagedInv(args[1]).open(player);
                            }


                        } else {
                            player.sendMessage("§b§lBTEG §7» §4Please search for at least 3 letters!");
                        }
                    } else {
                        player.sendMessage("§b§lBTEG §7» §4No permission for /sdb search!");
                    }
                } else if (args[0].matches("submit")) {
                    if (args.length == 5) {
                        if (player.hasPermission("sdb.submit")) {
                            Region plotRegion;

                            // Get WorldEdit selection of player
                            try {
                                plotRegion = WorldEdit.getInstance().getSessionManager().findByName(player.getName()).getSelection(WorldEdit.getInstance().getSessionManager().findByName(player.getName()).getSelectionWorld());
                            } catch (NullPointerException | IncompleteRegionException ex) {
                                ex.printStackTrace();
                                player.sendMessage("§b§lBTEG §7» §cPlease select a WorldEdit region!");
                                return true;
                            }

                            if (args[2].equalsIgnoreCase("vehicles") || args[2].equalsIgnoreCase("trees") || args[2].equalsIgnoreCase("trafficsigns") || args[2].equalsIgnoreCase("trains") || args[2].equalsIgnoreCase("misc") || args[2].equalsIgnoreCase("infrastructure") || args[2].equalsIgnoreCase("airplanes")) {
                                if (args[4].equalsIgnoreCase("y") || args[4].equalsIgnoreCase("yes") || args[4].equalsIgnoreCase("n") || args[4].equalsIgnoreCase("no")) {
                                    UUID uuid = UUID.randomUUID();
                                    File schematic = new File(uuid.toString() + ".schematic");
                                    try {
                                        schematic.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    boolean createdFile = false;
                                    try {
                                        createdFile = schematic.createNewFile();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");

                                    Clipboard cb = new BlockArrayClipboard(plotRegion);
                                    cb.setOrigin(cb.getRegion().getCenter());
                                    LocalSession playerSession = WorldEdit.getInstance().getSessionManager().findByName(player.getName());
                                    ForwardExtentCopy copy = new ForwardExtentCopy(playerSession.createEditSession(worldEdit.wrapPlayer(player)), plotRegion, cb, plotRegion.getMinimumPoint());
                                    try {
                                        Operations.completeLegacy(copy);
                                    } catch (MaxChangedBlocksException e) {
                                        e.printStackTrace();
                                    }


                                    try (ClipboardWriter writer = ClipboardFormat.SCHEMATIC.getWriter(new FileOutputStream(schematic, false))) {
                                        writer.write(cb, plotRegion.getWorld().getWorldData());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    try {
                                        FileInputStream input = new FileInputStream(schematic);
                                        PreparedStatement preparedStatement = MySQL.getConnection()
                                                .prepareStatement("INSERT INTO `schemdatabase`.`schematics` (`name`, `schematicData`, `category`, `iconId`, `iconIsHead`) VALUES (?, ?, ?, ?, ?);");

                                        preparedStatement.setString(1, args[1]);
                                        preparedStatement.setBinaryStream(2, input);
                                        preparedStatement.setString(3, args[2]);
                                        preparedStatement.setString(4, args[3]);
                                        if(args[4].equalsIgnoreCase("y") || args[4].equalsIgnoreCase("yes")){
                                            preparedStatement.setInt(5, 1);
                                        }else if(args[4].equalsIgnoreCase("n") || args[4].equalsIgnoreCase("no")){
                                            preparedStatement.setInt(5, 0);
                                        }


                                        player.sendMessage("§b§lBTEG §7» §7Schematic submitted to SchemDatabase!");
                                        preparedStatement.execute();
                                        schematic.delete();
                                    } catch (FileNotFoundException e) {
                                        e.printStackTrace();
                                    } catch (SQLException throwables) {
                                        throwables.printStackTrace();
                                    }
                                } else {
                                    player.sendMessage("§b§lBTEG §7» §7<isHead> must be no (Item) or yes (Head).");
                                    return true;
                                }
                            } else {
                                player.sendMessage("§b§lBTEG §7» §7" + args[2] + " is not a valid category!");
                                return true;
                            }
                        } else {
                            player.sendMessage("§b§lBTEG §7» §4No permission for /sdb submit!");
                            return true;
                        }
                    }else{
                        player.sendMessage("§b§lBTEG §7» §cWrong usage: §7/sdb submit <Name> <category:/sdb categories> <Head-ID/Item-Name> <isHead>");
                        return true;
                    }

                }

            }

            return true;
        }
        return true;
    }

    public static void sendMessage(String pMessage, Player pplayer){
        pplayer.sendMessage("§b§lBTEG §7» "+pMessage);
    }
}


