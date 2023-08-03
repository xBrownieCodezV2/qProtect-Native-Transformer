package dev.mdma.qprotect.core.transformer.impl.nativex.classnodes;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

public class Bootstrap implements Opcodes {
   public static ClassNode getClassNode(String mainClass, String libName) {
      ClassNode classNode = new ClassNode(Opcodes.ASM8);
      classNode.version = Opcodes.V1_8;
      classNode.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER;
      classNode.name = "de/brownie/nativeutil/Bootstrap";
      classNode.superName = "java/lang/Object";

      MethodNode initMethodNode = new MethodNode(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
      initMethodNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
      initMethodNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false));
      initMethodNode.instructions.add(new InsnNode(Opcodes.RETURN));
      classNode.methods.add(initMethodNode);

      MethodNode mainMethodNode = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "main", "([Ljava/lang/String;)V", null, new String[]{"java/lang/Exception"});
      mainMethodNode.instructions.add(new LdcInsnNode(libName));
      mainMethodNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "de/brownie/nativeutil/NativeUtils", "loadLibraryFromJar", "(Ljava/lang/String;)V", false));

      //check if we have a standalone application or forge mod
      if(mainClass != null) {
         mainMethodNode.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
         mainMethodNode.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, mainClass, "main", "([Ljava/lang/String;)V", false));
      }
      mainMethodNode.instructions.add(new InsnNode(Opcodes.RETURN));
      classNode.methods.add(mainMethodNode);

      classNode.visitEnd();

      return classNode;
   }
}
