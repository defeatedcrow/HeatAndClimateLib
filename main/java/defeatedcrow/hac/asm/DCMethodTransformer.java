package defeatedcrow.hac.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class DCMethodTransformer implements IClassTransformer, Opcodes {

	private static final String TARGET_PACKAGE_1 = "net.minecraft.block.";
	private static final String TARGET_PACKAGE_2 = "net.minecraft.world.World";
	private static final String TARGET_PACKAGE_3 = "net.minecraft.entity.Entity";
	private static final String TARGET_PACKAGE_4 = "net.minecraft.item.Item";
	private static final String TARGET_PACKAGE_5 = "net.minecraft.world.biome.Biome";
	private static final String TARGET_IGNORE1 = "net.minecraft.block.Block";
	// experimental
	private static final String TARGET_PACKAGE_PLUGIN1 = "biomesoplenty.common.block.BlockBOPFarmland";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		if (transformedName.contains(TARGET_PACKAGE_1) || transformedName.contains(TARGET_PACKAGE_PLUGIN1)) {
			try {
				return hookOnUpdateTick(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: Block#updateTick", e);
			}
		} else if (transformedName.contains(TARGET_PACKAGE_2)) {
			try {
				return hookOnBlockFreeze(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: World#canBlockFreezeBody", e);
			}
		} else if (transformedName.contains(TARGET_PACKAGE_3)) {
			try {
				return hookOnEntitySetAir(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: Entity#isInsideOfMaterial", e);
			}
		} else if (transformedName.contains(TARGET_PACKAGE_4)) {
			try {
				return hookOnItemUpdate(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: Item#onEntityItemUpdate", e);
			}
		} else if (transformedName.contains(TARGET_PACKAGE_5)) {
			try {
				return biomeTempUpdate(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: Biome#getTemperature", e);
			}
		} else {
			return basicClass;
		}
	}

	private byte[] hookOnUpdateTick(String className, byte[] bytes) {
		// ASMで、bytesに格納されたクラスファイルを解析します。
		ClassNode cnode = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(cnode, 0);

		// 改変対象のメソッド名です
		String targetMethodName = "updateTick";
		String targetMethodNameSRG = "func_180650_b";

		// 改変対象メソッドの戻り値型および、引数型をあらわします ※１
		String targetMethoddesc = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V";
		String targetMethoddescSRG = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V";

		// 対象のメソッドを検索取得します。
		MethodNode mnode = null;

		String mdesc = null;

		for (MethodNode curMnode : cnode.methods) {

			String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
			String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
			// System.out.println("[ " + mName + " : " + curMnode.name + " ] [ " + mdName + " : " +
			// curMnode.desc);
			if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
					.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
							.equals(mdName))) {
				mnode = curMnode;
				mdesc = curMnode.desc;
				// System.out.println("target found: " + className);
				break;
			}
		}

		if (mnode != null) {

			// System.out.println("try start!");
			InsnList overrideList = new InsnList();
			final LabelNode lavel = new LabelNode();
			String newdesc = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V";

			/*
			 * eventをよぶ
			 * if (DCBlockUpdateEvent.post(new DCBlockUpdateEvent(world, pos, state, rand))){
			 * return;
			 * }
			 * postはキャンセルされた場合のみtrueを返す
			 */
			overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/hook/DCBlockUpdateEvent"));
			overrideList.add(new InsnNode(DUP));
			overrideList.add(new VarInsnNode(ALOAD, 1));
			overrideList.add(new VarInsnNode(ALOAD, 2));
			overrideList.add(new VarInsnNode(ALOAD, 3));
			overrideList.add(new VarInsnNode(ALOAD, 4));
			overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCBlockUpdateEvent", "<init>",
					mdesc, false));
			overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCBlockUpdateEvent", "post",
					"()Z", false));
			overrideList.add(new JumpInsnNode(IFEQ, lavel));
			overrideList.add(new InsnNode(RETURN));
			overrideList.add(lavel);

			mnode.instructions.insert(overrideList);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cnode.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private byte[] hookOnBlockFreeze(String className, byte[] bytes) {
		// ASMで、bytesに格納されたクラスファイルを解析します。
		ClassNode cnode = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(cnode, 0);

		// 改変対象のメソッド名です
		// canBlockFreezeBodyは難読化前後で名前が変わらない模様
		String targetMethodName = "canBlockFreezeBody";
		String targetMethodNameSRG = "canBlockFreezeBody";

		// 改変対象メソッドの戻り値型および、引数型をあらわします ※１
		String targetMethoddesc = "(Lnet/minecraft/util/math/BlockPos;Z)Z";
		String targetMethoddescSRG = "(Lnet/minecraft/util/math/BlockPos;Z)Z";

		// 対象のメソッドを検索取得します。
		MethodNode mnode = null;
		String mdesc = null;

		for (MethodNode curMnode : cnode.methods) {

			String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
			String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
			// System.out.println("[ " + mName + " : " + curMnode.name + " ] [ " + mdName + " : " +
			// curMnode.desc);
			if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
					.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
							.equals(mdName))) {
				mnode = curMnode;
				mdesc = curMnode.desc;
				// System.out.println("target found: " + className);
				break;
			}
		}

		if (mnode != null) {

			// System.out.println("try start!");
			InsnList overrideList = new InsnList();
			final LabelNode lavel = new LabelNode();
			final LabelNode lavel2 = new LabelNode();

			/*
			 * eventをよぶ
			 * if (new DCBlockFreezeEvent(this, pos).result()){
			 * return true;
			 * }
			 * resultはResult == ARROW時のみtrueを返す。
			 * 理想はResult == DENY時にreturn falseを返す処理も入れたかったが断念。
			 */
			overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/hook/DCBlockFreezeEvent"));
			overrideList.add(new InsnNode(DUP));
			overrideList.add(new VarInsnNode(ALOAD, 0));
			overrideList.add(new VarInsnNode(ALOAD, 1));
			overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCBlockFreezeEvent", "<init>",
					"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false));
			overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCBlockFreezeEvent", "result",
					"()Z", false));
			overrideList.add(new JumpInsnNode(IFEQ, lavel));
			overrideList.add(new InsnNode(ICONST_1));
			overrideList.add(new InsnNode(IRETURN));
			overrideList.add(lavel);

			mnode.instructions.insert(overrideList);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cnode.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private byte[] hookOnEntitySetAir(String className, byte[] bytes) {
		// ASMで、bytesに格納されたクラスファイルを解析します。
		ClassNode cnode = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(cnode, 0);

		// 改変対象のメソッド名です
		String targetMethodName = "isInsideOfMaterial";
		String targetMethodNameSRG = "func_70055_a";

		// 改変対象メソッドの戻り値型および、引数型をあらわします ※１
		String targetMethoddesc = "(Lnet/minecraft/block/material/Material;)Z";
		String targetMethoddescSRG = "(Lnet/minecraft/block/material/Material;)Z";

		// 対象のメソッドを検索取得します。
		MethodNode mnode = null;
		String mdesc = null;

		for (MethodNode curMnode : cnode.methods) {

			String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
			String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
			// System.out.println("[ " + mName + " : " + curMnode.name + " ] [ " + mdName + " : " +
			// curMnode.desc);
			if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
					.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
							.equals(mdName))) {
				mnode = curMnode;
				mdesc = curMnode.desc;
				// System.out.println("target found: " + className);
				break;
			}
		}

		if (mnode != null) {

			// System.out.println("try start!");
			InsnList overrideList = new InsnList();
			final LabelNode lavel = new LabelNode();
			final LabelNode lavel2 = new LabelNode();

			/*
			 * eventをよぶ
			 * if (new CamouflageInsideMaterialEvent(this, pos).result()){
			 * return true;
			 * }
			 * resultはResult == ARROW時のみtrueを返す。
			 */
			overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/damage/CamouflageInsideMaterialEvent"));
			overrideList.add(new InsnNode(DUP));
			overrideList.add(new VarInsnNode(ALOAD, 0));
			overrideList.add(new VarInsnNode(ALOAD, 1));
			overrideList.add(new MethodInsnNode(INVOKESPECIAL,
					"defeatedcrow/hac/api/damage/CamouflageInsideMaterialEvent", "<init>",
					"(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/material/Material;)V", false));
			overrideList.add(new MethodInsnNode(INVOKEVIRTUAL,
					"defeatedcrow/hac/api/damage/CamouflageInsideMaterialEvent", "result", "()Z", false));
			overrideList.add(new JumpInsnNode(IFEQ, lavel));
			overrideList.add(new InsnNode(ICONST_1));
			overrideList.add(new InsnNode(IRETURN));
			overrideList.add(lavel);

			mnode.instructions.insert(overrideList);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cnode.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private byte[] hookOnItemUpdate(String className, byte[] bytes) {
		// ASMで、bytesに格納されたクラスファイルを解析します。
		ClassNode cnode = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(cnode, 0);

		// 改変対象のメソッド名です
		String targetMethodName = "onEntityItemUpdate";
		String targetMethodNameSRG = "onEntityItemUpdate";

		// 改変対象メソッドの戻り値型および、引数型をあらわします
		String targetMethoddesc = "(Lnet/minecraft/entity/item/EntityItem;)Z";
		String targetMethoddescSRG = "(Lnet/minecraft/entity/item/EntityItem;)Z";

		// 対象のメソッドを検索取得します。
		MethodNode mnode = null;
		String mdesc = null;

		for (MethodNode curMnode : cnode.methods) {

			String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
			String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
			// System.out.println("[ " + mName + " : " + curMnode.name + " ] [ " + mdName + " : " +
			// curMnode.desc);
			if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
					.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
							.equals(mdName))) {
				mnode = curMnode;
				mdesc = curMnode.desc;
				// System.out.println("target found: " + className);
				break;
			}
		}

		if (mnode != null) {

			// System.out.println("try start!");
			InsnList overrideList = new InsnList();
			final LabelNode lavel = new LabelNode();
			final LabelNode lavel2 = new LabelNode();

			/*
			 * eventをよぶ
			 * if (new DCEntityItemUpdateEvent(entityItem).result()){
			 * return true;
			 * }
			 * resultはResult == ARROW時のみtrueを返す。
			 */
			overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/hook/DCEntityItemUpdateEvent"));
			overrideList.add(new InsnNode(DUP));
			overrideList.add(new VarInsnNode(ALOAD, 1));
			overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCEntityItemUpdateEvent",
					"<init>", "(Lnet/minecraft/entity/item/EntityItem;)V", false));
			overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCEntityItemUpdateEvent",
					"result", "()Z", false));
			overrideList.add(new JumpInsnNode(IFEQ, lavel));
			overrideList.add(new InsnNode(ICONST_1));
			overrideList.add(new InsnNode(IRETURN));
			overrideList.add(lavel);

			mnode.instructions.insert(overrideList);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cnode.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}

	private byte[] biomeTempUpdate(String className, byte[] bytes) {
		// ASMで、bytesに格納されたクラスファイルを解析します。
		ClassNode cnode = new ClassNode();
		ClassReader reader = new ClassReader(bytes);
		reader.accept(cnode, 0);

		// 改変対象のメソッド名です
		String targetMethodName = "getTemperature";
		String targetMethodNameSRG = "func_180626_a";

		// 改変対象メソッドの戻り値型および、引数型をあらわします
		String targetMethoddesc = "(Lnet/minecraft/util/math/BlockPos;)F";
		String targetMethoddescSRG = "(Lnet/minecraft/util/math/BlockPos;)F";

		// 対象のメソッドを検索取得します。
		MethodNode mnode = null;
		String mdesc = null;

		for (MethodNode curMnode : cnode.methods) {

			String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
			String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
			// System.out.println("[ " + mName + " : " + curMnode.name + " ] [ " + mdName + " : " +
			// curMnode.desc);
			if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
					.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
							.equals(mdName))) {
				mnode = curMnode;
				mdesc = curMnode.desc;
				// System.out.println("target found: " + className);
				break;
			}
		}

		if (mnode != null) {

			// System.out.println("try start!");
			InsnList overrideList = new InsnList();
			final LabelNode lavel = new LabelNode();

			/*
			 * eventをよぶ
			 * return new DCBiomeTempEvent(biome, pos).result();
			 * 確定でresult()を返すのでバニラを完全に上書きする
			 */
			overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/hook/DCBiomeTempEvent"));
			overrideList.add(new InsnNode(DUP));
			overrideList.add(new VarInsnNode(ALOAD, 0));
			overrideList.add(new VarInsnNode(ALOAD, 1));
			overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCBiomeTempEvent", "<init>",
					"(Lnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/BlockPos;)V", false));
			overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCBiomeTempEvent", "result",
					"()F", false));
			overrideList.add(new InsnNode(FRETURN));

			mnode.instructions.insert(overrideList);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cnode.accept(cw);
			bytes = cw.toByteArray();
		}

		return bytes;
	}
}
