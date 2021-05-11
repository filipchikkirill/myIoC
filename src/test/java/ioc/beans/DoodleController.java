package ioc.beans;

import ioc.annotation.Autowired;
import ioc.annotation.Controller;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DoodleController {
    @Autowired
    private DoodleService doodleService;

    private int count;

    public void hello() {
        log.info(doodleService.helloWord());
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }
}
