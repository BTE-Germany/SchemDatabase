package de.leander.schemdatabase.inventories;

import com.google.errorprone.annotations.ForOverride;
import com.tchristofferson.pagedinventories.PagedInventory;
import com.tchristofferson.pagedinventories.PagedInventoryAPI;
import de.leander.schemdatabase.SchemDatabase;
import de.leander.schemdatabase.util.ItemFactory;
import de.leander.schemdatabase.util.MySQL;
import de.leander.schemdatabase.util.Schematic;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class AirplanesScreen {
    SchemDatabase schemDatabase;
    PagedInventory pagedInv;
    PagedInventoryAPI api;
    StandardScreen screen;
    Player player;
    ArrayList<Schematic> schematics = new ArrayList<Schematic>();
    int schematicCount;
    int maxPerPage = 45;


    public AirplanesScreen(SchemDatabase schemDatabase, Player pPlayer) {
        this.schemDatabase = schemDatabase;
        this.api = new PagedInventoryAPI(this.schemDatabase.getPlugin());
        this.screen = new StandardScreen(this.schemDatabase);
        player = pPlayer;
    }

    void createAirplanesScreen() {

        try {
            this.schematicCount = MySQL.getSchematicsCountInCategory("airplanes");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        this.pagedInv = (PagedInventory) api.createPagedInventory(ItemFactory.getNavigationRow(player, screen));


            for (int i = 0; i < Math.ceil((double) this.schematicCount / (double) this.maxPerPage); i++) {
                Inventory inv1 = Bukkit.createInventory(null, 54, "§cAirplanes§r §8- Page " + (i + 1));

                    this.getData(i);

                for (Schematic schem : schematics) {
                    inv1.addItem(ItemFactory.newHead(schem.isIconIsHead(), schem.getIconId(), "§9" + schem.getName(), "§8ID: §7" + String.valueOf(schem.getId())));
                }

                this.pagedInv.addPage(inv1);


            }

    }

    void fillPageAsync(Inventory pInv){

    }

    public PagedInventory getPagedInv() {
        if (this.pagedInv == null){
                this.createAirplanesScreen();
        }
        return this.pagedInv;
    }

    private void getData(int page) {

        this.schematics.clear();

        try {
            ResultSet schemRS = MySQL.getPageSchematicsByCategory("airplanes", page, this.maxPerPage);

            while (true) {
                try {
                    if (!schemRS.next()) break;
                    if(schemRS.getInt("iconIsHead") == 1) {
                        this.schematics.add(new Schematic(
                                schemRS.getInt("id"),
                                schemRS.getString("name"),
                                schemRS.getString("category"),
                                schemRS.getString("iconId"),
                                true
                                ));
                    } else {
                        this.schematics.add(new Schematic(
                                schemRS.getInt("id"),
                                schemRS.getString("name"),
                                schemRS.getString("category"),
                                schemRS.getString("iconId"),
                                false
                        ));
                    }


                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
