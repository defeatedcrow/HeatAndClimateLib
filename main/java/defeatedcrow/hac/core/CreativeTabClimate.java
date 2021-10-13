package defeatedcrow.hac.core;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabClimate extends CreativeTabs {

	// クリエイティブタブのアイコン画像や名称の登録クラス
	public CreativeTabClimate(String type) {
		super(type);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return "HeatAndClimate";
	}

	@Override
	public ItemStack getTabIconItem() {
		return new ItemStack(DCInit.climate_checker);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllRelevantItems(NonNullList<ItemStack> list) {
		for (Item item : subItems) {
			item.getSubItems(this, list);
		}
	}

	private List<Item> subItems = Lists.newArrayList();

	public void addSubItem(Item item) {
		if (item != null) {
			item.setCreativeTab(this);
			subItems.add(item);
		}
	}

	public void addSubItem(Block block) {
		if (block != null) {
			block.setCreativeTab(this);
			subItems.add(Item.getItemFromBlock(block));
		}
	}

}
