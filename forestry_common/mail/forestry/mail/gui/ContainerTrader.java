/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.mail.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

import forestry.core.gui.ContainerForestry;
import forestry.core.gui.slots.SlotClosed;
import forestry.core.gui.slots.SlotCustom;
import forestry.mail.TradeStation;
import forestry.mail.gadgets.MachineTrader;
import forestry.mail.items.ItemStamps;

public class ContainerTrader extends ContainerForestry {

	private final MachineTrader machine;

	public ContainerTrader(InventoryPlayer player, MachineTrader tile) {
		super(tile);

		machine = tile;
		IInventory inventory = machine.getOrCreateTradeInventory();

		// Trade good
		this.addSlot(new Slot(inventory, TradeStation.SLOT_TRADEGOOD, 78, 109));

		// Exchange
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++)
				addSlot(new Slot(inventory, TradeStation.SLOT_EXCHANGE_1 + j + i * 2, 69 + j * 18, 55 + i * 18));

		// Stamps
		for (int i = 0; i < 2; i++)
			for (int j = 0; j < 2; j++)
				addSlot(new SlotCustom(inventory, TradeStation.SLOT_STAMPS_1 + j + i * 2, 15 + j * 18, 37 + i * 18, new Object[] { ItemStamps.class }));

		// Letters
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 2; j++)
				addSlot(new SlotCustom(inventory, TradeStation.SLOT_LETTERS_1 + j + i * 2, 15 + j * 18, 73 + i * 18, new Object[] { Items.paper }));

		// Input buffer
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 2; j++)
				addSlot(new Slot(inventory, TradeStation.SLOT_INPUTBUF_1 + j + i * 2, 123 + j * 18, 19 + i * 18));

		// Output buffer
		for (int i = 0; i < 6; i++)
			for (int j = 0; j < 2; j++)
				addSlot(new SlotClosed(inventory, TradeStation.SLOT_OUTPUTBUF_1 + j + i * 2, 177 + j * 18, 19 + i * 18));

		// Player inventory
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 9; j++)
				addSlot(new Slot(player, j + i * 9 + 9, 33 + j * 18, 138 + i * 18));
		// Player hotbar
		for (int i = 0; i < 9; i++)
			addSlot(new Slot(player, i, 33 + i * 18, 196));

	}

	public boolean isLinked() {
		return getMoniker() != null && !getMoniker().isEmpty();
	}

	public String getMoniker() {
		return machine.getMoniker();
	}

}
