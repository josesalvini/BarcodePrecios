package com.dinosaurio.preciosdino.ComplementosClass;

public class MenuItem {

    private String menuOpcion;
    private int menuImagen;


    public MenuItem(String menuOpcion, int menuImagen) {
        this.menuOpcion = menuOpcion;
        this.menuImagen = menuImagen;
    }

    public String getMenuOpcion() {
        return menuOpcion;
    }

    public void setMenuOpcion(String menuOpcion) {
        this.menuOpcion = menuOpcion;
    }

    public int getMenuImagen() {
        return menuImagen;
    }

    public void setMenuImagen(int menuImagen) {
        this.menuImagen = menuImagen;
    }


}
