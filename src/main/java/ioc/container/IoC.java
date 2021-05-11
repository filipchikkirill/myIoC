package ioc.container;

import ioc.annotation.Autowired;
import lombok.extern.slf4j.Slf4j;
import ioc.utils.ClassUtil;

import java.lang.reflect.Field;
import java.util.Optional;

@Slf4j
public class IoC {

    /**
     * Контейнер для кофейных зерен
     */
    private BeanContainer beanContainer;

    public IoC() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * Выполнить IoC
     */
    public void doIoC() {
        for (Class<?> clz : beanContainer.getClasses()) { // Обходим все бины в контейнере бинов
            final Object targetBean = beanContainer.getBean(clz);
            Field[] fields = clz.getDeclaredFields();
            for (Field field : fields) { // Обходим все атрибуты в Бине
                if (field.isAnnotationPresent(Autowired.class)) {// Если атрибут аннотирован Autowired, вставьте его
                    final Class<?> fieldClass = field.getType();
                    Object fieldValue = getClassInstance(fieldClass);
                    if (null != fieldValue) {
                        ClassUtil.setField(field, targetBean, fieldValue);
                    } else {
                        throw new RuntimeException("Невозможно ввести соответствующий класс, тип цели:" + fieldClass.getName());
                    }
                }
            }
        }
    }

    /**
     * Получить его экземпляр или класс реализации в соответствии с Class
     */
    private Object getClassInstance(final Class<?> clz) {
        return Optional
                .ofNullable(beanContainer.getBean(clz))
                .orElseGet(() -> {
                    Class<?> implementClass = getImplementClass(clz);
                    if (null != implementClass) {
                        return beanContainer.getBean(implementClass);
                    }
                    return null;
                });
    }

    /**
     * Получить класс реализации интерфейса
     */
    private Class<?> getImplementClass(final Class<?> interfaceClass) {
        return beanContainer.getClassesBySuper(interfaceClass)
                .stream()
                .findFirst()
                .orElse(null);
    }

}