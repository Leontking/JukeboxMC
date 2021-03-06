package org.jukeboxmc.world;

import org.jukeboxmc.block.Block;
import org.jukeboxmc.block.BlockAir;
import org.jukeboxmc.block.BlockDirt;
import org.jukeboxmc.block.BlockType;
import org.jukeboxmc.block.direction.BlockFace;
import org.jukeboxmc.blockentity.BlockEntity;
import org.jukeboxmc.entity.Entity;
import org.jukeboxmc.item.Item;
import org.jukeboxmc.math.AxisAlignedBB;
import org.jukeboxmc.math.BlockPosition;
import org.jukeboxmc.math.Vector;
import org.jukeboxmc.network.packet.*;
import org.jukeboxmc.player.Player;
import org.jukeboxmc.utils.Utils;
import org.jukeboxmc.world.chunk.Chunk;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @author LucGamesYT
 * @version 1.0
 */
public class World {

    private String name;

    private int currentTick;

    private Map<Long, Chunk> chunkMap = new HashMap<>();
    private Map<Long, CompletableFuture<Chunk>> chunkFutures = new HashMap<>();
    private Map<Long, Player> players = new HashMap<>();

    public World( String name ) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void update( long timestamp ) {
        for ( Player player : this.players.values() ) {
            if ( player != null && player.isSpawned() ) {
                player.getPlayerConnection().update( timestamp );

                if ( this.currentTick % ( 20 * 5 * 60 ) == 0 ) {
                    player.getPlayerConnection().sendTime( this.currentTick );
                }
            }
        }
        this.currentTick++;
    }

    public void addPlayer( Player player ) {
        this.players.put( player.getEntityId(), player );
    }

    public void removePlayer( Player player ) {
        this.players.remove( player.getEntityId() );
    }

    public Collection<Player> getPlayers() {
        return this.players.values();
    }

    public void loadChunk( Chunk chunk ) {
        //TODO Chunk net gefunden in leveldb = generieren
    }

    public Chunk getChunk( int chunkX, int chunkZ ) {
        Long chunkHash = Utils.toLong( chunkX, chunkZ );
        if ( !this.chunkMap.containsKey( chunkHash ) ) {
            Chunk chunk = new Chunk( chunkX, chunkZ );
            for ( int blockX = 0; blockX < 16; blockX++ ) {
                for ( int blockZ = 0; blockZ < 16; blockZ++ ) {
                    chunk.setBlock( blockX, 0, blockZ, 0, BlockType.BEDROCK.getBlock() );
                    chunk.setBlock( blockX, 1, blockZ, 0, BlockType.DIRT.getBlock() );
                    chunk.setBlock( blockX, 2, blockZ, 0, BlockType.DIRT.getBlock() );
                    chunk.setBlock( blockX, 3, blockZ, 0, BlockType.DIRT.<BlockDirt>getBlock().setDirtType( BlockDirt.DirtType.COARSE ) );
                    chunk.setBlock( blockX, 4, blockZ, 0, BlockType.GRASS.getBlock() );
                }
            }
            this.chunkMap.put( chunkHash, chunk ); //TODO this.loadChunk;
            return chunk;
        }
        return this.chunkMap.get( chunkHash );
    }

    public Block getBlock( BlockPosition location ) {
        return this.getBlock( location.toVector(), 0 );
    }

    public Block getBlock( Vector location ) {
        return this.getBlock( location, 0 );
    }

    public Block getBlock( Vector location, int layer ) {
        Chunk chunk = this.getChunk( location.getFloorX() >> 4, location.getFloorZ() >> 4 );
        Block block = chunk.getBlock( location.getFloorX(), location.getFloorY(), location.getFloorZ(), layer );
        block.setWorld( this );
        block.setPosition( new BlockPosition( location.getFloorX(), location.getFloorY(), location.getFloorZ() ) );
        return block;
    }

    public Block getBlockAt( int x, int y, int z ) {
        return this.getBlock( new Vector( x, y, z ), 0 );
    }

    public void setBlock( Vector location, Block block ) {
        this.setBlock( location.toBlockPosition(), block, 0 );
    }

    public void setBlock( BlockPosition location, Block block ) {
        this.setBlock( location, block, 0 );
    }

    public void setBlock( BlockPosition location, Block block, int layer ) {
        Chunk chunk = this.getChunk( location.getX() >> 4, location.getZ() >> 4 );
        chunk.setBlock( location.getX(), location.getY(), location.getZ(), layer, block );

        block.setWorld( this );
        block.setPosition( location );
        block.setLayer( layer );

        UpdateBlockPacket updateBlockPacket = new UpdateBlockPacket();
        updateBlockPacket.setPosition( location );
        updateBlockPacket.setBlockId( block.getRuntimeId() );
        updateBlockPacket.setFlags( UpdateBlockPacket.FLAG_ALL_PRIORITY );
        updateBlockPacket.setLayer( layer );
        for ( Player player : this.getPlayers() ) {
            player.getPlayerConnection().sendPacket( updateBlockPacket );
        }

        if ( block.hasBlockEntity() ) {
            BlockEntity blockEntity = block.getBlockEntity();

            chunk.setBlockEntity( location.getX(), location.getY(), location.getZ(), blockEntity );

            BlockEntityDataPacket blockEntityDataPacket = new BlockEntityDataPacket();
            blockEntityDataPacket.setBlockPosition( block.getPosition() );
            blockEntityDataPacket.setNbt( blockEntity.toCompound().build() );
            for ( Player player : this.getPlayers() ) {
                player.getPlayerConnection().sendPacket( blockEntityDataPacket );
            }
        } else {
            chunk.removeBlockEntity( location.getX(), location.getY(), location.getZ() );
        }
    }

    public void setBlockEntity( BlockPosition location, BlockEntity blockEntity ) {
        Chunk chunk = this.getChunk( location.getX() >> 4, location.getZ() >> 4 );
        chunk.setBlockEntity( location.getX(), location.getY(), location.getZ(), blockEntity );
    }

    public BlockEntity getBlockEntity( BlockPosition location ) {
        Chunk chunk = this.getChunk( location.getX() >> 4, location.getZ() >> 4 );
        return chunk.getBlockEntity( location.getX(), location.getY(), location.getZ() );
    }

    public void playSound( Player player, LevelSound levelSound ) {
        this.playSound( player, player.getLocation(), levelSound, -1, ":", false, false );
    }

    public void playSound( Vector position, LevelSound levelSound, int data ) {
        this.playSound( null, position, levelSound, data, ":", false, false );
    }

    public void playSound( Vector position, LevelSound levelSound, int data, String entityIdentifier ) {
        this.playSound( null, position, levelSound, data, entityIdentifier, false, false );
    }

    public void playSound( Vector position, LevelSound levelSound, int data, String entityIdentifier, boolean isBaby, boolean isGlobal ) {
        this.playSound( null, position, levelSound, data, entityIdentifier, isBaby, isGlobal );
    }

    public void playSound( Player player, Vector position, LevelSound levelSound, int data, String entityIdentifier, boolean isBaby, boolean isGlobal ) {
        LevelSoundEventPacket levelSoundEventPacket = new LevelSoundEventPacket();
        levelSoundEventPacket.setLevelSound( levelSound );
        levelSoundEventPacket.setPosition( position );
        levelSoundEventPacket.setExtraData( data );
        levelSoundEventPacket.setEntityIdentifier( entityIdentifier );
        levelSoundEventPacket.setBabyMob( isBaby );
        levelSoundEventPacket.setGlobal( isGlobal );

        if ( player == null ) {
            this.sendChunkPacket( position.getFloorX() >> 4, position.getFloorZ() >> 4, levelSoundEventPacket );
        } else {
            player.getPlayerConnection().sendPacket( levelSoundEventPacket );
        }
    }

    public void sendChunkPacket( int chunkX, int chunkZ, Packet packet ) {
        for ( Player player : this.getPlayers() ) {
            if ( player != null ) {
                if ( player.getPlayerConnection().knowsChunk( chunkX, chunkZ ) ) {
                    player.getPlayerConnection().sendPacket( packet );
                }
            }
        }
    }

    public int getCurrentTick() {
        return this.currentTick;
    }

    public void sendLevelEvent( Vector position, int eventId, int runtimeId ) {
        this.sendLevelEvent( null, position, eventId, runtimeId );
    }

    public void sendLevelEvent( Player player, Vector position, int eventId, int runtimeId ) {
        LevelEventPacket levelEventPacket = new LevelEventPacket();
        levelEventPacket.setPosition( position );
        levelEventPacket.setEventId( eventId );
        levelEventPacket.setData( runtimeId );

        if ( player != null ) {
            player.getPlayerConnection().sendPacket( levelEventPacket );
        } else {
            this.sendChunkPacket( position.getFloorX() >> 4, position.getFloorZ() >> 4, levelEventPacket );
        }
    }

    public Collection<Entity> getNearbyEntities( AxisAlignedBB bb ) {
        Set targetEntity = new HashSet();

        int minX = (int) Math.floor( ( bb.getMinX() - 2 ) / 16 );
        int maxX = (int) Math.ceil( ( bb.getMaxX() + 2 ) / 16 );
        int minZ = (int) Math.floor( ( bb.getMinZ() - 2 ) / 16 );
        int maxZ = (int) Math.ceil( ( bb.getMaxZ() + 2 ) / 16 );

        for ( int x = minX; x <= maxX; ++x ) {
            for ( int z = minZ; z <= maxZ; ++z ) {
                Chunk chunk = this.getChunk( x, z );
                if ( chunk != null ) {
                    chunk.iterateEntities( entity -> {
                        AxisAlignedBB boundingBox = entity.getBoundingBox();
                        if ( boundingBox.intersectsWith( bb ) ) {
                            targetEntity.add( entity );
                        }
                    } );
                }
            }
        }
        return targetEntity;
    }

    public boolean useItemOn( Player player, BlockPosition blockPosition, BlockPosition placePosition, Vector clickedPosition, BlockFace blockFace ) {
        Block clickedBlock = this.getBlock( blockPosition );
        if ( clickedBlock instanceof BlockAir ) {
            return false;
        }
        Item itemInHand = player.getInventory().getItemInHand();
        Block replacedBlock = this.getBlock( placePosition );
        Block placedBlock = itemInHand.getBlock();
        placedBlock.setWorld( placedBlock.getWorld() );
        placedBlock.setPosition( placePosition );

        boolean interact = false;
        if ( !player.isSneaking() ) {
            interact = clickedBlock.interact( player, blockPosition, clickedPosition, blockFace, itemInHand );
        }

        if ( placedBlock instanceof BlockAir ) {
            return false;
        }
        System.out.println( "V: " + placedBlock.isSolid() );

        if ( ( !interact ) || player.isSneaking() ) {
            if ( !replacedBlock.canBeReplaced() ) {
                return false;
            }

            if ( placedBlock.isSolid() ) {
                Collection<Entity> nearbyEntities = this.getNearbyEntities( placedBlock.getBoundingBox() );
                if ( !nearbyEntities.isEmpty() ) {
                    return false;
                }
            }

            if ( player != null ) {
                if ( placedBlock.isSolid() ) {
                    AxisAlignedBB boundingBox = player.getBoundingBox();
                    if ( placedBlock.getBoundingBox().intersectsWith( boundingBox ) ) {
                        return false;
                    }
                }
            }

            placedBlock.placeBlock( player, this, placePosition, itemInHand, blockFace );
            this.playSound( placePosition.toVector(), LevelSound.PLACE, placedBlock.getRuntimeId() );
        }

        return interact;
    }

    public BlockPosition getSidePosition( BlockPosition blockPosition, BlockFace blockFace ) {
        switch ( blockFace ) {
            case DOWN:
                return this.getRelative( blockPosition, Vector.DOWN );
            case UP:
                return this.getRelative( blockPosition, Vector.UP );
            case NORTH:
                return this.getRelative( blockPosition, Vector.NORTH );
            case SOUTH:
                return this.getRelative( blockPosition, Vector.SOUTH );
            case WEST:
                return this.getRelative( blockPosition, Vector.WEST );
            case EAST:
                return this.getRelative( blockPosition, Vector.EAST );
        }
        return null;
    }

    private BlockPosition getRelative( BlockPosition blockPosition, Vector position ) {
        float x = blockPosition.getX() + position.getX();
        float y = blockPosition.getY() + position.getY();
        float z = blockPosition.getZ() + position.getZ();
        return new Vector( x, y, z ).toBlockPosition();
    }

    public void breakBlock( BlockPosition blockPosition, boolean dropItem ) {
        Block breakBlock = this.getBlock( blockPosition );

        this.playSound( blockPosition.toVector(), LevelSound.BREAK, breakBlock.getRuntimeId() );
        this.sendLevelEvent( blockPosition.toVector(), 2001, breakBlock.getRuntimeId() );
        this.setBlock( blockPosition, new BlockAir() );

        if ( dropItem ) {
            //TODO Drop the item
        }
    }
}
