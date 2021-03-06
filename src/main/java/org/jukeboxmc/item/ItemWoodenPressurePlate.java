package org.jukeboxmc.item;

import org.jukeboxmc.block.BlockWoodenPressurePlate;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemWoodenPressurePlate extends Item {

    public ItemWoodenPressurePlate() {
        super( "minecraft:wooden_pressure_plate", 72 );
    }

    @Override
    public BlockWoodenPressurePlate getBlock() {
        return new BlockWoodenPressurePlate();
    }
}
