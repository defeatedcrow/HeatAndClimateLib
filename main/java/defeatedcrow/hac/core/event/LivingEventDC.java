package defeatedcrow.hac.core.event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

import defeatedcrow.hac.api.climate.BlockSet;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.damage.ClimateDamageEvent;
import defeatedcrow.hac.api.damage.ClimateDamageEvent.DamageSet;
import defeatedcrow.hac.api.damage.DamageAPI;
import defeatedcrow.hac.api.damage.DamageSourceClimate;
import defeatedcrow.hac.api.magic.IJewelCharm;
import defeatedcrow.hac.config.CoreConfigDC;
import defeatedcrow.hac.core.ClimateCore;
import defeatedcrow.hac.core.packet.HaCPacket;
import defeatedcrow.hac.core.packet.MessageCharmKey;
import defeatedcrow.hac.core.util.DCTimeHelper;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

// 常時監視系
public class LivingEventDC {

	// ログイン直後は動かない
	private static int count = 20;

	@SubscribeEvent
	public void onEvent(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (living != null) {

			if (count == 0) {
				if (living instanceof EntityPlayer) {
					this.onPlayerUpdate(event);
					if (!living.world.isRemote) {
						this.playerChunkUpdate(event);
						if (CoreConfigDC.sharePotionWithRidingMob) {
							onLivingPotionUpdate(living);
						}
					} else {
						this.onPlayerKeyUpdate(event);
					}
				}
				this.onLivingUpdate(event);
			} else {
				count--;
			}
		}
	}

	public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase living = event.getEntityLiving();
		if (living != null) {
			if (!living.world.isRemote) {

				if (living.isPotionActive(MobEffects.JUMP_BOOST)) {
					living.fallDistance = 0.0F;
				}
				onLivingCharmUpdate(living);

				if (living instanceof EntityPlayer || CoreConfigDC.mobClimateDamage) {
					onLivingClimateUpdate(living);
				}
			}
		}
	}

	public void onLivingPotionUpdate(EntityLivingBase living) {
		/* Potion */
		ArrayList<PotionEffect> potions = new ArrayList<PotionEffect>();

		boolean f = false;
		if (living.isRiding() && !living.getActivePotionMap().isEmpty()) {
			f = true;
			if (living instanceof EntityLiving && ((EntityLiving) living).hasCustomName()) {

			} else {
				if (living instanceof IMob) {
					f = false;
				} else if (living.getRidingEntity() != null && living.getRidingEntity() instanceof IMob) {
					f = false;
				}
			}
		}

		if (f) {
			// PotionEffectのリスト
			Iterator iterator = living.getActivePotionEffects().iterator();

			while (iterator.hasNext()) {
				PotionEffect effect = (PotionEffect) iterator.next();
				Potion potion = effect.getPotion();

				// 騎乗関係のMobにポーション効果を分け与える
				if (living.getRidingEntity() != null && living.getRidingEntity() instanceof EntityLivingBase) {
					EntityLivingBase riding = (EntityLivingBase) living.getRidingEntity();
					if (potion != null) {
						riding.addPotionEffect(effect);
					}
				}
			}
			iterator = null;
		}

	}

	public void onLivingCharmUpdate(EntityLivingBase living) {
		/* Amulet */

		NonNullList<ItemStack> charms = DCUtil.getMobCharm(living);
		for (ItemStack item2 : charms) {
			int m = item2.getMetadata();
			IJewelCharm amu = (IJewelCharm) item2.getItem();
			amu.constantEffect(living, item2);
		}
		charms.clear();
	}

	public void onLivingClimateUpdate(EntityLivingBase living) {
		/* climate damage */

		if (DCTimeHelper.getCount(living.world) == 0 && CoreConfigDC.climateDam) {

			// ピースフルではダメージがない
			if (living.world.getDifficulty() == EnumDifficulty.PEACEFUL && !CoreConfigDC.peacefulDam) {
				return;
			}

			for (Class<? extends Entity> c : CoreConfigDC.blackListEntity) {
				if (c.isInstance(living))
					return;
			}

			IClimate clm = ClimateAPI.calculator.getClimate(living.world, living.getPosition());

			if (CoreConfigDC.heldItem) {
				List<ItemStack> hands = Lists.newArrayList();
				if (!DCUtil.isEmpty(living.getHeldItemMainhand())) {
					hands.add(living.getHeldItemMainhand());
				}
				if (!DCUtil.isEmpty(living.getHeldItemOffhand())) {
					hands.add(living.getHeldItemOffhand());
				}
				for (ItemStack item : hands) {
					if (DCUtil.isEmpty(item))
						continue;

					Block target = null;
					int meta = 0;

					if (item.getItem() instanceof ItemBlock) {
						target = ((ItemBlock) item.getItem()).getBlock();
						meta = item.getItemDamage();
					} else if (item
							.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null) instanceof FluidBucketWrapper) {
						FluidBucketWrapper bucket = (FluidBucketWrapper) item
								.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
						if (bucket.getFluid() != null && bucket.getFluid().getFluid().getBlock() != null) {
							target = bucket.getFluid().getFluid().getBlock();
						}
					}

					if (target == null)
						continue;

					DCHeatTier nT = clm.getHeat();
					DCHumidity nH = clm.getHumidity();
					DCAirflow nA = clm.getAirflow();

					DCHeatTier cT = DCUtil.getBlockTemp(new BlockSet(target, meta), living.world, living.getPosition());
					if (nT != cT) {
						nT = nT.getAverageTemp(cT);
					}

					DCHumidity cH = DCUtil.getBlockHum(new BlockSet(target, meta), living.world, living.getPosition());
					if (nH != cH) {
						nH = nH.getAverageHumidity(cH);
					}

					DCAirflow cA = DCUtil.getBlockAir(new BlockSet(target, meta), living.world, living.getPosition());
					if (nA != cA) {
						nA = nA.getAverageAirflow(cA);
					}

					clm = ClimateAPI.register.getClimateFromParam(nT, nH, nA);

				}
			}

			DCHeatTier heat = clm.getHeat();

			float prevTemp = 2.0F; // normal
			if (living instanceof EntityPlayer) {
				prevTemp = 1.0F * (3 - CoreConfigDC.damageDifficulty); // 1.0F ~ 3.0F
			}
			float damTemp = Math.abs(heat.getTier()) * 1.0F; // hot 0F ~ 8.0F / cold 0F ~ 10.0F
			boolean isCold = heat.getTier() < 0;
			DamageSourceClimate source = isCold ? DamageSourceClimate.climateColdDamage :
					DamageSourceClimate.climateHeatDamage;

			// 基礎ダメージ
			if (isCold) {
				damTemp -= prevTemp;
				damTemp *= 2.0F;
			} else {
				damTemp -= prevTemp;
			}

			// この時点でダメージ無しならスキップ
			if (damTemp >= 1.0F) {
				// 次に装備と耐性計算

				/* damage判定 */
				// mobごとの特性
				prevTemp = DamageAPI.resistantData.getHeatResistant(living, heat);

				// 防具の計算
				if (living.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)) {
					IItemHandler handler = living
							.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);
					if (handler != null) {
						for (int s = 0; s < handler.getSlots(); s++) {
							ItemStack item = handler.getStackInSlot(s);
							if (DCUtil.isEmpty(item))
								continue;

							float p = DCUtil.getItemResistantData(item, isCold);
							prevTemp += p;
						}
					}
				}

				damTemp -= prevTemp;

				ClimateDamageEvent fireEvent = new ClimateDamageEvent(living, source, clm, damTemp);
				DamageSet result = fireEvent.result();
				damTemp = result.damage;
				DamageSource source2 = result.source;

				// 2.0F未満の場合はとどめを刺さない
				if (damTemp < 2.0F && living.getHealth() < 2.0F) {
					damTemp = 0.0F;
				}

				if (damTemp >= 1.0F) {
					living.attackEntityFrom(source2, damTemp);
					if (living instanceof EntityCreature) {
						Vec3d vec = null;
						BlockPos p2 = null;
						if (isCold) {
							p2 = ClimateAPI.calculator.getMaxColdPos(living.world, living.getPosition(), 2);
						} else {
							p2 = ClimateAPI.calculator.getMaxHeatPos(living.world, living.getPosition(), 2);
						}

						if (p2 != null) {
							vec = new Vec3d(p2);
							// 逃げるAIを差し込む
							EntityCreature animal = (EntityCreature) living;
							for (EntityAITaskEntry task : animal.tasks.taskEntries) {
								if (task != null && task.action instanceof EntityAIRunFromHeatsource) {
									EntityAIRunFromHeatsource ai = (EntityAIRunFromHeatsource) task.action;
									ai.avoidPos = vec;
									return;
								}
							}
							animal.tasks.addTask(3, new EntityAIRunFromHeatsource(animal, vec));
						}
					}
				}

			}

			// normal以上は湿度・通気ダメージを受ける
			if (CoreConfigDC.damageDifficulty > 0) {
				float damHum = CoreConfigDC.damageDifficulty;
				float damAir = CoreConfigDC.damageDifficulty;
				float prevHum = DamageAPI.resistantData.getHumResistant(living, clm.getHumidity());
				float prevAir = DamageAPI.resistantData.getAirResistant(living, clm.getAirflow());
				damHum -= prevHum;
				damAir -= prevAir;

				if (prevHum <= 0F && CoreConfigDC.enableHumidity) {
					DamageSourceClimate sourceHum = clm.getHumidity() == DCHumidity.DRY ?
							DamageSourceClimate.climateDryDamage : DamageSourceClimate.climateWaterDamage;
					living.hurtResistantTime = 0;
					living.attackEntityFrom(sourceHum, damHum);
				}

				if (prevAir <= 0F && CoreConfigDC.enableSuffocation) {
					DamageSourceClimate sourceAir = clm.getAirflow() == DCAirflow.TIGHT ?
							DamageSourceClimate.climateSuffocationDamage : DamageSourceClimate.climateWindDamage;
					living.hurtResistantTime = 0;
					living.attackEntityFrom(sourceAir, damAir);
				}
			}
		}
	}

	public void onPlayerUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if ((entity instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) event.getEntity();

			// charm
			if (!player.world.isRemote) {
				NonNullList<ItemStack> charms = DCUtil.getPlayerCharm(player, null);
				for (ItemStack item2 : charms) {
					int m = item2.getMetadata();
					IJewelCharm jew = (IJewelCharm) item2.getItem();
					jew.constantEffect(player, item2);
				}
			}
		}
	}

	private boolean x_key = false;

	@SideOnly(Side.CLIENT)
	public void onPlayerKeyUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if ((entity instanceof EntityPlayer)) {
			EntityPlayer player = (EntityPlayer) event.getEntity();
			if (ClimateCore.proxy.isWarpKeyDown()) {
				if (!x_key) {
					x_key = true;
					HaCPacket.INSTANCE.sendToServer(new MessageCharmKey((byte) 1));
				}
			} else {
				x_key = false;
			}

			ClimateCore.proxy.updatePlayerClimate();
		}
	}

	private int count2 = 20;

	// Block Update をプレイヤーに肩代わりさせる
	public void playerChunkUpdate(LivingEvent.LivingUpdateEvent event) {
		EntityLivingBase entity = event.getEntityLiving();

		if (CoreConfigDC.enableVanilla) {
			if (count2 > 0) {
				count2--;
				return;
			}
			EntityPlayer player = (EntityPlayer) event.getEntity();
			World world = player.world;
			int count = DCTimeHelper.getCount2(world);
			if (count == 0) {
				// DCLogger.debugLog("update tick" + tick);
				// 3x3回やる
				int i = 0;
				while (i < 3) {
					int cx = player.chunkCoordX - 4 + world.rand.nextInt(9);
					int cz = player.chunkCoordZ - 4 + world.rand.nextInt(9);
					if (world.getChunkFromChunkCoords(cx, cz).isLoaded()) {
						int j = 0;
						while (j < 3) {
							int x = (cx << 4) + world.rand.nextInt(16);
							int z = (cz << 4) + world.rand.nextInt(16);
							int y = world.provider.hasSkyLight() ? MathHelper.floor(player.posY) + 5 : world.provider
									.getActualHeight();
							for (int y1 = y; y1 > 1; y1--) {
								BlockPos pos = new BlockPos(x, y1, z);
								if (world.isAirBlock(pos) || world.getBlockState(pos).getMaterial().isLiquid()) {
									continue;
								}
								// 表面のみ
								IBlockState state = world.getBlockState(pos);
								Block block = state.getBlock();
								if (!block.getTickRandomly()) {
									world.scheduleUpdate(pos, block, 200);
								}
								break;
							}
							j++;
						}
					}
					i++;
				}

			}
		}
	}

}
