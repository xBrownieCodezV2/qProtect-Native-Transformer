package dev.mdma.qprotect.core.transformer.impl.nativex.classnodes;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.ClassNode;

public class OS implements Opcodes {

    public static ClassNode getClassNode(){

        ClassWriter classWriter = new ClassWriter(0);
        FieldVisitor fieldVisitor;
        MethodVisitor methodVisitor;

        classWriter.visit(V1_8, ACC_FINAL | ACC_SUPER | ACC_ENUM, "de/brownie/nativeutil/OS", "Ljava/lang/Enum<Lde/brownie/nativeutil/OS;>;", "java/lang/Enum", null);

        {
            fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM, "WINDOWS", "Lde/brownie/nativeutil/OS;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM, "MAC", "Lde/brownie/nativeutil/OS;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PUBLIC | ACC_FINAL | ACC_STATIC | ACC_ENUM, "UNIX", "Lde/brownie/nativeutil/OS;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            fieldVisitor = classWriter.visitField(ACC_PRIVATE | ACC_FINAL | ACC_STATIC | ACC_SYNTHETIC, "$VALUES", "[Lde/brownie/nativeutil/OS;", null, null);
            fieldVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "values", "()[Lde/brownie/nativeutil/OS;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(3, label0);
            methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OS", "$VALUES", "[Lde/brownie/nativeutil/OS;");
            methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "[Lde/brownie/nativeutil/OS;", "clone", "()Ljava/lang/Object;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "[Lde/brownie/nativeutil/OS;");
            methodVisitor.visitInsn(ARETURN);
            methodVisitor.visitMaxs(1, 0);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_STATIC, "valueOf", "(Ljava/lang/String;)Lde/brownie/nativeutil/OS;", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(3, label0);
            methodVisitor.visitLdcInsn(Type.getType("Lde/brownie/nativeutil/OS;"));
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
            methodVisitor.visitTypeInsn(CHECKCAST, "de/brownie/nativeutil/OS");
            methodVisitor.visitInsn(ARETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("name", "Ljava/lang/String;", null, label0, label1, 0);
            methodVisitor.visitMaxs(2, 1);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", "()V", null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(3, label0);
            methodVisitor.visitVarInsn(ALOAD, 0);
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitVarInsn(ILOAD, 2);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false);
            methodVisitor.visitInsn(RETURN);
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLocalVariable("this", "Lde/brownie/nativeutil/OS;", null, label0, label1, 0);
            methodVisitor.visitMaxs(3, 3);
            methodVisitor.visitEnd();
        }
        {
            methodVisitor = classWriter.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
            methodVisitor.visitCode();
            Label label0 = new Label();
            methodVisitor.visitLabel(label0);
            methodVisitor.visitLineNumber(4, label0);
            methodVisitor.visitTypeInsn(NEW, "de/brownie/nativeutil/OS");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn("WINDOWS");
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "de/brownie/nativeutil/OS", "<init>", "(Ljava/lang/String;I)V", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, "de/brownie/nativeutil/OS", "WINDOWS", "Lde/brownie/nativeutil/OS;");
            Label label1 = new Label();
            methodVisitor.visitLabel(label1);
            methodVisitor.visitLineNumber(5, label1);
            methodVisitor.visitTypeInsn(NEW, "de/brownie/nativeutil/OS");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn("MAC");
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "de/brownie/nativeutil/OS", "<init>", "(Ljava/lang/String;I)V", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, "de/brownie/nativeutil/OS", "MAC", "Lde/brownie/nativeutil/OS;");
            Label label2 = new Label();
            methodVisitor.visitLabel(label2);
            methodVisitor.visitLineNumber(6, label2);
            methodVisitor.visitTypeInsn(NEW, "de/brownie/nativeutil/OS");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn("UNIX");
            methodVisitor.visitInsn(ICONST_2);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "de/brownie/nativeutil/OS", "<init>", "(Ljava/lang/String;I)V", false);
            methodVisitor.visitFieldInsn(PUTSTATIC, "de/brownie/nativeutil/OS", "UNIX", "Lde/brownie/nativeutil/OS;");
            Label label3 = new Label();
            methodVisitor.visitLabel(label3);
            methodVisitor.visitLineNumber(3, label3);
            methodVisitor.visitInsn(ICONST_3);
            methodVisitor.visitTypeInsn(ANEWARRAY, "de/brownie/nativeutil/OS");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_0);
            methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OS", "WINDOWS", "Lde/brownie/nativeutil/OS;");
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_1);
            methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OS", "MAC", "Lde/brownie/nativeutil/OS;");
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitInsn(ICONST_2);
            methodVisitor.visitFieldInsn(GETSTATIC, "de/brownie/nativeutil/OS", "UNIX", "Lde/brownie/nativeutil/OS;");
            methodVisitor.visitInsn(AASTORE);
            methodVisitor.visitFieldInsn(PUTSTATIC, "de/brownie/nativeutil/OS", "$VALUES", "[Lde/brownie/nativeutil/OS;");
            methodVisitor.visitInsn(RETURN);
            methodVisitor.visitMaxs(4, 0);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();
        ClassReader classReader = new ClassReader(classWriter.toByteArray());
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        return classNode;
    }
}
