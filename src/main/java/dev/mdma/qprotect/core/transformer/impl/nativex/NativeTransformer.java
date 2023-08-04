package dev.mdma.qprotect.core.transformer.impl.nativex;

import by.radioegor146.*;
import by.radioegor146.instructions.InvokeDynamicHandler;
import by.radioegor146.instructions.MethodHandler;
import by.radioegor146.source.CMakeFilesBuilder;
import by.radioegor146.source.ClassSourceBuilder;
import by.radioegor146.source.MainSourceBuilder;
import by.radioegor146.source.StringPool;
import dev.mdma.qprotect.api.jar.JarFile;
import dev.mdma.qprotect.api.qProtectAPI;
import dev.mdma.qprotect.api.transformer.ClassTransformer;
import dev.mdma.qprotect.api.transformer.TransformException;
import dev.mdma.qprotect.api.utils.BytecodeUtils;
import dev.mdma.qprotect.core.transformer.impl.nativex.classnodes.Bootstrap;

import dev.mdma.qprotect.core.transformer.impl.nativex.classnodes.NativeUtils;
import dev.mdma.qprotect.core.transformer.impl.nativex.classnodes.OS;
import dev.mdma.qprotect.core.transformer.impl.nativex.classnodes.OSUtil;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class NativeTransformer extends ClassTransformer {

    private Snippets snippets;
    private StringPool stringPool;
    private InterfaceStaticClassProvider staticClassProvider;

    private MethodProcessor methodProcessor;

    private NodeCache<String> cachedStrings;
    private NodeCache<String> cachedClasses;
    private NodeCache<CachedMethodInfo> cachedMethods;
    private NodeCache<CachedFieldInfo> cachedFields;

    private StringBuilder nativeMethods;
    private Map<String, InvokeDynamicInsnNode> invokeDynamics;
    private Map<String, MethodInsnNode> methodHandleInvokes;

    private int currentClassId;
    private String nativeDir;

    private CMakeFilesBuilder cMakeBuilder;

    private String projectName;

    private MainSourceBuilder mainSourceBuilder;

    private Path cppOutput;

    private Path cppDir;


    public NativeTransformer() {
        super("Native Transformer", "Converted {} Java classes to C++ classes");
    }

    @Override
    public boolean doInitialization(JarFile jarFile) {
        stringPool = new StringPool();
        snippets = new Snippets(stringPool);
        cachedStrings = new NodeCache<>("(cstrings[%d])");
        cachedClasses = new NodeCache<>("(cclasses[%d])");
        cachedMethods = new NodeCache<>("(cmethods[%d])");
        cachedFields = new NodeCache<>("(cfields[%d])");
        invokeDynamics = new HashMap<>();
        methodHandleInvokes = new HashMap<>();
        methodProcessor = new MethodProcessor(this);

        nativeDir = "native";
        staticClassProvider = new InterfaceStaticClassProvider(nativeDir);
        mainSourceBuilder = new MainSourceBuilder();


        String jarPath = qProtectAPI.Factory.getAPI().getInputFile().getParent();

        cppDir = new File(jarPath, "cpp").toPath();
        cppOutput = cppDir.resolve("output");

        try {
            Files.createDirectories(cppOutput);
            Util.copyResource("sources/native_jvm.cpp", cppDir);
            Util.copyResource("sources/native_jvm.hpp", cppDir);
            Util.copyResource("sources/native_jvm_output.hpp", cppDir);
            Util.copyResource("sources/string_pool.hpp", cppDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        projectName = String.format("native_jvm_classes_%s",
                qProtectAPI.Factory.getAPI().getInputFile().getName().replaceAll("[$#.\\s/]", "_"));

        cMakeBuilder = new CMakeFilesBuilder(projectName);
        cMakeBuilder.addMainFile("native_jvm.hpp");
        cMakeBuilder.addMainFile("native_jvm.cpp");
        cMakeBuilder.addMainFile("native_jvm_output.hpp");
        cMakeBuilder.addMainFile("native_jvm_output.cpp");
        cMakeBuilder.addMainFile("string_pool.hpp");
        cMakeBuilder.addMainFile("string_pool.cpp");

        return true;
    }

    @Override
    public boolean runOnClass(String s, ClassNode classNode, JarFile jarFile) throws TransformException {
        nativeMethods = new StringBuilder();
        Logger.info("Processing " + classNode.name);
        if(classNode.superName.equals("org/bukkit/plugin/java/JavaPlugin") || classNode.superName.equals("net/md_5/bungee/api/plugin/Plugin")) {
            Logger.info("Skipping spigot entrypoint: {}", classNode.name);
            return false;
        }

        if (classNode.methods.stream().noneMatch(x -> x.name.equals("<clinit>"))) {
            classNode.methods.add(new MethodNode(Opcodes.ASM9, Opcodes.ACC_STATIC, "<clinit>", "()V", null,
                    new String[0]));
        }
        staticClassProvider.newClass();

        invokeDynamics.clear();
        methodHandleInvokes.clear();

        cachedStrings.clear();
        cachedClasses.clear();
        cachedMethods.clear();
        cachedFields.clear();

        try (ClassSourceBuilder cppBuilder = new ClassSourceBuilder(cppOutput, classNode.name,
                stringPool)) {
            StringBuilder instructions = new StringBuilder();

            classNode.sourceFile = cppBuilder.getCppFilename();
            for (int i = 0; i < classNode.methods.size(); i++) {
                MethodNode method = classNode.methods.get(i);
                MethodContext context = new MethodContext(this, method, i, classNode, currentClassId);
                methodProcessor.processMethod(context);
                instructions.append(context.output.toString().replace("\n", "\n    "));

                nativeMethods.append(context.nativeMethods);

                if ((classNode.access & Opcodes.ACC_INTERFACE) > 0) {
                    method.access &= ~Opcodes.ACC_NATIVE;
                }
            }

            invokeDynamics.forEach((key, value) -> InvokeDynamicHandler.processIndy(classNode, key, value));
            methodHandleInvokes.forEach(
                    (key, value) -> MethodHandler.processMethodHandleInvoke(classNode, key, value));

            qProtectAPI.Factory.getAPI().getClassPool().removeClass(classNode.name);
            qProtectAPI.Factory.getAPI().getClassPool().addClass(classNode.name, classNode);

            cppBuilder.addHeader(cachedStrings.size(), cachedClasses.size(), cachedMethods.size(),
                    cachedFields.size());
            cppBuilder.addInstructions(instructions.toString());
            cppBuilder.registerMethods(cachedStrings, cachedClasses, nativeMethods.toString(),
                    staticClassProvider);

            cMakeBuilder.addClassFile("output/" + cppBuilder.getHppFilename());
            cMakeBuilder.addClassFile("output/" + cppBuilder.getCppFilename());

            mainSourceBuilder.addHeader(cppBuilder.getHppFilename());
            mainSourceBuilder.registerClassMethods(currentClassId, cppBuilder.getFilename());


            currentClassId++;
        } catch (IOException ex) {
            Logger.error("Error while processing " + classNode.name);
        }

        return false;
    }

    @Override
    public boolean doFinalization(JarFile jarFile) {

        ClassNode loaderClass = new ClassNode();
        loaderClass.sourceFile = "synthetic";
        loaderClass.name = "native/Loader";
        loaderClass.version = 52;
        loaderClass.superName = "java/lang/Object";
        loaderClass.access = Opcodes.ACC_PUBLIC;
        MethodNode registerNativesForClassMethod = new MethodNode(Opcodes.ASM7,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_NATIVE, "registerNativesForClass", "(I)V",
                null, new String[0]);
        loaderClass.methods.add(registerNativesForClassMethod);

        // build classes
        qProtectAPI.Factory.getAPI().getClassPool().addClass(loaderClass.name, loaderClass);
        staticClassProvider.getReadyClasses().forEach(classNode -> qProtectAPI.Factory.getAPI().getClassPool().addClass(classNode.name, classNode));

        try {
            Files.write(cppDir.resolve("string_pool.cpp"), stringPool.build().getBytes(StandardCharsets.UTF_8));
            Files.write(cppDir.resolve("native_jvm_output.cpp"),
                    mainSourceBuilder.build(nativeDir, currentClassId).getBytes(StandardCharsets.UTF_8));
            Files.write(cppDir.resolve("CMakeLists.txt"), cMakeBuilder.build().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final AtomicReference<String> mainClass = new AtomicReference<>();
        AtomicBoolean standalone = new AtomicBoolean(true);
        ArrayList<ClassNode> mainNodes = new ArrayList<>();
        qProtectAPI.Factory.getAPI().getResources().forEach(resourceEntry -> {

            //bukkit/bungee plugin
            if (resourceEntry.getFileName().equals("plugin.yml") || resourceEntry.getFileName().equals("bungee.yml")) {
                standalone.set(false);
                jarFile.getClassPool().getClasses().stream().filter(classNode ->
                        classNode.superName.equals("org/bukkit/plugin/java/JavaPlugin")
                                || classNode.superName.equals("net/md_5/bungee/api/plugin/Plugin")).forEach(
                        classNode -> {
                            Logger.info("Found spigot entrypoint: {}", classNode.name);
                            MethodNode initializer = BytecodeUtils.getOrCreateClinit(classNode);
                            InsnList injectList = new InsnList();
                            injectList.add(new InsnNode(ACONST_NULL));
                            injectList.add(new MethodInsnNode(INVOKESTATIC, "de/brownie/nativeutil/Bootstrap", "main", "([Ljava/lang/String;)V"));
                            initializer.instructions.insertBefore(initializer.instructions.getFirst(), injectList);
                        });
            }

            //forge mod
            if (resourceEntry.getFileName().equals("mcmod.info")) {
                standalone.set(false);
                jarFile.getClassPool().getClasses().forEach(classNode ->
                        classNode.methods.stream().filter(methodNode ->
                                methodNode.desc.equals("(Lnet/minecraftforge/fml/common/event/FMLInitializationEvent;)V")).forEach(
                                methodNode -> {
                                    mainNodes.add(classNode);
                                    Logger.info("Found forge entrypoint: {}", classNode.name);
                                }));
                mainNodes.forEach(classNode -> {
                    MethodNode initializer = BytecodeUtils.getOrCreateClinit(classNode);
                    InsnList injectList = new InsnList();
                    injectList.add(new InsnNode(ACONST_NULL));
                    injectList.add(new MethodInsnNode(INVOKESTATIC, "de/brownie/nativeutil/Bootstrap", "main", "([Ljava/lang/String;)V"));
                    initializer.instructions.insertBefore(initializer.instructions.getFirst(), injectList);
                });
            }

            //fabric mod
            if (resourceEntry.getFileName().equals("fabric.mod.json")) {
                standalone.set(false);
                jarFile.getClassPool().getClasses().stream().filter(classNode ->
                        classNode.interfaces.contains("net/fabricmc/api/ModInitializer")).forEach(
                        classNode -> {
                            Logger.info("Found fabric entrypoint: {}", classNode.name);
                            MethodNode initializer = BytecodeUtils.getOrCreateClinit(classNode);
                            InsnList injectList = new InsnList();
                            injectList.add(new InsnNode(ACONST_NULL));
                            injectList.add(new MethodInsnNode(INVOKESTATIC, "de/brownie/nativeutil/Bootstrap", "main", "([Ljava/lang/String;)V"));
                            initializer.instructions.insertBefore(initializer.instructions.getFirst(), injectList);
                        });
            }

            //standalone program
            if (resourceEntry.getFileName().equals("META-INF/MANIFEST.MF") && standalone.get()) {
                Manifest manifest = null;
                try {
                    manifest = new Manifest(new ByteArrayInputStream(resourceEntry.getContent()));
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }
                if (manifest != null)
                    mainClass.set(manifest.getMainAttributes().getValue("Main-Class"));

                if (mainClass.get() == null) {
                    Logger.warn("No Main-Class found in the MANIFEST.MF aborting Loader creation.");
                    return;
                }

                Logger.info("Found program entrypoint: {}", mainClass);

                //manifest.getMainAttributes().replace("Main-Class", mainClass,"de.brownie.nativeutil.Bootstrap");
                Logger.info("Replacing Main-Class in Manifest...");
                manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "de.brownie.nativeutil.Bootstrap");

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    manifest.write(byteArrayOutputStream);
                } catch (IOException e) {
                    //throw new RuntimeException(e);
                }
                resourceEntry.setContent(byteArrayOutputStream.toByteArray());
            }

            if (mainClass.get() != null)
                mainClass.set(mainClass.get().replace(".", "/"));
        });

        qProtectAPI.Factory.getAPI().getClassPool().addClass("de/brownie/nativeutil/Bootstrap", Bootstrap.getClassNode(mainClass.get(), "/" + projectName));
        qProtectAPI.Factory.getAPI().getClassPool().addClass("de/brownie/nativeutil/NativeUtils", NativeUtils.getClassNode());
        qProtectAPI.Factory.getAPI().getClassPool().addClass("de/brownie/nativeutil/OS", OS.getClassNode());
        qProtectAPI.Factory.getAPI().getClassPool().addClass("de/brownie/nativeutil/OSUtil", OSUtil.getClassNode());
        Logger.info("Loader created!");

        Logger.info("Native files written to {}!", cppDir);

        return true;
    }


    public Snippets getSnippets() {
        return snippets;
    }

    public StringPool getStringPool() {
        return stringPool;
    }

    public InterfaceStaticClassProvider getStaticClassProvider() {
        return staticClassProvider;
    }

    public NodeCache<String> getCachedStrings() {
        return cachedStrings;
    }

    public NodeCache<String> getCachedClasses() {
        return cachedClasses;
    }

    public NodeCache<CachedMethodInfo> getCachedMethods() {
        return cachedMethods;
    }

    public NodeCache<CachedFieldInfo> getCachedFields() {
        return cachedFields;
    }

    public String getNativeDir() {
        return nativeDir;
    }

    public Map<String, InvokeDynamicInsnNode> getInvokeDynamics() {
        return invokeDynamics;
    }

    public Map<String, MethodInsnNode> getMethodHandleInvokes() {
        return methodHandleInvokes;
    }
}