package com.brightspark.sparkshammers.hammerCrafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;

public class SHSlotCrafting extends Slot
{
    /** The craft matrix inventory linked to this result slot. */
    private final InventoryCrafting craftMatrix;
    /** The player that is using the GUI where this slot resides. */
    private final EntityPlayer thePlayer;
    /** The number of items that have been crafted so far. Gets passed to ItemStack.onCrafting before being reset. */
    private int amountCrafted;

    public SHSlotCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory p_i45790_3_, int slotIndex, int xPosition, int yPosition)
    {
        super(p_i45790_3_, slotIndex, xPosition, yPosition);
        this.thePlayer = player;
        this.craftMatrix = craftingInventory;
    }

    /**
     * Check if the stack is a valid item for this slot. Always true beside for the armor slots.
     */
    public boolean isItemValid(ItemStack stack)
    {
        return false;
    }

    /**
     * Decrease the size of the stack in slot (first int arg) by the amount of the second int arg. Returns the new
     * stack.
     */
    public ItemStack decrStackSize(int amount)
    {
        if (this.getHasStack())
        {
            //func_190916_E -> getStackSize
            this.amountCrafted += Math.min(amount, this.getStack().func_190916_E());
        }

        return super.decrStackSize(amount);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood. Typically increases an
     * internal count then calls onCrafting(item).
     */
    protected void onCrafting(ItemStack stack, int amount)
    {
        this.amountCrafted += amount;
        this.onCrafting(stack);
    }

    /**
     * the itemStack passed in is the output - ie, iron ingots, and pickaxes, not ore and wood.
     */
    protected void onCrafting(ItemStack stack)
    {
        if (this.amountCrafted > 0)
        {
            stack.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.amountCrafted);
        }

        this.amountCrafted = 0;
    }

    public void onPickupFromSlot(EntityPlayer playerIn, ItemStack stack)
    {
        net.minecraftforge.fml.common.FMLCommonHandler.instance().firePlayerCraftingEvent(playerIn, stack, craftMatrix);
        this.onCrafting(stack);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(playerIn);
        ItemStack[] aitemstack = HammerCraftingManager.getInstance().func_180303_b(this.craftMatrix, playerIn.worldObj);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);

        for (int i = 0; i < aitemstack.length; ++i)
        {
            ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
            ItemStack itemstack1 = aitemstack[i];

            if (itemstack != null)
            {
                this.craftMatrix.decrStackSize(i, 1);
            }

            if (itemstack1 != null)
            {
                if (this.craftMatrix.getStackInSlot(i) == null)
                {
                    this.craftMatrix.setInventorySlotContents(i, itemstack1);
                }
                else if (!this.thePlayer.inventory.addItemStackToInventory(itemstack1))
                {
                    this.thePlayer.dropItem(itemstack1, false);
                }
            }
        }
    }
}
