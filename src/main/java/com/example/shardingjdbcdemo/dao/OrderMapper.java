package com.example.shardingjdbcdemo.dao;

import com.example.shardingjdbcdemo.pojo.Order;
import tk.mybatis.mapper.common.Mapper;

@org.apache.ibatis.annotations.Mapper
public interface OrderMapper extends Mapper<Order> {

}