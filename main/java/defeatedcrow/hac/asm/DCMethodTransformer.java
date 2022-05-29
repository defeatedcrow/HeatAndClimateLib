package defeatedcrow.hac.asm;

import org.apache.logging.log4j.LogManager;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
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
	private static final String TARGET_PACKAGE_6 = "net.minecraft.world.gen.MapGenCaves";
	private static final String TARGET_PACKAGE_7 = "net.minecraft.world.gen.MapGenRavine";
	private static final String TARGET_PACKAGE_8 = "net.minecraft.item.Item";
	private static final String TARGET_PACKAGE_9 = "net.minecraft.item.ItemFood";
	private static final String TARGET_IGNORE1 = "net.minecraft.block.Block";

	public static boolean enableBlockUpdate = true;
	public static boolean enableBlockFreeze = true;
	public static boolean enableEntityInAir = true;
	public static boolean enableDropUpdate = true;
	public static boolean enableBiomeTemp = true;
	public static boolean enableCaveWater = true;
	public static boolean enableRavineWater = true;
	public static boolean enableEatFood = true;

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		if (enableBlockUpdate && transformedName.contains(TARGET_PACKAGE_1)) {
			try {
				return hookOnUpdateTick(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: Block#updateTick", e);
			}
		}
		if (enableBlockFreeze && transformedName.contains(TARGET_PACKAGE_2)) {
			try {
				return hookOnBlockFreeze(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: World#canBlockFreezeBody", e);
			}
		}
		if (enableEntityInAir && transformedName.contains(TARGET_PACKAGE_3)) {
			try {
				return hookOnEntitySetAir(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: Entity#isInsideOfMaterial", e);
			}
		}
		if (enableDropUpdate && transformedName.contains(TARGET_PACKAGE_4)) {
			try {
				return hookOnItemMethod(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: ItemFood#onItemUseFinish", e);
			}
		}
		if (enableBiomeTemp && transformedName.contains(TARGET_PACKAGE_5)) {
			try {
				return biomeTempUpdate(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: Biome#getTemperature", e);
			}
		}
		if (enableCaveWater && transformedName.contains(TARGET_PACKAGE_6)) {
			try {
				return digCaveBlock(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: MapGenCave#digBlock", e);
			}
		}
		if (enableRavineWater && transformedName.contains(TARGET_PACKAGE_7)) {
			try {
				return digRavineBlock(name, basicClass);
			} catch (Exception e) {
				throw new RuntimeException("failed : DCMethodTransformer loading: MapGenRavine#digBlock", e);
			}
		}

		return basicClass;
	}

	private byte[] hookOnUpdateTick(String className, byte[] bytes) {
		try {
			ClassNode cnode = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(cnode, 0);

			String targetMethodName = "updateTick";
			String targetMethodNameSRG = "func_180650_b";

			String targetMethoddesc =
					"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V";
			String targetMethoddescSRG =
					"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V";

			MethodNode mnode = null;

			String mdesc = null;

			for (MethodNode curMnode : cnode.methods) {

				String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
				String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);

				if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
						.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
								.equals(mdName))) {
					mnode = curMnode;
					mdesc = curMnode.desc;
					break;
				}
			}

			if (mnode != null) {

				InsnList overrideList = new InsnList();
				final LabelNode lavel = new LabelNode();
				String newdesc =
						"(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V";

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
				overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCBlockUpdateEvent",
						"<init>", mdesc, false));
				overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCBlockUpdateEvent",
						"post", "()Z", false));
				overrideList.add(new JumpInsnNode(IFEQ, lavel));
				overrideList.add(new InsnNode(RETURN));
				overrideList.add(lavel);

				mnode.instructions.insert(overrideList);

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
				cnode.accept(cw);
				bytes = cw.toByteArray();
			}
		} catch (Exception e) {
			LogManager.getLogger("dcs_asm")
					.fatal("Failed to load DCMethodTransformer:Block#updateTick. It's not work correctly.");
		}
		return bytes;
	}

	private byte[] hookOnBlockFreeze(String className, byte[] bytes) {
		try {
			ClassNode cnode = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(cnode, 0);

			// 改変対象のメソッド名
			// canBlockFreezeBodyは難読化前後で名前が変わらない模様
			String targetMethodName = "canBlockFreezeBody";
			String targetMethodNameSRG = "canBlockFreezeBody";

			String targetMethoddesc = "(Lnet/minecraft/util/math/BlockPos;Z)Z";
			String targetMethoddescSRG = "(Lnet/minecraft/util/math/BlockPos;Z)Z";

			MethodNode mnode = null;
			String mdesc = null;

			for (MethodNode curMnode : cnode.methods) {

				String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
				String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);

				if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
						.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
								.equals(mdName))) {
					mnode = curMnode;
					mdesc = curMnode.desc;
					break;
				}
			}

			if (mnode != null) {

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
				overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCBlockFreezeEvent",
						"<init>", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V", false));
				overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCBlockFreezeEvent",
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
		} catch (Exception e) {
			LogManager.getLogger("dcs_asm")
					.fatal("Failed to load DCMethodTransformer:World#canBlockFreezeBody. It's not work correctly.");
		}
		return bytes;
	}

	private byte[] hookOnEntitySetAir(String className, byte[] bytes) {
		try {
			ClassNode cnode = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(cnode, 0);

			String targetMethodName = "isInsideOfMaterial";
			String targetMethodNameSRG = "func_70055_a";

			String targetMethoddesc = "(Lnet/minecraft/block/material/Material;)Z";
			String targetMethoddescSRG = "(Lnet/minecraft/block/material/Material;)Z";

			MethodNode mnode = null;
			String mdesc = null;

			for (MethodNode curMnode : cnode.methods) {

				String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
				String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);

				if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
						.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
								.equals(mdName))) {
					mnode = curMnode;
					mdesc = curMnode.desc;
					break;
				}
			}

			if (mnode != null) {

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
		} catch (Exception e) {
			LogManager.getLogger("dcs_asm")
					.fatal("Failed to load DCMethodTransformer:Entity#isInsideOfMaterial. It's not work correctly.");
		}
		return bytes;
	}

	private byte[] biomeTempUpdate(String className, byte[] bytes) {
		try {
			ClassNode cnode = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(cnode, 0);

			String targetMethodName = "getTemperature";
			String targetMethodNameSRG = "func_180626_a";

			String targetMethoddesc = "(Lnet/minecraft/util/math/BlockPos;)F";
			String targetMethoddescSRG = "(Lnet/minecraft/util/math/BlockPos;)F";

			MethodNode mnode = null;
			String mdesc = null;

			for (MethodNode curMnode : cnode.methods) {

				String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
				String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
				if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
						.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
								.equals(mdName))) {
					mnode = curMnode;
					mdesc = curMnode.desc;
					break;
				}
			}

			if (mnode != null) {

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
				overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCBiomeTempEvent",
						"<init>", "(Lnet/minecraft/world/biome/Biome;Lnet/minecraft/util/math/BlockPos;)V", false));
				overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCBiomeTempEvent",
						"result", "()F", false));
				overrideList.add(new InsnNode(FRETURN));

				mnode.instructions.insert(overrideList);

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
				cnode.accept(cw);
				bytes = cw.toByteArray();
			}
		} catch (Exception e) {
			LogManager.getLogger("dcs_asm")
					.fatal("Failed to load DCMethodTransformer:Biome#getTemperature. It's not work correctly.");
		}
		return bytes;
	}

	private byte[] digCaveBlock(String className, byte[] bytes) {
		try {
			ClassNode cnode = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(cnode, 0);

			// 改変対象のメソッド名です
			String target1 = "isOceanBlock";
			String target1SRG = "isOceanBlock";

			// 改変対象メソッドの戻り値型および、引数型をあらわします
			String targetDesc1 = "(Layw;IIIII)Z";
			String targetDesc1SRG = "(Lnet/minecraft/world/chunk/ChunkPrimer;IIIII)Z";

			// 対象のメソッドを検索取得します。
			MethodNode mnode1 = null;
			String mdesc1 = null;

			String target2 = "digBlock";
			String target2SRG = "digBlock";

			// 改変対象メソッドの戻り値型および、引数型をあらわします
			String targetDesc2 = "(Layw;IIIIIZLawt;Lawt;)V";
			String targetDesc2SRG =
					"(Lnet/minecraft/world/chunk/ChunkPrimer;IIIIIZLnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;)V";

			// 対象のメソッドを検索取得します。
			MethodNode mnode2 = null;
			String mdesc2 = null;
			String hook = null;
			String hook2 = null;

			for (MethodNode curMnode : cnode.methods) {

				String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
				String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
				// LogManager.getLogger("dcs_asm")
				// .info("[ " + className + " ] [ " + mName + " : " + curMnode.name + " ] [ " + mdName + " : " +
				// curMnode.desc);
				if (target1.equals(curMnode.name) && targetDesc1.equals(curMnode.desc)) {
					mnode1 = curMnode;
					mdesc1 = curMnode.desc;
					hook = "(Layw;III)V";
				} else if (target1SRG.equals(mName) && targetDesc1SRG.equals(mdName)) {
					mnode1 = curMnode;
					mdesc1 = curMnode.desc;
					hook = "(Lnet/minecraft/world/chunk/ChunkPrimer;III)V";
				} else if (target2.equals(curMnode.name) && targetDesc2.equals(curMnode.desc)) {
					mnode2 = curMnode;
					mdesc2 = curMnode.desc;
					hook2 = "(Laza;Layw;IIIIIZLawt;Lawt;)V";
				} else if (target2SRG.equals(mName) && targetDesc2SRG.equals(mdName)) {
					mnode2 = curMnode;
					mdesc2 = curMnode.desc;
					hook2 = "(Lnet/minecraft/world/gen/MapGenCaves;Lnet/minecraft/world/chunk/ChunkPrimer;IIIIIZLnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;)V";
				}
			}

			if (mnode1 != null) {

				// LogManager.getLogger("dcs_asm").info("try start1!");
				InsnList overrideList = new InsnList();
				final LabelNode lavel = new LabelNode();

				/*
				 * eventをよぶ
				 * result()で完全に上書きする
				 */
				overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/hook/DCCaveOceanBlock"));
				overrideList.add(new InsnNode(DUP));
				overrideList.add(new VarInsnNode(ALOAD, 1));
				overrideList.add(new VarInsnNode(ILOAD, 2));
				overrideList.add(new VarInsnNode(ILOAD, 3));
				overrideList.add(new VarInsnNode(ILOAD, 4));
				overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCCaveOceanBlock",
						"<init>", hook, false));
				overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCCaveOceanBlock",
						"result", "()Z", false));
				overrideList.add(new InsnNode(IRETURN));

				mnode1.instructions.insert(overrideList);

			}

			if (mnode2 != null) {

				// LogManager.getLogger("dcs_asm").info("try start2!");
				InsnList overrideList = new InsnList();
				final LabelNode lavel = new LabelNode();

				/*
				 * eventをよぶ
				 * result()がtrueの場合に上書きする
				 */
				overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/hook/DCCaveWaterEvent"));
				overrideList.add(new InsnNode(DUP));
				overrideList.add(new VarInsnNode(ALOAD, 0));
				overrideList.add(new VarInsnNode(ALOAD, 1));
				overrideList.add(new VarInsnNode(ILOAD, 2));
				overrideList.add(new VarInsnNode(ILOAD, 3));
				overrideList.add(new VarInsnNode(ILOAD, 4));
				overrideList.add(new VarInsnNode(ILOAD, 5));
				overrideList.add(new VarInsnNode(ILOAD, 6));
				overrideList.add(new VarInsnNode(ILOAD, 7));
				overrideList.add(new VarInsnNode(ALOAD, 8));
				overrideList.add(new VarInsnNode(ALOAD, 9));
				overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCCaveWaterEvent",
						"<init>", hook2, false));
				overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCCaveWaterEvent",
						"result", "()Z", false));
				overrideList.add(new JumpInsnNode(IFEQ, lavel));
				overrideList.add(new InsnNode(RETURN));
				overrideList.add(lavel);

				mnode2.instructions.insert(overrideList);
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cnode.accept(cw);
			bytes = cw.toByteArray();

		} catch (Exception e) {
			LogManager.getLogger("dcs_asm")
					.fatal("Failed to load DCMethodTransformer:MapGenCave#digBlock. It's not work correctly.");
		}
		return bytes;
	}

	private byte[] digRavineBlock(String className, byte[] bytes) {
		try {
			ClassNode cnode = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(cnode, 0);

			// 改変対象のメソッド名です
			String target1 = "isOceanBlock";
			String target1SRG = "isOceanBlock";

			// 改変対象メソッドの戻り値型および、引数型をあらわします
			String targetDesc1 = "(Layw;IIIII)Z";
			String targetDesc1SRG = "(Lnet/minecraft/world/chunk/ChunkPrimer;IIIII)Z";

			// 対象のメソッドを検索取得します。
			MethodNode mnode1 = null;
			String mdesc1 = null;

			String target2 = "digBlock";
			String target2SRG = "digBlock";

			// 改変対象メソッドの戻り値型および、引数型をあらわします
			String targetDesc2 = "(Layw;IIIIIZ)V";
			String targetDesc2SRG = "(Lnet/minecraft/world/chunk/ChunkPrimer;IIIIIZ)V";

			// 対象のメソッドを検索取得します。
			MethodNode mnode2 = null;
			String mdesc2 = null;
			String hook = null;
			String hook2 = null;

			for (MethodNode curMnode : cnode.methods) {

				String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
				String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
				// LogManager.getLogger("dcs_asm")
				// .info("[ " + className + " ] [ " + mName + " : " + curMnode.name + " ] [ " + mdName + " : " +
				// curMnode.desc);
				if (target1.equals(curMnode.name) && targetDesc1.equals(curMnode.desc)) {
					mnode1 = curMnode;
					mdesc1 = curMnode.desc;
					hook = "(Layw;III)V";
				} else if (target1SRG.equals(mName) && targetDesc1SRG.equals(mdName)) {
					mnode1 = curMnode;
					mdesc1 = curMnode.desc;
					hook = "(Lnet/minecraft/world/chunk/ChunkPrimer;III)V";
				} else if (target2.equals(curMnode.name) && targetDesc2.equals(curMnode.desc)) {
					mnode2 = curMnode;
					mdesc2 = curMnode.desc;
					hook2 = "(Layv;Layw;IIIIIZ)V";
				} else if (target2SRG.equals(mName) && targetDesc2SRG.equals(mdName)) {
					mnode2 = curMnode;
					mdesc2 = curMnode.desc;
					hook2 = "(Lnet/minecraft/world/gen/MapGenRavine;Lnet/minecraft/world/chunk/ChunkPrimer;IIIIIZ)V";
				}
			}

			if (mnode1 != null) {

				// LogManager.getLogger("dcs_asm").info("try start1!");
				InsnList overrideList = new InsnList();
				final LabelNode lavel = new LabelNode();

				/*
				 * eventをよぶ
				 * result()がtrueの場合に上書きする
				 */
				overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/hook/DCCaveOceanBlock"));
				overrideList.add(new InsnNode(DUP));
				overrideList.add(new VarInsnNode(ALOAD, 1));
				overrideList.add(new VarInsnNode(ILOAD, 2));
				overrideList.add(new VarInsnNode(ILOAD, 3));
				overrideList.add(new VarInsnNode(ILOAD, 4));
				overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCCaveOceanBlock",
						"<init>", hook, false));
				overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCCaveOceanBlock",
						"result", "()Z", false));
				overrideList.add(new InsnNode(IRETURN));

				mnode1.instructions.insert(overrideList);
			}

			if (mnode2 != null) {

				// LogManager.getLogger("dcs_asm").info("try start2!");
				InsnList overrideList = new InsnList();
				final LabelNode lavel = new LabelNode();

				/*
				 * eventをよぶ
				 * result()がtrueの場合に上書きする
				 */
				overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/hook/DCRavineWaterEvent"));
				overrideList.add(new InsnNode(DUP));
				overrideList.add(new VarInsnNode(ALOAD, 0));
				overrideList.add(new VarInsnNode(ALOAD, 1));
				overrideList.add(new VarInsnNode(ILOAD, 2));
				overrideList.add(new VarInsnNode(ILOAD, 3));
				overrideList.add(new VarInsnNode(ILOAD, 4));
				overrideList.add(new VarInsnNode(ILOAD, 5));
				overrideList.add(new VarInsnNode(ILOAD, 6));
				overrideList.add(new VarInsnNode(ILOAD, 7));
				overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCRavineWaterEvent",
						"<init>", hook2, false));
				overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCRavineWaterEvent",
						"result", "()Z", false));
				overrideList.add(new JumpInsnNode(IFEQ, lavel));
				overrideList.add(new InsnNode(RETURN));
				overrideList.add(lavel);

				mnode2.instructions.insert(overrideList);
			}

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cnode.accept(cw);
			bytes = cw.toByteArray();

		} catch (Exception e) {
			LogManager.getLogger("dcs_asm")
					.fatal("Failed to load DCMethodTransformer:MapGenRavine#digBlock. It's not work correctly.");
		}
		return bytes;
	}

	private byte[] hookOnItemMethod(String className, byte[] bytes) {
		try {
			ClassNode cnode = new ClassNode();
			ClassReader reader = new ClassReader(bytes);
			reader.accept(cnode, 0);

			// 改変対象のメソッド名
			String targetMethodName = "onItemUseFinish";
			String targetMethodNameSRG = "func_77654_b";

			String targetMethoddesc =
					"(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/item/ItemStack;";
			String targetMethoddescSRG =
					"(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;)Lnet/minecraft/item/ItemStack;";

			MethodNode mnode = null;
			String mdesc = null;

			String targetMethodName2 = "onEntityItemUpdate";
			String targetMethodNameSRG2 = "onEntityItemUpdate";

			String targetMethoddesc2 = "(Lnet/minecraft/entity/item/EntityItem;)Z";
			String targetMethoddescSRG2 = "(Lnet/minecraft/entity/item/EntityItem;)Z";

			MethodNode mnode2 = null;
			String mdesc2 = null;

			for (MethodNode curMnode : cnode.methods) {

				String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
				String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);

				if (className.contains("ItemFood"))
					if ((targetMethodName.equals(curMnode.name) && targetMethoddesc
							.equals(curMnode.desc)) || (targetMethodNameSRG.equals(mName) && targetMethoddescSRG
									.equals(mdName))) {
						mnode = curMnode;
						mdesc = curMnode.desc;
						break;
					}

				if ((targetMethodName2.equals(curMnode.name) && targetMethoddesc2
						.equals(curMnode.desc)) || (targetMethodNameSRG2.equals(mName) && targetMethoddescSRG2
								.equals(mdName))) {
					mnode2 = curMnode;
					mdesc2 = curMnode.desc;
					break;
				}
			}

			if (mnode != null) {

				InsnList overrideList = new InsnList();
				final LabelNode lavel = new LabelNode();
				final LabelNode lavel2 = new LabelNode();

				/*
				 * eventをよぶ
				 * if (new DCItemEatEvent(ItemStack stack, World worldIn, EntityLivingBase livingIn, PotionEffect
				 * potionIn).result()){
				 * return;
				 * }
				 * resultはResult == ARROW時のみtrueを返す。
				 */
				overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/hook/DCItemEatEvent"));
				overrideList.add(new InsnNode(DUP));
				overrideList.add(new VarInsnNode(ALOAD, 1));
				overrideList.add(new VarInsnNode(ALOAD, 2));
				overrideList.add(new VarInsnNode(ALOAD, 3));
				overrideList.add(new VarInsnNode(ALOAD, 0));
				overrideList.add(new FieldInsnNode(GETFIELD, "net/minecraft/item/ItemFood", "potionId",
						"Lnet/minecraft/potion/PotionEffect;"));
				overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/hook/DCItemEatEvent", "<init>",
						"(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/potion/PotionEffect;)V",
						false));
				overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/hook/DCItemEatEvent", "result",
						"()Z", false));
				overrideList.add(new JumpInsnNode(IFEQ, lavel));
				overrideList.add(new InsnNode(ICONST_1));
				overrideList.add(new VarInsnNode(ALOAD, 1));
				overrideList.add(new InsnNode(ARETURN));
				overrideList.add(lavel);

				mnode.instructions.insert(overrideList);

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
				cnode.accept(cw);
				bytes = cw.toByteArray();
			}

			if (mnode2 != null) {

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

				mnode2.instructions.insert(overrideList);

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
				cnode.accept(cw);
				bytes = cw.toByteArray();
			}
		} catch (Exception e) {
			LogManager.getLogger("dcs_asm")
					.fatal("Failed to load DCMethodTransformer:ItemFood#onItemUseFinish. It's not work correctly.");
		}
		return bytes;
	}
}
