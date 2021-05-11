package ioc.utils;


import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

/**
 * Инструменты работы класса
 */
@Slf4j
public final class ClassUtil {

    /**
     * Протокол URL формата файла
     */
    public static final String FILE_PROTOCOL = "file";

    /**
     * протокол URL формата jar
     */
    public static final String JAR_PROTOCOL = "jar";

    /**
     * Получить classLoader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Получить класс
     */
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Создание экземпляра класса
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        try {
            Class<?> clazz = loadClass(className);
            return (T) clazz.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Создание экземпляра класса
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Установить значение атрибута класса
     */
    public static void setField(Field field, Object target, Object value) {
        setField(field, target, value, true);
    }

    /**
     * Установить значение атрибута класса
     */
    public static void setField(Field field, Object target, Object value, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("setField error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить сборник классов под пакет
     */
    public static Set<Class<?>> getPackageClass(String basePackage) {
        URL url = getClassLoader()
                .getResource(basePackage.replace(".", "/"));
        if (null == url) {
            throw new RuntimeException("Не удалось получить файл пути к проекту");
        }
        try {
            if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
                // Если это обычная папка, проходим
                File file = new File(url.getFile());
                Path basePath = file.toPath();
                return Files.walk(basePath)
                        .filter(path -> path.toFile().getName().endsWith(".class"))
                        .map(path -> getClassByPath(path, basePath, basePackage))
                        .collect(Collectors.toSet());
            } else if (url.getProtocol().equalsIgnoreCase(JAR_PROTOCOL)) {
                // Если он находится в пакете jar, проанализируем запись в пакете jar
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                return jarURLConnection.getJarFile()
                        .stream()
                        .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                        .map(ClassUtil::getClassByJar)
                        .collect(Collectors.toSet());
            }
            return Collections.emptySet();
        } catch (IOException e) {
            log.error("load package error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Получить класс из пути
     */
    private static Class<?> getClassByPath(Path classPath, Path basePath, String basePackage) {
        String packageName = classPath.toString().replace(basePath.toString(), "");
        String className = (basePackage + packageName)
                .replace("/", ".")
                .replace("\\", ".")
                .replace(".class", "");
        return loadClass(className);
    }

    /**
     * Получить класс из пакета jar
     */
    private static Class<?> getClassByJar(JarEntry jarEntry) {
        String jarEntryName = jarEntry.getName();
        // Получаем имя класса
        String className = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replaceAll("/", ".");
        return loadClass(className);
    }
}