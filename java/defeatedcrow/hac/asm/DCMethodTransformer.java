package defeatedcrow.hac.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

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

public class DCMethodTransformer implements IClassTransformer, Opcodes {

	private static final String TARGET_PACKAGE = "net.minecraft.block.";
	private static final String TARGET_IGNORE = "net.minecraft.block.Block";

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {

		if (!transformedName.contains(TARGET_PACKAGE)) {
			// 処理対象外なので何もしない
			return basicClass;
		}
		try {
			return hookOnUpdateTick(name, basicClass);
		} catch (Exception e) {
			throw new RuntimeException("failed : DCMethodTransformer loading", e);
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

		// 改変対象メソッドの戻り値型および、引数型をあらわします　※１
		String targetMethoddesc = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V";
		String targetMethoddescSRG = "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Ljava/util/Random;)V";

		// 対象のメソッドを検索取得します。
		MethodNode mnode = null;

		String mdesc = null;

		for (MethodNode curMnode : cnode.methods) {

			String mName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(className, curMnode.name, curMnode.desc);
			String mdName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(curMnode.desc);
			// System.out.println("[ " + mName + " : " + curMnode.name + " ]  [ " + mdName + " : " +
			// curMnode.desc);
			if ((targetMethodName.equals(curMnode.name) && targetMethoddesc.equals(curMnode.desc))
					|| (targetMethodNameSRG.equals(mName) && targetMethoddescSRG.equals(mdName))) {
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

			// メソッドコールを、バイトコードであらわした例です。
			overrideList.add(new TypeInsnNode(NEW, "defeatedcrow/hac/api/recipe/DCBlockUpdateEvent"));
			overrideList.add(new InsnNode(DUP));
			overrideList.add(new VarInsnNode(ALOAD, 1));
			overrideList.add(new VarInsnNode(ALOAD, 2));
			overrideList.add(new VarInsnNode(ALOAD, 3));
			overrideList.add(new VarInsnNode(ALOAD, 4));
			overrideList.add(new MethodInsnNode(INVOKESPECIAL, "defeatedcrow/hac/api/recipe/DCBlockUpdateEvent",
					"<init>", mdesc, false));
			overrideList.add(new MethodInsnNode(INVOKEVIRTUAL, "defeatedcrow/hac/api/recipe/DCBlockUpdateEvent",
					"post", "()Z", false));
			overrideList.add(new JumpInsnNode(IFEQ, lavel));
			overrideList.add(new InsnNode(RETURN));
			overrideList.add(lavel);

			// mnode.instructions.get(1)で、対象のメソッドの先頭を取得
			// mnode.instructions.insertで、指定した位置にバイトコードを挿入します。
			mnode.instructions.insert(overrideList);

			// mnode.maxLocals = 4;

			// 改変したクラスファイルをバイト列に書き出します
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
			cnode.accept(cw);
			bytes = cw.toByteArray();

			// System.out.println("bbbb");
		}

		return bytes;
	}
}
