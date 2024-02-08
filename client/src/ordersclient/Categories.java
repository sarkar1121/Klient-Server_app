/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package ordersclient;

/**
 * this enum is used to store category names 
 * @author hrusk
 */
public enum Categories {
    POMUCKY("Pomůcky"),PROSTREDKY("Prostředky"),OCHRANNE_POMUCKY("Ochranné pomůcky"), NAHRADNI_DILY("Náhrandí díly"), SPOTREBNI_MATERIAL("Spotřební materiál"), VETSI_POLOZKY("Větší položky");
     
    private final String name;

    Categories(String name) {
        this.name = name;
    }
    
    /**
     * This method is used to return name of category
     * @return name of category
     */
    public String getDescription() {
        return name;
    }
}
