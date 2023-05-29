package com.zspc.core.spring.tx.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author zhuansunpengcheng
 * @create 2019-07-04 6:36 PM
 **/
@Repository
public class PersonDao {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void insert(){
        String sql = "INSERT INTO `person`(name,age) VALUES(?,?)";
        String username = UUID.randomUUID().toString().substring(0, 5);
        jdbcTemplate.update(sql, username,19);
    }

}
