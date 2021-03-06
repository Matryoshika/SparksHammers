package brightspark.sparkshammers.integration.jei;

import brightspark.sparkshammers.gui.GuiHammerCraft;
import brightspark.sparkshammers.hammerCrafting.HammerCraftingManager;
import brightspark.sparkshammers.hammerCrafting.HammerShapedOreRecipe;
import brightspark.sparkshammers.init.SHBlocks;
import brightspark.sparkshammers.init.SHItems;
import brightspark.sparkshammers.integration.jei.HammerCraftingTable.HammerCraftingRecipeCategory;
import brightspark.sparkshammers.integration.jei.HammerCraftingTable.HammerCraftingRecipeWrapper;
import brightspark.sparkshammers.item.ItemAOE;
import brightspark.sparkshammers.reference.Reference;
import brightspark.sparkshammers.util.LoaderHelper;
import brightspark.sparkshammers.util.LogHelper;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IIngredientBlacklist;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

@JEIPlugin
public class SparksHammersPlugin implements IModPlugin
{
    public static IJeiHelpers jeiHelper;

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registry)
    {
        jeiHelper = registry.getJeiHelpers();

        registry.addRecipeCategories(new HammerCraftingRecipeCategory(jeiHelper.getGuiHelper()));
    }

    @Override
    public void register(@Nonnull IModRegistry registry)
    {
        LoaderHelper.reloadLocalOreDict();
        registry.handleRecipes(HammerShapedOreRecipe.class, HammerCraftingRecipeWrapper.FACTORY, Reference.JEI.HAMMER_CRAFTING_UID);
        registry.addRecipes(HammerCraftingManager.getValidRecipeList(), Reference.JEI.HAMMER_CRAFTING_UID);
        registry.addRecipeClickArea(GuiHammerCraft.class, 111, 69, 26, 19, Reference.JEI.HAMMER_CRAFTING_UID);
        registry.addRecipeCatalyst(new ItemStack(SHBlocks.blockHammerCraft), Reference.JEI.HAMMER_CRAFTING_UID);

        //Hide tools from JEI if you can't make them!
        IIngredientBlacklist blacklist = jeiHelper.getIngredientBlacklist();
        int count = 0;
        for(ItemAOE tool : SHItems.AOE_TOOLS)
        {
            String oreDic = tool.getDependantOreDic();
            if(oreDic != null && !LoaderHelper.doesOreExist(oreDic))
            {
                blacklist.addIngredientToBlacklist(new ItemStack(tool));
                count++;
            }
        }
        if(count > 0)
            LogHelper.info("Hidden " + count + " tools from JEI due to missing ingredients");
    }
}
