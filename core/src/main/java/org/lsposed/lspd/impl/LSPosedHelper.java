package org.lsposed.lspd.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.errors.HookFailedError;

public class LSPosedHelper {

    @SuppressWarnings("UnusedReturnValue")
    public static <T> XposedInterface.MethodUnhooker<Method> hookMethod(Class<? extends XposedInterface.Hooker> hooker,
            Class<T> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            var method = clazz.getDeclaredMethod(methodName, parameterTypes);
            return LSPosedBridge.doHook(method, XposedInterface.PRIORITY_DEFAULT, hooker);
        } catch (NoSuchMethodException e) {
            throw new HookFailedError(e);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static <T> Set<XposedInterface.MethodUnhooker<Method>> hookAllMethods(
            Class<? extends XposedInterface.Hooker> hooker, Class<T> clazz, String methodName) {
        var unhooks = new HashSet<XposedInterface.MethodUnhooker<Method>>();
        for (var method : clazz.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                unhooks.add(LSPosedBridge.doHook(method, XposedInterface.PRIORITY_DEFAULT, hooker));
            }
        }
        return unhooks;
    }

    @SuppressWarnings("UnusedReturnValue")
    public static <T> XposedInterface.MethodUnhooker<Constructor<T>> hookConstructor(
            Class<? extends XposedInterface.Hooker> hooker, Class<T> clazz, Class<?>... parameterTypes) {
        try {
            var constructor = clazz.getDeclaredConstructor(parameterTypes);
            return LSPosedBridge.doHook(constructor, XposedInterface.PRIORITY_DEFAULT, hooker);
        } catch (NoSuchMethodException e) {
            throw new HookFailedError(e);
        }
    }

    /**
     * Hook all constructors of a class. Added for Android 16+ compatibility
     * where constructor signatures may change between versions.
     */
    @SuppressWarnings({ "UnusedReturnValue", "unchecked" })
    public static <T> Set<XposedInterface.MethodUnhooker<Constructor<T>>> hookAllConstructors(
            Class<? extends XposedInterface.Hooker> hooker, Class<T> clazz) {
        var unhooks = new HashSet<XposedInterface.MethodUnhooker<Constructor<T>>>();
        for (var constructor : clazz.getDeclaredConstructors()) {
            unhooks.add(LSPosedBridge.doHook((Constructor<T>) constructor, XposedInterface.PRIORITY_DEFAULT, hooker));
        }
        return unhooks;
    }
}
