package com.mundiaurum.analysisapp.events;

public class ItemSavedEvent {
    private String itemId;
    private String packageId;


    public ItemSavedEvent(String itemId, String packageId){
        this.itemId = itemId;
        this.packageId = packageId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getPackageId() {
        return packageId;
    }
}
