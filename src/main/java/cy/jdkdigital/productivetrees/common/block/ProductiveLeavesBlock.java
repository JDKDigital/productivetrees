package cy.jdkdigital.productivetrees.common.block;

import cy.jdkdigital.productivelib.util.ColorUtil;
import cy.jdkdigital.productivetrees.registry.ClientRegistration;
import cy.jdkdigital.productivetrees.registry.TreeObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public class ProductiveLeavesBlock extends LeavesBlock
{
    protected final TreeObject treeObject;

    public ProductiveLeavesBlock(Properties properties, TreeObject treeObject) {
        super(properties);
        this.treeObject = treeObject;
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand) {
        super.animateTick(state, level, pos, rand);
        if (treeObject.hasFallingLeaves() && rand.nextInt(10) == 0) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (!isFaceFull(blockstate.getCollisionShape(level, blockpos), Direction.UP)) {
                var particle = ClientRegistration.PETAL_PARTICLES.get();
                particle.setColor(ColorUtil.getCacheColor(TextColor.parseColor(treeObject.getLeafColor()).getValue()));
                ParticleUtils.spawnParticleBelow(level, pos, rand, particle);
            }
        }
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        // TODO remove before launch
        if (pPlayer.getItemInHand(pHand).is(Items.BONE_MEAL) && pState.getBlock() instanceof ProductiveLeavesBlock leaf) {
            if (leaf.getTree().hasFruit()) {
                pLevel.setBlockAndUpdate(pPos, leaf.getTree().getFruitBlock().get().defaultBlockState().setValue(BlockStateProperties.PERSISTENT, true));
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    public TreeObject getTree() {
        return treeObject;
    }
}
