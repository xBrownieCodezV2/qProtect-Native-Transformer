package dev.mdma.qprotect.core.transformer.impl.nativex.classnodes;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

public class OSUtil implements Opcodes {
    public static ClassNode getClassNode() {
        ClassNode classNode = new ClassNode();
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        classWriter.visit(52, ACC_PUBLIC, "de/brownie/nativeutil/OSUtil", null, "java/lang/Object", null);

        FieldVisitor fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_STATIC, "osName", "Ljava/lang/String;", null, null);
        fieldVisitor.visitEnd();

        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitLdcInsn("os.name");
        methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/System", "getProperty", "(Ljava/lang/String;)Ljava/lang/String;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toLowerCase", "()Ljava/lang/String;", false);
        methodVisitor.visitFieldInsn(PUTSTATIC, "de/brownie/nativeutil/OSUtil", "osName", "Ljava/lang/String;");
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitCode();
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "getCurrentOS", "()Lde/brownie/nativeutil/OS;", null, new String[]{"java/io/IOException"});
        methodVisitor.visitCode();
        methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OSUtil", "osName", "Ljava/lang/String;");
        methodVisitor.visitLdcInsn("win");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
        Label label1 = new Label();
        methodVisitor.visitJumpInsn(IFEQ, label1);
        methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OS", "WINDOWS", "Lde/brownie/nativeutil/OS;");
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitLabel(label1);

        methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OSUtil", "osName", "Ljava/lang/String;");
        methodVisitor.visitLdcInsn("mac");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
        Label label2 = new Label();
        methodVisitor.visitJumpInsn(IFEQ, label2);
        methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OS", "MAC", "Lde/brownie/nativeutil/OS;");
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitLabel(label2);

        methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OSUtil", "osName", "Ljava/lang/String;");
        methodVisitor.visitLdcInsn("nix");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
        Label label3 = new Label();
        methodVisitor.visitJumpInsn(IFNE, label3);
        methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OSUtil", "osName", "Ljava/lang/String;");
        methodVisitor.visitLdcInsn("nux");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
        methodVisitor.visitJumpInsn(IFEQ, label3);
        methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OSUtil", "osName", "Ljava/lang/String;");
        methodVisitor.visitLdcInsn("aix");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "contains", "(Ljava/lang/CharSequence;)Z", false);
        Label label4 = new Label();
        methodVisitor.visitJumpInsn(IFEQ, label4);
        methodVisitor.visitLabel(label3);
        methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OS", "UNIX", "Lde/brownie/nativeutil/OS;");
        methodVisitor.visitInsn(ARETURN);
        methodVisitor.visitLabel(label4);

        methodVisitor.visitTypeInsn(NEW, "java/io/IOException");
        methodVisitor.visitInsn(DUP);
        methodVisitor.visitLdcInsn("Unsupported operating system");
        methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/io/IOException", "<init>", "(Ljava/lang/String;)V", false);
        methodVisitor.visitInsn(ATHROW);

        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();

        classWriter.visitEnd();
        classWriter.toByteArray();

        ClassReader classReader = new ClassReader(classWriter.toByteArray());
        classReader.accept(classNode, 0);

        return classNode;
    }
}
