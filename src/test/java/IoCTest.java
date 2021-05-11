import ioc.beans.DoodleController;
import ioc.container.BeanContainer;
import ioc.container.IoC;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class IoCTest {
    @Test
    public void doIoc() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("ioc");

        beanContainer.getBeans().stream()
                .map(o -> o.getClass().getName())
                .forEach(System.out::println);
        new IoC().doIoC();
        DoodleController controller = (DoodleController) beanContainer.getBean(DoodleController.class);
    }
}
