package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

/** A thin leaf block that connects on all six faces (like 3D redstone) so strands bend and fork; model is a hub plus an arm per connected side. */
public class ProductiveBranchLeavesBlock extends ProductiveLeavesBlock
{
    public static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = PipeBlock.PROPERTY_BY_DIRECTION;
    private final int radius;
    private final boolean connectToSelf;
    private final VoxelShape core;
    private final Map<Direction, VoxelShape> arms = new EnumMap<>(Direction.class);

    public ProductiveBranchLeavesBlock(Properties properties, TreeObject treeObject, int radius, boolean connectToSelf) {
        super(properties, treeObject);
        this.radius = radius;
        this.connectToSelf = connectToSelf;
        BlockState state = this.stateDefinition.any();
        for (BooleanProperty property : PROPERTY_BY_DIRECTION.values()) {
            state = state.setValue(property, false);
        }
        this.registerDefaultState(state);
        // collision/selection: a central node and an arm reaching to each connected face
        double lo = 8 - radius;
        double hi = 8 + radius;
        this.core = Block.box(lo, lo, lo, hi, hi, hi);
        this.arms.put(Direction.DOWN, Block.box(lo, 0, lo, hi, lo, hi));
        this.arms.put(Direction.UP, Block.box(lo, hi, lo, hi, 16, hi));
        this.arms.put(Direction.NORTH, Block.box(lo, lo, 0, hi, hi, lo));
        this.arms.put(Direction.SOUTH, Block.box(lo, lo, hi, hi, hi, 16));
        this.arms.put(Direction.WEST, Block.box(0, lo, lo, lo, hi, hi));
        this.arms.put(Direction.EAST, Block.box(hi, lo, lo, 16, hi, hi));
    }

    private boolean canConnectTo(BlockState neighbour) {
        // when connectToSelf is off, don't fuse to another of the same block (keeps thin tips from chaining)
        if (!connectToSelf && neighbour.getBlock() == this) {
            return false;
        }
        // connect to logs and any leaves
        return neighbour.is(BlockTags.LOGS) || neighbour.getBlock() instanceof LeavesBlock;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        LevelReader level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
            state = state.setValue(entry.getValue(), canConnectTo(level.getBlockState(pos.relative(entry.getKey()))));
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos pos, BlockPos neighbourPos) {
        BlockState updated = super.updateShape(state, direction, neighbourState, level, pos, neighbourPos);
        return updated.setValue(PROPERTY_BY_DIRECTION.get(direction), canConnectTo(neighbourState));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        VoxelShape shape = this.core;
        for (Map.Entry<Direction, BooleanProperty> entry : PROPERTY_BY_DIRECTION.entrySet()) {
            if (state.getValue(entry.getValue())) {
                shape = Shapes.or(shape, this.arms.get(entry.getKey()));
            }
        }
        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        PROPERTY_BY_DIRECTION.values().forEach(builder::add);
    }
}
