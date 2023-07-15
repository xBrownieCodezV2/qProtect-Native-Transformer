package by.radioegor146;

import by.radioegor146.source.StringPool;
import dev.mdma.qprotect.core.transformer.impl.nativex.NativeTransformer;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.tree.MethodInsnNode;

public class MethodContext {
    public NativeTransformer nativeTransformer;

    public final MethodNode method;
    public final ClassNode clazz;
    public final int methodIndex;
    public final int classIndex;

    public final StringBuilder output;
    public final StringBuilder nativeMethods;

    public Type ret;
    public ArrayList<Type> argTypes;

    public int line;
    public int invokeSpecialId;
    public List<Integer> stack;
    public List<Integer> locals;
    public List<TryCatchBlockNode> tryCatches;

    public MethodNode proxyMethod;
    public MethodNode nativeMethod;

    public MethodContext(NativeTransformer nativeTransformer, MethodNode method, int methodIndex, ClassNode clazz,
                         int classIndex) {
        this.nativeTransformer = nativeTransformer;
        this.method = method;
        this.methodIndex = methodIndex;
        this.clazz = clazz;
        this.classIndex = classIndex;

        this.output = new StringBuilder();
        this.nativeMethods = new StringBuilder();

        this.line = this.invokeSpecialId = -1;
        this.stack = new ArrayList<>();
        this.locals = new ArrayList<>();
        this.tryCatches = new ArrayList<>();
    }

    public NodeCache<String> getCachedStrings() {
        return nativeTransformer.getCachedStrings();
    }

    public NodeCache<String> getCachedClasses() {
        return nativeTransformer.getCachedClasses();
    }

    public NodeCache<CachedMethodInfo> getCachedMethods() {
        return nativeTransformer.getCachedMethods();
    }

    public NodeCache<CachedFieldInfo> getCachedFields() {
        return nativeTransformer.getCachedFields();
    }

    public Snippets getSnippets() {
        return nativeTransformer.getSnippets();
    }

    public StringPool getStringPool() {
        return nativeTransformer.getStringPool();
    }

    public Map<String, InvokeDynamicInsnNode> getInvokeDynamics() {
        return nativeTransformer.getInvokeDynamics();
    }

    public Map<String, MethodInsnNode> getMethodHandleInvokes() {
        return nativeTransformer.getMethodHandleInvokes();
    }
}
