package com.example.shardingjdbcdemo;

import com.example.shardingjdbcdemo.dao.OrderMapper;
import com.example.shardingjdbcdemo.pojo.Order;
import com.example.shardingjdbcdemo.snowflake.SnowflakeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.UUID;

@SpringBootTest
class ShardingJdbcDemoApplicationTests {

    @Resource
    private OrderMapper orderMapper;
    @Test
    void contextLoads() {

        Order order = new Order();
        order.setId(101010);
        order.setAmount(BigDecimal.ZERO);
        order.setTitle("写操作测试");

        int i = orderMapper.insert(order);
    }

}
