package com.zspc.core.spring.tx.service;

import com.zspc.core.spring.tx.dao.PersonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author zhuansunpengcheng
 * @create 2019-07-04 6:43 PM
 **/
@Service
public class PersonService {

    @Autowired
    PersonDao personDao;

    @Transactional(rollbackFor = Exception.class)
    public void insert(){
        personDao.insert();
        System.out.println("插入完成...");
    }

}
