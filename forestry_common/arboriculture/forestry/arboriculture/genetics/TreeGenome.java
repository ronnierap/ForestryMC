/*******************************************************************************
 * Copyright 2011-2014 by SirSengir
 * 
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * 
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/.
 ******************************************************************************/
package forestry.arboriculture.genetics;

import java.util.EnumSet;

import net.minecraft.nbt.NBTTagCompound;

import net.minecraftforge.common.EnumPlantType;

import forestry.api.arboriculture.EnumTreeChromosome;
import forestry.api.arboriculture.IAlleleFruit;
import forestry.api.arboriculture.IAlleleGrowth;
import forestry.api.arboriculture.IAlleleLeafEffect;
import forestry.api.arboriculture.IAlleleTreeSpecies;
import forestry.api.arboriculture.IFruitProvider;
import forestry.api.arboriculture.IGrowthProvider;
import forestry.api.arboriculture.ITreeGenome;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleFloat;
import forestry.api.genetics.IAlleleInteger;
import forestry.api.genetics.IChromosome;
import forestry.api.genetics.ISpeciesRoot;
import forestry.core.genetics.Allele;
import forestry.core.genetics.AllelePlantType;
import forestry.core.genetics.Chromosome;
import forestry.core.genetics.Genome;
import forestry.plugins.PluginArboriculture;

public class TreeGenome extends Genome implements ITreeGenome {

	public TreeGenome(IChromosome[] chromosomes) {
		super(chromosomes);
	}

	public TreeGenome(NBTTagCompound nbttagcompound) {
		super(nbttagcompound);
	}

	@Override
	public IAlleleTreeSpecies getPrimary() {
		return (IAlleleTreeSpecies) getActiveAllele(EnumTreeChromosome.SPECIES.ordinal());
	}

	@Override
	public IAlleleTreeSpecies getSecondary() {
		return (IAlleleTreeSpecies) getInactiveAllele(EnumTreeChromosome.SPECIES.ordinal());
	}

	@Override
	public IFruitProvider getFruitProvider() {
		return ((IAlleleFruit) getActiveAllele(EnumTreeChromosome.FRUITS.ordinal())).getProvider();
	}

	@Override
	public IGrowthProvider getGrowthProvider() {
		return ((IAlleleGrowth) getActiveAllele(EnumTreeChromosome.GROWTH.ordinal())).getProvider();
	}

	@Override
	public float getHeight() {
		return ((IAlleleFloat) getActiveAllele(EnumTreeChromosome.HEIGHT.ordinal())).getValue();
	}

	@Override
	public float getFertility() {
		return ((IAlleleFloat) getActiveAllele(EnumTreeChromosome.FERTILITY.ordinal())).getValue();
	}

	@Override
	public float getYield() {
		return ((IAlleleFloat) getActiveAllele(EnumTreeChromosome.YIELD.ordinal())).getValue();
	}

	@Override
	public float getSappiness() {
		// FIXME: Legacy handling.
		if (getChromosomes()[EnumTreeChromosome.SAPPINESS.ordinal()] == null)
			getChromosomes()[EnumTreeChromosome.SAPPINESS.ordinal()] = new Chromosome(Allele.sappinessLowest);

		IAllele allele = getActiveAllele(EnumTreeChromosome.SAPPINESS.ordinal());
		// FIXME: More legacy handling
		if (allele instanceof IAlleleFloat)
			return ((IAlleleFloat) allele).getValue();
		else {
			getChromosomes()[EnumTreeChromosome.SAPPINESS.ordinal()] = new Chromosome(Allele.sappinessLowest);
			return 0.1f;
		}
	}

	@Override
	public EnumSet<EnumPlantType> getPlantTypes() {
		// / FIXME: Needs some legacy handling.
		if (!(getActiveAllele(EnumTreeChromosome.PLANT.ordinal()) instanceof AllelePlantType))
			getChromosomes()[EnumTreeChromosome.PLANT.ordinal()] = new Chromosome(Allele.plantTypeNone);

		return ((AllelePlantType) getActiveAllele(EnumTreeChromosome.PLANT.ordinal())).getPlantTypes();
	}

	@Override
	public int getMaturationTime() {
		if (getChromosomes()[EnumTreeChromosome.MATURATION.ordinal()] == null)
			getChromosomes()[EnumTreeChromosome.MATURATION.ordinal()] = new Chromosome(Allele.maturationSlowest);

		return ((IAlleleInteger) getActiveAllele(EnumTreeChromosome.MATURATION.ordinal())).getValue();
	}

	private IAllele translateGirth(int girth) {
		switch (girth) {
		case 2:
			return Allele.int2;
		case 3:
			return Allele.int3;
		default:
			return Allele.int1;
		}
	}

	@Override
	public int getGirth() {
		if (getChromosomes()[EnumTreeChromosome.GIRTH.ordinal()] == null)
			getChromosomes()[EnumTreeChromosome.GIRTH.ordinal()] = new Chromosome(translateGirth(getPrimary().getGirth()),
					translateGirth(getSecondary().getGirth()));

		return ((IAlleleInteger) getActiveAllele(EnumTreeChromosome.GIRTH.ordinal())).getValue();
	}
	
	@Override
	public IAlleleLeafEffect getEffect() {
		return (IAlleleLeafEffect) getActiveAllele(EnumTreeChromosome.EFFECT.ordinal());
	}

	@Override
	public ISpeciesRoot getSpeciesRoot() {
		return PluginArboriculture.treeInterface;
	}

}
