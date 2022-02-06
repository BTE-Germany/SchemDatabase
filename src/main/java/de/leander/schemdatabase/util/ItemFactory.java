package de.leander.schemdatabase.util;

import com.tchristofferson.pagedinventories.NavigationRow;
import com.tchristofferson.pagedinventories.handlers.PagedInventoryCustomNavigationHandler;
import com.tchristofferson.pagedinventories.navigationitems.CloseNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.CustomNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.NextNavigationItem;
import com.tchristofferson.pagedinventories.navigationitems.PreviousNavigationItem;
import de.leander.schemdatabase.commands.SDBCommand;
import de.leander.schemdatabase.inventories.StandardScreen;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;


public class ItemFactory {

    static Player zPlayer;

    ItemFactory(){

    }

    static public ItemStack newHead(boolean isHead, String id, String pText, String pBeschreibung){
        if(isHead) {
            HeadDatabaseAPI api = new HeadDatabaseAPI();
            ItemStack head = api.getItemHead(id);
            if(api.getItemHead(id) != null){
                ItemMeta meta = head.getItemMeta();
                meta.setDisplayName(pText);
                List<String> lore = Collections.singletonList(pBeschreibung);
                meta.setLore(lore);
                head.setItemMeta(meta);

                return head;
            }else{
                ItemStack item = new ItemStack(Material.SKULL_ITEM, 1);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(pText);
                meta.setLore(Collections.singletonList(pBeschreibung));
                item.setItemMeta(meta);
                return item;
            }
        }else{
            ItemStack item = new ItemStack(Material.getMaterial(id), 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(pText);
            meta.setLore(Collections.singletonList(pBeschreibung));
            item.setItemMeta(meta);
            return item;
        }
    }


    public static NavigationRow getNavigationRow(Player player, StandardScreen screen){

        zPlayer = player;
        CustomNavigationItem navigationItem = new CustomNavigationItem(ItemFactory.newHead(true, "9226", "§4§lBack to menu", "§7Get back to the categories"), 0) {
            @Override
            public void handleClick(PagedInventoryCustomNavigationHandler handler) {
                player.openInventory(screen.getCategories());
            }
        };
        NextNavigationItem nextNavItem = new NextNavigationItem(ItemFactory.newHead(true, "9237", "§a§lNext Page", ""), 8);
        PreviousNavigationItem prevNavItem = new PreviousNavigationItem(ItemFactory.newHead(true, "9243", "§a§lPrevious Page", ""), 7);
        CloseNavigationItem closeNavItem = new CloseNavigationItem(ItemFactory.newHead(true, "9274", "§4§lClose Menu", "§7Close the menu"), 6);
        return new NavigationRow(nextNavItem, prevNavItem, closeNavItem, navigationItem);
    }
}
