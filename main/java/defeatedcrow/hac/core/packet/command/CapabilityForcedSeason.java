package defeatedcrow.hac.core.packet.command;

import java.util.concurrent.Callable;

import defeatedcrow.hac.api.climate.EnumSeason;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityForcedSeason {

	private CapabilityForcedSeason() {}

	public static final CapabilityForcedSeason INSTANCE = new CapabilityForcedSeason();

	@CapabilityInject(IForcedSeason.class)
	public static Capability<IForcedSeason> FORCED_SEASON_CAPABILITY = null;

	public static void register() {
		CapabilityManager.INSTANCE.register(IForcedSeason.class, INSTANCE.new StorageDC(), INSTANCE.new CallableDC());
	}

	public class StorageDC implements Capability.IStorage<IForcedSeason> {

		@Override
		public NBTBase writeNBT(Capability<IForcedSeason> capability, IForcedSeason instance, EnumFacing side) {
			NBTTagCompound nbt = new NBTTagCompound();
			EnumSeason season = instance.getSeason();
			boolean forced = instance.isForced();

			if (season != null) {
				nbt.setByte("dcs.season", (byte) season.id);
			} else if (nbt.hasKey("dcs.season")) {
				nbt.removeTag("dcs.season");
			}
			nbt.setBoolean("dcs.season.flag", forced);

			return nbt;
		}

		@Override
		public void readNBT(Capability<IForcedSeason> capability, IForcedSeason instance, EnumFacing side,
				NBTBase nbt) {
			NBTTagCompound tag = (NBTTagCompound) nbt;
			byte i = 0;
			if (tag.hasKey("dcs.season")) {
				i = tag.getByte("dcs.season");
			}
			EnumSeason season = EnumSeason.getSeasonFromID(i);
			boolean f = tag.getBoolean("dcs.season.flag");
			instance.setForcedSeason(season);
			instance.setForced(f);
		}

	}

	public class CallableDC implements Callable<IForcedSeason> {

		@Override
		public IForcedSeason call() throws Exception {
			return new ForcedSeason();
		}

	}

}
