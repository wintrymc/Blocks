package it.wintry.customblocks.objects;

import lombok.Data;
import org.bukkit.Material;

@Data
public class CustomFurniture {

    private final String name;
    private final Material itemMaterial;
    private final Material furnitureMaterial;
    private final int itemModelData;
    private final int furnitureModelData;

}
