package com.georgev22.disguise;

import com.google.common.collect.Maps;
import li.cock.ie.reflect.DuckBypass;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;

import java.lang.reflect.*;
import java.util.Map;
import java.util.Objects;

public class ReflectionUtil {

    public static String serverVersion = null;
    private static final DuckBypass reflect;

    static {
        reflect = new DuckBypass();
        try {
            Class.forName("org.bukkit.Bukkit");
            String version = Bukkit.getServer().getClass().getPackage().getName().substring(Bukkit.getServer().getClass().getPackage().getName().lastIndexOf('.') + 1);
            setObject(ReflectionUtil.class, null, "serverVersion", version);
        } catch (Exception ignored) {
        }
    }

    /*
     * The server version string to location NMS & OBC classes
     */
    private static String versionString;

    /*
     * Cache of NMS classes that we've searched for
     */
    private static final Map<String, Class<?>> loadedNMSClasses = Maps.newHashMap();

    /*
     * Cache of OBS classes that we've searched for
     */
    private static final Map<String, Class<?>> loadedOBCClasses = Maps.newHashMap();

    /*
     * Cache of methods that we've found in particular classes
     */
    private static final Map<Class<?>, Map<String, Method>> loadedMethods = Maps.newHashMap();

    /*
     * Cache of fields that we've found in particular classes
     */
    private static final Map<Class<?>, Map<String, Field>> loadedFields = Maps.newHashMap();

    /**
     * Gets the version string for NMS & OBC class paths
     *
     * @return The version string of OBC and NMS packages
     */
    public static String getVersion() {
        if (versionString == null) {
            String name = Bukkit.getServer().getClass().getPackage().getName();
            versionString = name.substring(name.lastIndexOf('.') + 1);
        }

        return versionString;
    }

    public static Object invokeMethod(Object object, Method method, Object... params) {
        Validate.notNull(method, "method cannot be null");
        Validate.isTrue(object != null || Modifier.isStatic(method.getModifiers()), "object cannot be null");
        Validate.notNull(params, "params cannot be null");

        try {
            return (method.invoke(object, params));
        } catch (InvocationTargetException | IllegalAccessException var4) {
            var4.printStackTrace();
        }
        return null;
    }

    public static Class<?> getBukkitClass(String clazz) throws Exception {
        return Class.forName("org.bukkit.craftbukkit." + serverVersion + "." + clazz);
    }

    public static Class<?> getBungeeClass(String path, String clazz) throws Exception {
        return Class.forName("net.md_5.bungee." + path + "." + clazz);
    }

    private static Constructor<?> getConstructor(Class<?> clazz, Class<?>... args) throws Exception {
        Constructor<?> c = clazz.getConstructor(args);
        c.setAccessible(true);
        return c;
    }

    public static Enum<?> getEnum(Class<?> clazz, String constant) throws Exception {
        Class<?> c = Class.forName(clazz.getName());
        Enum<?>[] econstants = (Enum<?>[]) c.getEnumConstants();
        for (Enum<?> e : econstants)
            if (e.name().equalsIgnoreCase(constant))
                return e;
        throw new Exception("Enum constant not found " + constant);
    }

    public static Enum<?> getEnum(Class<?> clazz, String enumname, String constant) throws Exception {
        Class<?> c = Class.forName(clazz.getName() + "$" + enumname);
        Enum<?>[] econstants = (Enum<?>[]) c.getEnumConstants();
        for (Enum<?> e : econstants)
            if (e.name().equalsIgnoreCase(constant))
                return e;
        throw new Exception("Enum constant not found " + constant);
    }

    private static Field getField(Class<?> clazz, String fname) throws Exception {
        Field f;
        try {
            f = clazz.getDeclaredField(fname);
        } catch (Exception e) {
            f = clazz.getField(fname);
        }
        setFieldAccessible(f);
        return f;
    }

    public static Object getFirstObject(Class<?> clazz, Class<?> objclass, Object instance) throws Exception {
        Field f = null;
        for (Field fi : clazz.getDeclaredFields())
            if (fi.getType().equals(objclass)) {
                f = fi;
                break;
            }

        if (f == null)
            for (Field fi : clazz.getFields())
                if (fi.getType().equals(objclass)) {
                    f = fi;
                    break;
                }

        assert f != null;
        setFieldAccessible(f);
        return f.get(instance);
    }

    private static Method getMethod(Class<?> clazz, String mname) {
        Method m;
        try {
            m = clazz.getDeclaredMethod(mname);
        } catch (Exception e) {
            try {
                m = clazz.getMethod(mname);
            } catch (Exception ex) {
                return null;
            }
        }
        m.setAccessible(true);
        return m;
    }

    public static <T> Field getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if ((name == null || field.getName().equals(name)) && fieldType.isAssignableFrom(field.getType()) && index-- <= 0) {
                field.setAccessible(true);
                return field;
            }
        }

        if (target.getSuperclass() != null)
            return getField(target.getSuperclass(), name, fieldType, index);
        throw new IllegalArgumentException("Cannot find field with type " + fieldType);
    }

    public static Method getMethod(Class<?> clazz, String mname, Class<?>... args) {
        Method m;
        try {
            m = clazz.getDeclaredMethod(mname, args);
        } catch (Exception e) {
            try {
                m = clazz.getMethod(mname, args);
            } catch (Exception ex) {
                return null;
            }
        }
        m.setAccessible(true);
        return m;
    }

    public static Class<?> getNMSClass(String clazz) throws Exception {
        return Class.forName("net.minecraft.server." + serverVersion + "." + clazz);
    }

    public static Class<?> getOBCClass(String clazz) throws Exception {
        return Class.forName("org.bukkit.craftbukkit." + serverVersion + "." + clazz);
    }

    public static Object getObject(Class<?> clazz, Object obj, String fname) throws Exception {
        return getField(clazz, fname).get(obj);
    }

    public static Object getObject(Object obj, String fname) throws Exception {
        return getField(obj.getClass(), fname).get(obj);
    }

    public static Object invokeConstructor(Class<?> clazz, Class<?>[] args, Object... initargs) throws Exception {
        return getConstructor(clazz, args).newInstance(initargs);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method) throws Exception {
        return Objects.requireNonNull(getMethod(clazz, method)).invoke(obj);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Class<?>[] args, Object... initargs)
            throws Exception {
        return Objects.requireNonNull(getMethod(clazz, method, args)).invoke(obj, initargs);
    }

    public static Object invokeMethod(Class<?> clazz, Object obj, String method, Object... initargs) throws Exception {
        return Objects.requireNonNull(getMethod(clazz, method)).invoke(obj, initargs);
    }

    public static Object invokeMethod(Object obj, String method) throws Exception {
        return Objects.requireNonNull(getMethod(obj.getClass(), method)).invoke(obj);
    }

    public static Object invokeMethod(Object obj, String method, Object[] initargs) throws Exception {
        return Objects.requireNonNull(getMethod(obj.getClass(), method)).invoke(obj, initargs);
    }

    private static void setFieldAccessible(Field f) throws Exception {
        /*
        f.setAccessible(true);
        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(f, f.getModifiers() & ~Modifier.FINAL);
        */
        reflect.setEditable(f);
    }

    public static void setObject(Class<?> clazz, Object obj, String fname, Object value) throws Exception {
        // getField(clazz, fname).set(obj, value);
        reflect.setValue(clazz, fname, obj, value);
    }

    public static void setObject(Object obj, String fname, Object value) throws Exception {
        getField(obj.getClass(), fname).set(obj, value);
    }
}