/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.core.genetics;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

import net.minecraftforge.common.MinecraftForge;

import forestry.api.core.ForestryEvent;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;
import forestry.api.genetics.IBreedingTracker;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.IMutation;
import forestry.core.network.PacketIds;
import forestry.core.network.PacketNBT;
import forestry.core.proxy.Proxies;

public abstract class BreedingTracker extends WorldSavedData implements IBreedingTracker {

	private ArrayList<String> discoveredSpecies = new ArrayList<String>();
	private ArrayList<String> discoveredMutations = new ArrayList<String>();
	private String modeName;

	String username;
	
	public BreedingTracker(String s, String username) {
		super(s);
		this.username = username;
	}

	@Override
	public String getModeName() {
		return modeName;
	}

	@Override
	public void setModeName(String name) {
		this.modeName = name;
		markDirty();
	}

	/**
	 * Returns the common tracker
	 * 
	 * @param player
	 *            used to get worldObj
	 * @return common tracker for this breeding system
	 */
	protected abstract IBreedingTracker getCommonTracker(EntityPlayer player);

	/**
	 * Tag stored in NBT to identify the type of the tracker being synced
	 */
	protected abstract String getPacketTag();

	@Override
	public void synchToPlayer(EntityPlayer player) {
		setModeName(getCommonTracker(player).getModeName());
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		encodeToNBT(nbttagcompound);
		Proxies.net.sendToPlayer(new PacketNBT(PacketIds.GENOME_TRACKER_UPDATE, nbttagcompound), player);
	}

	/* HELPER FUNCTIONS TO PREVENT OBFUSCATION OF INTERFACE METHODS */
	@Override
	public void decodeFromNBT(NBTTagCompound nbttagcompound) {
		readFromNBT(nbttagcompound);
	}

	@Override
	public void encodeToNBT(NBTTagCompound nbttagcompound) {
		writeToNBT(nbttagcompound);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {

		if (nbttagcompound.hasKey("BMS"))
			modeName = nbttagcompound.getString("BMS");

		// / SPECIES
		discoveredSpecies = new ArrayList<String>();
		int count = nbttagcompound.getInteger("SpeciesCount");
		for (int i = 0; i < count; i++)
			discoveredSpecies.add(nbttagcompound.getString("SD" + i));

		// / MUTATIONS
		discoveredMutations = new ArrayList<String>();
		count = nbttagcompound.getInteger("MutationsCount");
		for (int i = 0; i < count; i++)
			discoveredMutations.add(nbttagcompound.getString("MD" + i));

	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {

		if (modeName != null && !modeName.isEmpty())
			nbttagcompound.setString("BMS", modeName);

		nbttagcompound.setString("TYPE", getPacketTag());

		// / SPECIES
		nbttagcompound.setInteger("SpeciesCount", discoveredSpecies.size());
		for (int i = 0; i < discoveredSpecies.size(); i++)
			if (discoveredSpecies.get(i) != null)
				nbttagcompound.setString("SD" + i, discoveredSpecies.get(i));

		// / MUTATIONS
		nbttagcompound.setInteger("MutationsCount", discoveredMutations.size());
		for (int i = 0; i < discoveredMutations.size(); i++)
			if (discoveredMutations.get(i) != null)
				nbttagcompound.setString("MD" + i, discoveredMutations.get(i));

	}

	private static final String MUTATION_FORMAT = "%s-%s=%s";
	@Override
	public void registerMutation(IMutation mutation) {
		discoveredMutations.add(String.format(MUTATION_FORMAT, mutation.getAllele0().getUID(), mutation.getAllele1().getUID(), mutation.getTemplate()[0].getUID()));
		markDirty();
		MinecraftForge.EVENT_BUS.post(new ForestryEvent.MutationDiscovered(
				AlleleManager.alleleRegistry.getSpeciesRoot(this.getPacketTag()), 
				username, 
				mutation, this));
	}
	
	@Override
	public void registerMutation(IAllele allele0, IAllele allele1) {
		discoveredMutations.add(allele0.getUID() + "-" + allele1.getUID());
		markDirty();
	}

	@Override
	public boolean isDiscovered(IMutation mutation) {
		return  discoveredMutations.contains(String.format(MUTATION_FORMAT, mutation.getAllele0().getUID(), mutation.getAllele1().getUID(), mutation.getTemplate()[0].getUID()))
				|| discoveredMutations.contains(mutation.getAllele0().getUID() + "-" + mutation.getAllele1().getUID());
	}

	@Override
	public boolean isDiscovered(IAlleleSpecies species) {
		return discoveredSpecies.contains(species.getUID());
	}

	@Override
	public int getSpeciesBred() {
		return discoveredSpecies.size();
	}

	@Override
	public void registerBirth(IIndividual individual) {
		registerSpecies(individual.getGenome().getPrimary());
		registerSpecies(individual.getGenome().getSecondary());
	}

	@Override
	public void registerSpecies(IAlleleSpecies species) {
		if (!discoveredSpecies.contains(species.getUID())) {
			discoveredSpecies.add(species.getUID());
			MinecraftForge.EVENT_BUS.post(new ForestryEvent.SpeciesDiscovered(
					AlleleManager.alleleRegistry.getSpeciesRoot(this.getPacketTag()), 
					username, 
					species, this));
		}
	}

}
