package ioc.beans;


import ioc.annotation.Service;

@Service
public class DoodleServiceImpl implements DoodleService {
    @Override
    public String helloWord() {
        return "hello word";
    }
}
