/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.apiculture.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.apiculture.IBee;
import forestry.api.core.Tabs;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IIndividual;
import forestry.apiculture.genetics.Bee;
import forestry.core.config.Config;
import forestry.core.genetics.ItemGE;
import forestry.core.utils.StringUtil;
import forestry.plugins.PluginApiculture;

public class ItemBeeGE extends ItemGE {

	EnumBeeType type;

	public ItemBeeGE(EnumBeeType type) {
		super();
		this.type = type;
		setCreativeTab(Tabs.tabApiculture);
		if (type != EnumBeeType.DRONE)
			setMaxStackSize(1);
	}

	@Override
	protected IIndividual getIndividual(ItemStack itemstack) {
		return new Bee(itemstack.getTagCompound());
	}

	@Override
	protected int getDefaultPrimaryColour() {
		return 0xffffff;
	}

	@Override
	protected int getDefaultSecondaryColour() {
		return 0xffdc16;
	}

	@Override
	public String getItemStackDisplayName(ItemStack itemstack) {

		if (itemstack.getTagCompound() == null)
			return StringUtil.localize(type.getName());

		IBee individual = new Bee(itemstack.getTagCompound());
		return individual.getDisplayName() + StringUtil.localize(type.getName() + ".adj.add") + " " + StringUtil.localize(type.getName());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean flag) {
		if(!itemstack.hasTagCompound())
			return;

		if(type != EnumBeeType.DRONE) {
			IBee individual = new Bee(itemstack.getTagCompound());
			if (individual.isNatural())
				list.add("\u00A7e\u00A7o" + StringUtil.localize("bees.stock.pristine"));
			else
				list.add("\u00A7e" + StringUtil.localize("bees.stock.ignoble"));
		}

		super.addInformation(itemstack, player, list, flag);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List itemList) {
		if (type == EnumBeeType.QUEEN)
			return;

		addCreativeItems(itemList, true);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addCreativeItems(List itemList, boolean hideSecrets) {

		for (IIndividual individual : PluginApiculture.beeInterface.getIndividualTemplates()) {
			// Don't show secret bees unless ordered to.
			if (hideSecrets && individual.isSecret() && !Config.isDebug)
				continue;

			NBTTagCompound nbttagcompound = new NBTTagCompound();
			ItemStack someStack = new ItemStack(this);
			individual.writeToNBT(nbttagcompound);
			someStack.setTagCompound(nbttagcompound);
			itemList.add(someStack);
		}
	}

	/* RENDERING */
	@Override
	public int getColorFromItemStack(ItemStack itemstack, int renderPass) {
		if (!itemstack.hasTagCompound())
			return super.getColorFromItemStack(itemstack, renderPass);

		return getColourFromSpecies(PluginApiculture.beeInterface.getMember(itemstack).getGenome().getPrimary(), renderPass);
	}

	@Override
	public int getColourFromSpecies(IAlleleSpecies species, int renderPass) {

		if (species != null && species instanceof IAlleleBeeSpecies)
			return ((IAlleleBeeSpecies) species).getIconColour(renderPass);
		else
			return 0xffffff;

	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public int getRenderPasses(int metadata) {
		return 3;
	}

	/* ICONS */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister register) {
		for (IAllele allele : AlleleManager.alleleRegistry.getRegisteredAlleles().values())
			if (allele instanceof IAlleleBeeSpecies)
				((IAlleleBeeSpecies) allele).getIconProvider().registerIcons(register);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack itemstack, int renderPass) {
		return getIconFromSpecies(PluginApiculture.beeInterface.getMember(itemstack).getGenome().getPrimary(), renderPass);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconFromSpecies(IAlleleBeeSpecies species, int renderPass) {
		if (species == null)
			species = (IAlleleBeeSpecies) PluginApiculture.beeInterface.getDefaultTemplate()[EnumBeeChromosome.SPECIES.ordinal()];

		return species.getIcon(type, renderPass);
	}
}
