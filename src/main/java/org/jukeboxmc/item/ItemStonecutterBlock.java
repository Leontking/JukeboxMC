package org.jukeboxmc.item;

import org.jukeboxmc.block.BlockStonecutterBlock;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemStonecutterBlock extends Item {

    public ItemStonecutterBlock() {
        super( "minecraft:stonecutter_block", -197 );
    }

    @Override
    public BlockStonecutterBlock getBlock() {
        return new BlockStonecutterBlock();
    }
}
