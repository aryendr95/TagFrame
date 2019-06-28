package com.tagframe.tagframe.Models;

/**
 * Created by abhinav on 16/04/2016.
 */
public class Menu {

    private String tittle;
    private int icon;

    public Menu(String icon, int iconid) {
        this.tittle = icon;
        this.icon = iconid;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
