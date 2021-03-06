/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;

import forestry.api.apiculture.BeeManager;
import forestry.apiculture.gadgets.TileAlvearySwarmer;
import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotCustom;

public class ContainerAlvearySwarmer extends ContainerForestry {

	public ContainerAlvearySwarmer(InventoryPlayer player, TileAlvearySwarmer tile) {
		super(tile);

		this.addSlot(new SlotCustom(tile, 0, 79, 52, getInducerItems()));
		this.addSlot(new SlotCustom(tile, 1, 100, 39, getInducerItems()));
		this.addSlot(new SlotCustom(tile, 2, 58, 39, getInducerItems()));
		this.addSlot(new SlotCustom(tile, 3, 79, 26, getInducerItems()));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(player, j + i * 9 + 9, 8 + j * 18, 87 + i * 18));
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(player, i, 8 + i * 18, 145));

	}

	private Object[] getInducerItems() {
		return BeeManager.inducers.keySet().toArray(new Object[0]);
	}
}
