package org.jukeboxmc.item;

import org.jukeboxmc.block.BlockColor;
import org.jukeboxmc.block.BlockConcrete;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemConcrete extends Item {

    public ItemConcrete() {
        super( "minecraft:concrete", 236 );
    }

    @Override
    public BlockConcrete getBlock() {
        return new BlockConcrete();
    }

    public void setColor( BlockColor blockColor ) {
        this.setMeta( blockColor.ordinal() );
    }

    public BlockColor getColor() {
        return BlockColor.values()[this.getMeta()];
    }

}
