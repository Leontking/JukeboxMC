package org.jukeboxmc.item;

import org.jukeboxmc.block.BlockCrimsonStandingSign;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemCrimsonSign extends Item {

    public ItemCrimsonSign() {
        super( "minecraft:crimson_sign", 602 );
    }

    @Override
    public BlockCrimsonStandingSign getBlock() {
        return new BlockCrimsonStandingSign();
    }
}
