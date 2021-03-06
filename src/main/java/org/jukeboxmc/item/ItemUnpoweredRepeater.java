package org.jukeboxmc.item;

import org.jukeboxmc.block.BlockUnpoweredRepeater;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemUnpoweredRepeater extends Item {

    public ItemUnpoweredRepeater() {
        super( "minecraft:unpowered_repeater", 93 );
    }

    @Override
    public BlockUnpoweredRepeater getBlock() {
        return new BlockUnpoweredRepeater();
    }
}
