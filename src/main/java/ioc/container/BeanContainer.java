package ioc.container;

import ioc.annotation.Component;
import ioc.annotation.Controller;
import ioc.annotation.Repository;
import ioc.annotation.Service;
import lombok.extern.slf4j.Slf4j;
import ioc.utils.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Контейнер для Beans
 */
@Slf4j
public class BeanContainer {

    /**
     * Надо ли загружать Bean.
     * Флаг, загружены уже бины или нет.
     * используется в методе isLoadBean()
     */
    private boolean isLoadBean = false;

    /**
     * Список аннотаций bean-компонента
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
            = Arrays.asList(Component.class,
                            Controller.class,
                            Service.class,
                            Repository.class
    );

    /**
     * Приватный конструктор для реализации паттерна Singleton
     */
    private BeanContainer(){
    }

    /**
     * Map для хранения Beans
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * Приватное поле для реализации паттерна Singleton
     */
    private static BeanContainer instance;

    /**
     * Получите экземпляр контейнера Bean
     * Метод для реализации паттерна Singleton
     */
    public static BeanContainer getInstance() {
        if (instance == null) {
            instance = new BeanContainer();
        }
        return instance;
    }

    //------- Загрузка кофейных зерен -------

    /**
     * Следует ли загружать Bean
     */
    public boolean isLoadBean() {
        return isLoadBean;
    }

    /**
     * Сканирование и загрузка всех бобов
     */
    public void loadBeans(String basePackage) {
        if (isLoadBean()) {
            log.warn("Bean загружен");
            return;
        }

        Set<Class<?>> classSet = ClassUtil.getPackageClass(basePackage);
        classSet.stream()
                .filter(clz -> {
                    for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                        if (clz.isAnnotationPresent(annotation)) {
                            return true;
                        }
                    }
                    return false;
                })
                .forEach(clz -> beanMap.put(clz, ClassUtil.newInstance(clz)));
        isLoadBean = true;
    }

    //------- Работа с коллекцией кофейных зерен -------

    /**
     * Получить экземпляр Bean
     */
    public Object getBean(Class<?> clz) {
        if (null == clz) {
            return null;
        }
        return beanMap.get(clz);
    }

    /**
     * Получить всю коллекцию c Bean
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * Добавить экземпляр Bean
     */
    public Object addBean(Class<?> clz, Object bean) {
        return beanMap.put(clz, bean);
    }

    /**
     * Удалить экземпляр Bean
     */
    public void removeBean(Class<?> clz) {
        beanMap.remove(clz);
    }

    /**
     * Количество экземпляров Bean
     */
    public int size() {
        return beanMap.size();
    }

    /**
     * Коллекция классов всех Bean
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * Получите коллекцию классов Bean через аннотации
     * По сути фильтр посредством аннотаций
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        return beanMap.keySet()
                .stream()
                .filter(clz -> clz.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    /**
     * Получите коллекцию классов Bean через класс реализации или родительский класс
     * По сути фильтр через родителей
     */
    public Set<Class<?>> getClassesBySuper(Class<?> superClass) {
        return beanMap.keySet()
                .stream()
                .filter(superClass::isAssignableFrom)
                .filter(clz -> !clz.equals(superClass))
                .collect(Collectors.toSet());
    }
}
