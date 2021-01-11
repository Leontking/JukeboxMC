package org.jukeboxmc.block;

import org.jukeboxmc.item.Item;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class BlockMonsterEgg extends Block {

    public BlockMonsterEgg() {
        super( "minecraft:monster_egg" );
    }

    @Override
    public Item toItem() {
        return super.toItem().setMeta( this.getMonsterEggStoneType().ordinal() );
    }

    public void setMonsterEggStoneType( MonsterEggStoneType monsterEggStoneType ) {
        this.setState( "monster_egg_stone_type", monsterEggStoneType.name().toLowerCase() );
    }

    public MonsterEggStoneType getMonsterEggStoneType() {
        return this.stateExists( "monster_egg_stone_type" ) ? MonsterEggStoneType.valueOf( this.getStringState( "monster_egg_stone_type" ).toUpperCase()) : MonsterEggStoneType.STONE;
    }

    public enum MonsterEggStoneType {
        STONE,
        COBBLESTONE,
        STONE_BRICK,
        MOSSY_STONE_BRICK,
        CRACKED_STONE_BRICK,
        CHISELED_STONE_BRICK
    }
}
