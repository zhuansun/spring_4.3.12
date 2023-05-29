package com.zspc.core.spring.ext;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author zhuansunpengcheng
 * @create 2019-07-11 6:18 PM
 **/
@Service
public class PersonService {

    @EventListener(classes = {ApplicationEvent.class})
    public void listen(ApplicationEvent event) {
        System.out.println("UserService。。监听到的事件：" + event);
    }
}
