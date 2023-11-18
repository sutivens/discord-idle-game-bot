package org.example;

import java.util.ArrayList;

public abstract class Dungeon {
    protected int reqLevel;
    protected ArrayList<Mob> listMobs;
    protected ArrayList<Item> listItem;
    protected ArrayList<Equipment> listEquipment;

    Dungeon(int reqLevel, ArrayList<Mob> listMobs, ArrayList<Item> listItem, ArrayList<Equipment> listEquipment) {
        this.reqLevel = reqLevel;
        this.listMobs = listMobs;
        this.listItem = listItem;
        this.listEquipment = listEquipment;
    }

    public int getReqLevel() {
        return reqLevel;
    }

    public void setReqLevel(int reqLevel) {
        this.reqLevel = reqLevel;
    }

    public ArrayList<Mob> getListMobs() {
        return listMobs;
    }

    public void setListMobs(ArrayList<Mob> listMobs) {
        this.listMobs = listMobs;
    }

    public ArrayList<Item> getListItem() {
        return listItem;
    }

    public void setListItem(ArrayList<Item> listItem) {
        this.listItem = listItem;
    }

    public ArrayList<Equipment> getListEquipment() {
        return listEquipment;
    }

    public void setListEquipment(ArrayList<Equipment> listEquipment) {
        this.listEquipment = listEquipment;
    }
}
