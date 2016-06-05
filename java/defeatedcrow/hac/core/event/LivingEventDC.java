package defeatedcrow.hac.core.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.damage.DamageSourceClimate;
import defeatedcrow.hac.api.magic.CharmType;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.api.recipe.IClimateSmelting;
import defeatedcrow.hac.api.recipe.RecipeAPI;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.DCLogger;
import defeatedcrow.hac.core.util.DCPotion;
import defeatedcrow.hac.core.util.DCTimeHelper;

// AMT式Potion追加効果
public class LivingEventDC {

	@SubscribeEvent
	public void onEvent(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (living instanceof EntityPlayer) {
			this.onPlayerUpdate(event);
			if (!living.worldObj.isRemote) {
				this.playerChunkUpload(event);
			}
		} else {
			this.onLivingUpdate(event);
		}
	}

	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();

		ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>();

		if (living != null && !living.worldObj.isRemote) {

			boolean f = true;
			if (living instanceof EntityLiving && ((EntityLiving) living).hasCustomName()) {

			} else {
				if (living instanceof IMob) {
					f = false;
				} else if (living.getLowestRidingEntity() != null && living.getLowestRidingEntity() instanceof IMob) {
					f = false;
				} else if (living.getRidingEntity() != null && living.getRidingEntity() instanceof IMob) {
					f = false;
				}
			}

			if (f) {
				// PotionEffectのリスト
				Iterator iterator = living.getActivePotionEffects().iterator();

				while (iterator.hasNext()) {
					PotionEffect effect = (PotionEffect) iterator.next();

					Potion potion = effect.getPotion();

					if (potion != null && potion == DCPotion.jump) {
						living.fallDistance = 0.0F;
					}

					// 騎乗関係のMobにポーション効果を分け与える
					if (living.getRidingEntity() != null && living.getRidingEntity() instanceof EntityLivingBase) {
						EntityLivingBase riding = (EntityLivingBase) event.getEntity().getRidingEntity();
						if (potion != null) {
							riding.addPotionEffect(effect);
						}
					}

				}
			}

			// climate damage playerは除外
			if (DCTimeHelper.getCount(living.worldObj) == 0 && !(living instanceof EntityPlayer) && CoreConfigDC.climateDam) {
				int px = MathHelper.floor_double(living.posX);
				int py = MathHelper.floor_double(living.posY) + 1;
				int pz = MathHelper.floor_double(living.posZ);
				DCHeatTier heat = ClimateAPI.calculator.getTemp(living.worldObj, new BlockPos(px, py, pz), 2, false);

				float prev = 1.0F;
				float dam = heat.getTier() * 1.0F;

				Iterable<ItemStack> items = living.getArmorInventoryList();
				if (items != null) {
					for (ItemStack item : items) {
						if (item != null && item.getItem() instanceof ItemArmor) {
							ArmorMaterial mat = ((ItemArmor) item.getItem()).getArmorMaterial();
							prev += DamageAPI.armorRegister.getPreventAmount(mat);
						}
					}
				}

				boolean isCold = false;
				if (dam < 0.0F) {
					isCold = true;
					dam *= -1.0F;
					if (living.isImmuneToFire()) {
						prev -= 1.0F;
					}
				} else {
					if (living.isPotionActive(DCPotion.fire_reg)) {
						prev += 3.0F;
					}
					if (living.isImmuneToFire()) {
						prev += 3.0F;
					}
					if (living instanceof EntityVillager) {
						prev += 2.0F;
					}
				}
				dam -= prev;
				if (dam < 0.0F) {
					dam = 0.0F;
				}

				if (living.getHealth() - dam < 1.0F) {
					dam = living.getHealth() - 1.0F;
				}

				if (dam >= 1.0F) {
					if (isCold) {
						living.attackEntityFrom(DamageSourceClimate.climateColdDamage, dam);
						// DCLogger.debugLog("cold dam:" + dam);
					} else {
						living.attackEntityFrom(DamageSourceClimate.climateHeatDamage, dam);
						// DCLogger.debugLog("heat dam:" + dam);
					}
				}
			}
		}
	}

	public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if ((entity instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			// 装備
			ItemStack[] equip = player.inventory.armorInventory;
			ItemStack[] inside = new ItemStack[9];
			for (int i = 0; i < 9; i++) {
				inside[i] = player.inventory.getStackInSlot(i + 9);
			}

			// charm
			List<ItemStack> charms = new ArrayList<ItemStack>();
			for (ItemStack item1 : inside) {
				if (item1 != null && item1.getItem() != null && item1.getItem() instanceof IJewelCharm) {
					int m = item1.getItemDamage();
					if (((IJewelCharm) item1.getItem()).getType(m) == CharmType.CONSTANT) {
						charms.add(item1);
					}
				}
			}

			if (!player.worldObj.isRemote) {
				float heatPrv = 0.0F;
				float coldPrv = 0.0F;

				for (ItemStack item2 : charms) {
					int m = item2.getItemDamage();
					IJewelCharm jew = (IJewelCharm) item2.getItem();
					jew.formLivingEffect(player, item2);
					heatPrv += jew.reduceHeat(m);
					coldPrv += jew.reduceCold(m);
				}

				// climate damage
				if (DCTimeHelper.getCount(player.worldObj) == 0 && CoreConfigDC.climateDam && !player.capabilities.disableDamage) {
					int px = MathHelper.floor_double(player.posX);
					int py = MathHelper.floor_double(player.posY) + 1;
					int pz = MathHelper.floor_double(player.posZ);
					DCHeatTier heat = ClimateAPI.calculator.getTemp(player.worldObj, new BlockPos(px, py, pz), 2, false);

					float prev = 1.0F * (1 - CoreConfigDC.damageDifficulty);
					float dam = heat.getTier() * 1.0F;

					for (ItemStack item : equip) {
						if (item != null && item.getItem() instanceof ItemArmor) {
							ArmorMaterial mat = ((ItemArmor) item.getItem()).getArmorMaterial();
							float p = DamageAPI.armorRegister.getPreventAmount(mat);
							prev += p;
						}
					}

					boolean isCold = false;
					if (dam < 0.0F) {
						isCold = true;
						dam *= -1.0F;
						prev += coldPrv;
					} else {
						prev += heatPrv;
						if (player.isPotionActive(DCPotion.fire_reg)) {
							prev += 3.0F;
						}
					}
					dam -= prev;
					if (dam < 0.0F) {
						dam = 0.0F;
					}

					if (player.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && !CoreConfigDC.peacefulDam) {
						dam = 0.0F;
					}

					if (CoreConfigDC.damageDifficulty < 2 && player.getHealth() - dam < 1.0F) {
						dam = player.getHealth() - 1.0F;
					}

					if (dam >= 1.0F) {
						if (isCold) {
							player.attackEntityFrom(DamageSourceClimate.climateColdDamage, dam);
							DCLogger.debugLog("player cold dam:" + dam);
						} else {
							player.attackEntityFrom(DamageSourceClimate.climateHeatDamage, dam);
							DCLogger.debugLog("player heat dam:" + dam);
						}
					}

				}
			} else {

			}

		}
	}

	private int localCount = 0;

	// Block Update をプレイヤーに肩代わりさせる
	public void playerChunkUpload(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if ((entity instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			World world = player.worldObj;
			int count = DCTimeHelper.getCount2(world);

			int tick = (count >> 4) & 15;

			if (tick != localCount) {
				localCount = tick;
				// 3回やる
				int i = 0;
				while (i < CoreConfigDC.updateFrequency) {
					int cx = player.chunkCoordX - 4 + world.rand.nextInt(9);
					int cz = player.chunkCoordZ - 4 + world.rand.nextInt(9);
					if (world.getChunkFromChunkCoords(cx, cz).isLoaded()) {
						int j = 0;
						while (j < CoreConfigDC.updateFrequency) {
							int x = (cx << 4) + world.rand.nextInt(16);
							int z = (cz << 4) + world.rand.nextInt(16);
							int y = world.provider.getActualHeight();

							BlockPos under = new BlockPos(x, 1, z);
							BlockPos upper = new BlockPos(x, y, z);
							Iterable<BlockPos> itr = under.getAllInBox(under, upper);
							for (BlockPos pos : itr) {
								if (world.rand.nextBoolean())
									continue;
								if (world.isAirBlock(pos)) {
									continue;
								}
								IBlockState state = world.getBlockState(pos);
								Block block = state.getBlock();
								int meta = block.getMetaFromState(state);
								IClimate clm = ClimateAPI.calculator.getClimate(world, pos, new int[] {
										2,
										1,
										1 });
								IClimateSmelting recipe = RecipeAPI.registerSmelting.getRecipe(clm, new ItemStack(block, 1, meta));
								if (recipe == null || !recipe.matchClimate(clm) || recipe.hasPlaceableOutput() != 1)
									continue;

								if (recipe.getOutput() != null && recipe.getOutput().getItem() instanceof ItemBlock) {
									Block retB = Block.getBlockFromItem(recipe.getOutput().getItem());
									int retM = recipe.getOutput().getItemDamage();
									IBlockState ret = retB.getStateFromMeta(retM);
									world.setBlockState(pos, ret, 3);
								}
							}
							j++;
						}
					}
					i++;
				}

			}
		}
	}

	@SubscribeEvent
	public void onHurt(LivingHurtEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		DamageSource source = event.getSource();
		float newDam = event.getAmount();
		if (living != null && living instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) living;
			boolean hasCharm = false;
			List<ItemStack> charms = new ArrayList<ItemStack>();
			for (int i = 9; i < 18; i++) {
				ItemStack check = player.inventory.getStackInSlot(i);
				if (check != null && check.getItem() != null && check.getItem() instanceof IJewelCharm) {
					IJewelCharm charm = (IJewelCharm) check.getItem();
					int m = check.getItemDamage();
					if (charm.getType(m) == CharmType.DEFFENCE)
						charms.add(check);
				}
			}

			float red = 0.0F;
			for (ItemStack charm : charms) {
				red += ((IJewelCharm) charm.getItem()).reduceDamage(source, charm);
			}

			newDam -= red;
			if (newDam <= 0.0F) {
				event.setCanceled(true);
			} else {
				event.setAmount(newDam);
			}
		}
	}
}
