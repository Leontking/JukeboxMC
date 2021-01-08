package org.jukeboxmc.item;

import org.jukeboxmc.block.BlockCrimsonTrapdoor;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemCrimsonTrapdoor extends Item {

    public ItemCrimsonTrapdoor() {
        super( "minecraft:crimson_trapdoor", -246 );
    }

    @Override
    public BlockCrimsonTrapdoor getBlock() {
        return new BlockCrimsonTrapdoor();
    }
}
