package org.jukeboxmc.item;

import org.jukeboxmc.block.BlockSpruceButton;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class ItemSpruceButton extends Item {

    public ItemSpruceButton() {
        super( "minecraft:spruce_button", -144 );
    }

    @Override
    public BlockSpruceButton getBlock() {
        return new BlockSpruceButton();
    }
}
