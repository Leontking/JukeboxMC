package org.jukeboxmc.item;

import org.jukeboxmc.block.BlockHoneycombBlock;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemHoneycomb extends Item {

    public ItemHoneycomb() {
        super( "minecraft:honeycomb", 580 );
    }

    @Override
    public BlockHoneycombBlock getBlock() {
        return new BlockHoneycombBlock();
    }
}
