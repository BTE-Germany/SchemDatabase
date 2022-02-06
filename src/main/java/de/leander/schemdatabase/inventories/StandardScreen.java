package de.leander.schemdatabase.inventories;

import com.tchristofferson.pagedinventories.PagedInventory;
import com.tchristofferson.pagedinventories.PagedInventoryAPI;
import de.leander.schemdatabase.SchemDatabase;
import de.leander.schemdatabase.util.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import de.leander.schemdatabase.util.ItemFactory;

import java.sql.SQLException;

public class StandardScreen {

    SchemDatabase schemDatabase;

    PagedInventoryAPI api;
    private PagedInventory pagedInventory;
    private PagedInventory pagedInv;
    int schematicCount;

    public StandardScreen(SchemDatabase schemDatabase){

        this.schemDatabase = schemDatabase;
        this.api = new PagedInventoryAPI(this.schemDatabase.getPlugin());

    }


    public Inventory getCategories(){

        try {
            this.schematicCount = MySQL.getAllSchematicsCount();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        Inventory inv = Bukkit.createInventory(null, 54,"§cSchemDB §r§8- §9"+schematicCount+" schems");

        for(int i = 0; i < 10; i++){ inv.setItem(i,ItemFactory.newHead(false,"STAINED_GLASS_PANE"," ","")); }

        inv.setItem(17, ItemFactory.newHead(false,"STAINED_GLASS_PANE"," ",""));
        inv.setItem(18,ItemFactory.newHead(false,"STAINED_GLASS_PANE"," ",""));
        inv.setItem(26,ItemFactory.newHead(false,"STAINED_GLASS_PANE"," ",""));
        inv.setItem(27,ItemFactory.newHead(false,"STAINED_GLASS_PANE"," ",""));
        inv.setItem(35,ItemFactory.newHead(false,"STAINED_GLASS_PANE"," ",""));
        inv.setItem(36,ItemFactory.newHead(false,"STAINED_GLASS_PANE"," ",""));
        inv.setItem(44,ItemFactory.newHead(false,"STAINED_GLASS_PANE"," ",""));

        for(int i = 45; i < 54; i++){ inv.setItem(i,ItemFactory.newHead(false,"STAINED_GLASS_PANE"," ","")); }

        inv.setItem(10,ItemFactory.newHead(true,"23257","§a§lVehicles","§7Get a vehicle into your clipboard and paste it with //paste"));
        inv.setItem(11,ItemFactory.newHead(false,"SAPLING","§a§lTrees","§7Get a tree into your clipboard and paste it with //paste"));
        inv.setItem(12,ItemFactory.newHead(true,"36186","§a§lTraffic Signs","§7Get a traffic sign into your clipboard and paste it with //paste"));
        inv.setItem(13,ItemFactory.newHead(true,"21478","§a§lTrains","§7Get a train into your clipboard and paste it with //paste"));
        inv.setItem(14,ItemFactory.newHead(false,"HOPPER","§a§lMiscellaneous","§7Get something other into your clipboard and paste it with //paste"));
        inv.setItem(15,ItemFactory.newHead(false,"RAILS","§a§lInfrastructure","§7Get a part of infrastructure into your clipboard and paste it with //paste"));
        inv.setItem(16,ItemFactory.newHead(true,"36041","§a§lAirplanes","§7Get an airplane into your clipboard and paste it with //paste"));
        inv.setItem(43,ItemFactory.newHead(false,"COMPASS","§a§lSearch for norms","§7/norms search <Norm>"));
        inv.setItem(42,ItemFactory.newHead(false,"BOOK_AND_QUILL","§a§lSubmit new norms","§7/sdb submit <Name> <category:/sdb categories> <Head-ID/Item-Name> <isHead>"));
        inv.setItem(41,ItemFactory.newHead(false,"SIGN","§a§lPlugin info","§7/norms info"));
        inv.setItem(40,ItemFactory.newHead(false,"BARRIER","§4§lClose menu","§7Close this menu"));

        return inv;
    }


}
