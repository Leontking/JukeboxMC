package org.jukeboxmc.block;

import org.jukeboxmc.item.Item;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class BlockHardStainedGlassPane extends Block {

    public BlockHardStainedGlassPane() {
        super( "minecraft:hard_stained_glass_pane" );
    }

    @Override
    public Item toItem() {
        return super.toItem().setMeta( this.getColor().ordinal() );
    }

    public void setColor( BlockColor color ) {
        this.setState( "color", color.name().toLowerCase() );
    }

    public BlockColor getColor() {
        return this.stateExists( "color" ) ? BlockColor.valueOf( this.getStringState( "color" ).toUpperCase() ) : BlockColor.WHITE;
    }
}
