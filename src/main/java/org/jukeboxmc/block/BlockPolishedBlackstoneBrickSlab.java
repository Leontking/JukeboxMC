package org.jukeboxmc.block;

public class BlockPolishedBlackstoneBrickSlab extends Block {

    public BlockPolishedBlackstoneBrickSlab() {
        super("minecraft:polished_blackstone_brick_slab");
    }

    public void setTopSlot( boolean value ) {
        this.setState( "top_slot_bit", value ? (byte) 1 : (byte) 0 );
    }

    public boolean isTopSlot() {
        return this.stateExists( "top_slot_bit" ) && this.getByteState( "top_slot_bit" ) == 1;
    }
}