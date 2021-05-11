package ioc.beans.lvl2;

import ioc.annotation.Autowired;
import ioc.annotation.Controller;
import ioc.beans.DoodleService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class DoodleController2 {
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
