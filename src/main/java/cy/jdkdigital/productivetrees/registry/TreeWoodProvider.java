package cy.jdkdigital.productivetrees.registry;

import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

interface TreeWoodProvider
{
    Supplier<Block> getLogBlock();

    void setLogBlock(Supplier<Block> logBlock);

    Supplier<Block> getStrippedLogBlock();

    void setStrippedLogBlock(Supplier<Block> strippedLogBlock);

    Supplier<Block> getWoodBlock();

    void setWoodBlock(Supplier<Block> woodBlock);

    Supplier<Block> getStrippedWoodBlock();

    void setStrippedWoodBlock(Supplier<Block> strippedWoodBlock);

    Supplier<Block> getPlankBlock();

    void setPlankBlock(Supplier<Block> plankBlock);

    Supplier<Block> getStairsBlock();

    void setStairsBlock(Supplier<Block> stairsBlock);

    Supplier<Block> getSlabBlock();

    void setSlabBlock(Supplier<Block> slabBlock);

    Supplier<Block> getFenceBlock();

    void setFenceBlock(Supplier<Block> fenceBlock);

    Supplier<Block> getFenceGateBlock();
    
    void setFenceGateBlock(Supplier<Block> fenceGateBlock);

    void setPressurePlateBlock(Supplier<Block> pressurePlate);

    Supplier<Block> getPressurePlateBlock();

    Supplier<Block> getButtonBlock();

    void setButtonBlock(Supplier<Block> buttonBlock);

    Supplier<Block> getHiveBlock();

    void setHiveBlock(Supplier<Block> hiveBlock);

    Supplier<Block> getExpansionBoxBlock();

    void setExpansionBoxBlock(Supplier<Block> expansionBoxBlock);
}
