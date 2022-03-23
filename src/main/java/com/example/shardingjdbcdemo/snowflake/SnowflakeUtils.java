package com.example.shardingjdbcdemo.snowflake;

/**
 * @author Lixiangping
 * @createTime 2022年03月10日 13:59
 * @decription:
 */
public class SnowflakeUtils {

    static SnowFlake snowFlake = new SnowFlake(1, 1);
    static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);

    public static Long nextId(){
//        return snowFlake.nextId();
        return idWorker.nextId();
    }

}
