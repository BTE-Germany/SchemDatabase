package de.leander.schemdatabase.util;

public class Schematic {
    int id;
    String name;
    String category;
    String iconId;
    boolean iconIsHead;

    public Schematic(int id, String name, String category, String iconId, boolean iconIsHead) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.iconId = iconId;
        this.iconIsHead = iconIsHead;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getIconId() {
        return iconId;
    }

    public boolean isIconIsHead() {
        return iconIsHead;
    }
}
