package com.example.shardingjdbcdemo.api;

import com.example.shardingjdbcdemo.dao.OrderMapper;
import com.example.shardingjdbcdemo.pojo.Order;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Lixiangping
 * @createTime 2022年03月23日 16:14
 * @decription: 分库分表从测试
 */
@RestController
public class OrderController {

    @Resource
    private OrderMapper orderMapper;

    /**
     * 新增订单测试分库分表：
     *  订单id为奇数时数据落到ds0库，订单id为偶数时数据落到ds1库.
     *  user_id为奇数时数据落到t_order_0表，user_id为偶数时数据落到t_order_1表.
     * @param order
     * @return
     */
    @PostMapping("/order/add")
    public String add(@RequestBody Order order){
        int i = orderMapper.insert(order);
        if (i > 0) {
            return "新增成功~";
        }
        return "新增失败~";
    }

    /**
     * 查询订单详情
     * @param orderId 订单id
     * @param userId 用户id
     * @return
     */
    @GetMapping("/order/{orderId}")
    public Order add(@PathVariable("orderId") Long orderId){
        Example example = Example.builder(Order.class).build();
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", orderId);
        List<Order> list = orderMapper.selectByExample(example);
        if (list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

}
