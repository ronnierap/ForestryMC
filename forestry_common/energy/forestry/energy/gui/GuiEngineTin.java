/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.energy.gui;

import net.minecraft.entity.player.InventoryPlayer;

import forestry.core.config.Defaults;
import forestry.core.gui.widgets.SocketWidget;
import forestry.core.utils.EnumTankLevel;
import forestry.core.utils.StringUtil;
import forestry.energy.gadgets.EngineTin;

public class GuiEngineTin extends GuiEngine {

	public GuiEngineTin(InventoryPlayer inventory, EngineTin tile) {
		super(Defaults.TEXTURE_PATH_GUI + "/electricalengine.png", new ContainerEngineTin(inventory, tile), tile);
		widgetManager.add(new SocketWidget(this.widgetManager, 30, 40, tile, 0));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		String name = StringUtil.localize("tile.for.engine.0");
		this.fontRendererObj.drawString(name, getCenteredOffset(name), 6, fontColor.get("gui.title"));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int mouseX, int mouseY) {
		super.drawGuiContainerBackgroundLayer(var1, mouseX, mouseY);
		EngineTin engine = (EngineTin) tile;
		drawHealthMeter(guiLeft + 74, guiTop + 25, engine.getStorageScaled(46), engine.rateLevel(engine.getStorageScaled(100)));

	}

	private void drawHealthMeter(int x, int y, int height, EnumTankLevel rated) {
		int i = 176;
		int k = 0;
		switch (rated) {
		case EMPTY:
			break;
		case LOW:
			i += 4;
			break;
		case MEDIUM:
			i += 8;
			break;
		case HIGH:
			i += 12;
			break;
		case MAXIMUM:
			i += 16;
			break;
		}

		this.drawTexturedModalRect(x, y + 46 - height, i, k + 46 - height, 4, height);
	}

}
