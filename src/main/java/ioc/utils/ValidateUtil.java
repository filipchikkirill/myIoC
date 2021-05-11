package ioc.utils;

/**
 * Инструменты, связанные с проверкой
 */
public final class ValidateUtil {

    /**
     * Объект null
     */
    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    /**
     * Строка нулевая или ""
     */
    public static boolean isEmpty(String obj) {
        return (obj == null || "".equals(obj));
    }

    /**
     * Объект не равен нулю
     */
    public static boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * Строка не равна нулю или ""
     */
    public static boolean isNotEmpty(String obj) {
        return !isEmpty(obj);
    }
}
