package com.ferreusveritas.dynamictrees.trees;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.ferreusveritas.dynamictrees.ModBlocks;
import com.ferreusveritas.dynamictrees.VanillaTreeData;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.backport.Biome;
import com.ferreusveritas.dynamictrees.api.backport.BlockPos;
import com.ferreusveritas.dynamictrees.api.backport.IBlockAccess;
import com.ferreusveritas.dynamictrees.api.backport.IBlockState;
import com.ferreusveritas.dynamictrees.api.backport.SpeciesRegistry;
import com.ferreusveritas.dynamictrees.api.backport.World;
import com.ferreusveritas.dynamictrees.genfeatures.GenFeatureVine;
import com.ferreusveritas.dynamictrees.items.Seed;
import com.ferreusveritas.dynamictrees.util.CompatHelper;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary.Type;

public class TreeOak extends DynamicTree {
	
	public class SpeciesOak extends Species {
		
		SpeciesOak(DynamicTree treeFamily) {
			super(treeFamily.getName(), treeFamily);
			
			//Oak trees are about as average as you can get
			setBasicGrowingParameters(0.3f, 12.0f, upProbability, lowestBranchHeight, 0.8f);
			
			envFactor(Type.COLD, 0.75f);
			envFactor(Type.HOT, 0.50f);
			envFactor(Type.DRY, 0.50f);
			envFactor(Type.FOREST, 1.05f);
			
		}
		
		@Override
		public boolean isBiomePerfect(Biome biome) {
			return isOneOfBiomes(biome, BiomeGenBase.forest, BiomeGenBase.forestHills);
		}

		@Override
		public ArrayList<ItemStack> getDrops(IBlockAccess blockAccess, BlockPos pos, int chance, ArrayList<ItemStack> drops) {
			Random rand = blockAccess instanceof World ? ((World)blockAccess).rand : new Random();
			if ((rand.nextInt(chance) == 0)) {
				drops.add(new ItemStack(Items.apple, 1, 0));
			}
			return drops;
		}
		
	}
	
	/**
	 * Swamp Oaks are just Oaks with slight growth differences that can generate in water
	 * and with vines hanging from their leaves.
	 */
	public class SpeciesSwampOak extends Species {
		
		GenFeatureVine vineGen;
		
		SpeciesSwampOak(DynamicTree treeFamily) {
			super(new ResourceLocation(treeFamily.getName().getResourceDomain(), treeFamily.getName().getResourcePath() + "swamp"), treeFamily);
			
			setBasicGrowingParameters(0.3f, 12.0f, upProbability, lowestBranchHeight, 0.8f);
			
			envFactor(Type.COLD, 0.50f);
			envFactor(Type.DRY, 0.50f);
						
			vineGen = new GenFeatureVine(this).setMaxLength(7).setVerSpread(30).setRayDistance(6);
		}
		
		@Override
		public boolean isBiomePerfect(Biome biome) {
			return isOneOfBiomes(biome, BiomeGenBase.swampland);
		}
		
		@Override
		public boolean isAcceptableSoilForWorldgen(World world, BlockPos pos, IBlockState soilBlockState) {
			
			if(soilBlockState.getBlock() == Blocks.water) {
				Biome biome = world.getBiome(pos);
				if(CompatHelper.biomeHasType(biome, Type.SWAMP)) {
					BlockPos down = pos.down();
					if(isAcceptableSoil(world, down, world.getBlockState(down))) {
						return true;
					}
				}
			}
			
			return super.isAcceptableSoilForWorldgen(world, pos, soilBlockState);
		}

		//Swamp Oaks are just oaks in a swamp..  So they have the same drops
		@Override
		public ArrayList<ItemStack> getDrops(IBlockAccess blockAccess, BlockPos pos, int chance, ArrayList<ItemStack> drops) {
			return commonSpecies.getDrops(blockAccess, pos, chance, drops);
		}
		
		//Swamp Oaks are just oaks in a swamp..  So they have the same seeds
		@Override
		public ItemStack getSeedStack(int qty) {
			return commonSpecies.getSeedStack(qty);
		}
		
		//Swamp Oaks are just oaks in a swamp..  So they have the same seeds
		@Override
		public Seed getSeed() {
			return commonSpecies.getSeed();
		}
		
		@Override
		public void postGeneration(World world, BlockPos rootPos, Biome biome, int radius, List<BlockPos> endPoints, boolean worldGen) {
			super.postGeneration(world, rootPos, biome, radius, endPoints, worldGen);
			
			//Generate Vines
			vineGen.setQuantity(5).gen(world, rootPos.up(), endPoints);
		}
	}

	/**
	 * This species drops no seeds at all.  One must craft the seed from an apple.
	 */
	public class SpeciesAppleOak extends Species {

		public SpeciesAppleOak(DynamicTree treeFamily) {
			super(new ResourceLocation(treeFamily.getName().getResourceDomain(), "apple"), treeFamily);
			
			//A bit stockier, smaller and slower than your basic oak
			setBasicGrowingParameters(0.4f, 10.0f, 1, 4, 0.7f);
			
			envFactor(Type.COLD, 0.75f);
			envFactor(Type.HOT, 0.75f);
			envFactor(Type.DRY, 0.25f);
			
		}
		
		@Override
		public boolean isBiomePerfect(Biome biome) {
			return biome.base() == BiomeGenBase.plains;
		}

		@Override
		public ArrayList<ItemStack> getDrops(IBlockAccess blockAccess, BlockPos pos, int chance, ArrayList<ItemStack> drops) {
			return commonSpecies.getDrops(blockAccess, pos, chance, drops);
		}
		
		@Override
		public void postGeneration(World world, BlockPos pos, Biome biome, int radius, List<BlockPos> endPoints, boolean worldGen) {
			super.postGeneration(world, pos, biome, radius, endPoints, worldGen);
			
			// TODO Add Apples
		}
		
	}
	
	Species commonSpecies;
	Species swampSpecies;
	Species appleSpecies;
	
	public TreeOak() {
		super(VanillaTreeData.EnumType.OAK);
	}
	
	@Override
	public void createSpecies() {
		commonSpecies = new SpeciesOak(this);
		swampSpecies = new SpeciesSwampOak(this);
		appleSpecies = new SpeciesAppleOak(this);
	}
	
	@Override
	public void registerSpecies(SpeciesRegistry speciesRegistry) {
		speciesRegistry.register(commonSpecies);
		speciesRegistry.register(swampSpecies);
		speciesRegistry.register(appleSpecies);
	}
	
	@Override
	public Species getCommonSpecies() {
		return commonSpecies;
	}
	
	/**
	 * This will cause the swamp variation of the oak to grow when the player plants
	 * a common oak acorn.
	 */
	@Override
	public Species getSpeciesForLocation(World world, BlockPos pos) {
		if(CompatHelper.biomeHasType(world.getBiome(pos), Type.SWAMP)) {
			return swampSpecies;
		}
		
		return getCommonSpecies();
	}
	
	@Override
	public boolean rot(World world, BlockPos pos, int neighborCount, int radius, Random random) {
		if(super.rot(world, pos, neighborCount, radius, random)) {
			if(radius > 4 && TreeHelper.isRootyDirt(world, pos.down()) && world.getLightFor(EnumSkyBlock.Sky, pos) < 4) {
				world.setBlockState(pos, random.nextInt(3) == 0 ? ModBlocks.blockStates.redMushroom : ModBlocks.blockStates.brownMushroom);//Change branch to a mushroom
				world.setBlockState(pos.down(), ModBlocks.blockStates.podzol);//Change rooty dirt to Podzol
			}
			return true;
		}
		
		return false;
	}
	
}
