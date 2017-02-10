package com.iyoupu.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.QueryRunner;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: of2032
 * Date: 2017/1/24
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
@Slf4j
public class TestMysql {
    public static void main(String[] args) {
        log.info("3333333333333333333333");

        try {
            Object[][] params ={{"test-12344，测试批量数据","TT","TT","TT","TT",new Date(),33422},{"test-12344，测试批量数据","TT","TT","TT","TT",new Date(),33422}};
            Connection connection = DriverManager.getConnection("jdbc:mysql://iotdb.mysqldb.chinacloudapi.cn:3306/streamdb?useUnicode=true&characterEncoding=utf8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull",
                    "iotdb%yopu", "Yopu1234");
            QueryRunner queryRunner = new QueryRunner();
            queryRunner.batch(connection, "INSERT INTO  log (`content`,`level`,`loggerName`,threadName,`hostName`,`timeStamp`,pathID)VALUES(?,?,?,?,?,?,?) ",
                    params
                    );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
