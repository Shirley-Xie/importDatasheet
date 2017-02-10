
REACT:
(https://facebook.github.io/react/docs/hello-world.html)
1. 安装环境:http://zhinan.iyopu.com/category/frontend/page/2/
2. 练习react要的环境:https://github.com/dvajs/dva-cli/
($ npm install dva-cli -g
$ dva new myapp
# Start app
$ cd myapp
$ npm start)
3. 组件通信：http://www.alloyteam.com/2015/07/react-zu-jian-jian-tong-xin/

0. 查找config.pathID表和path.id表相同，过滤条件是AND (SUBSTRING 开始到结束)
select c.id, SUBSTRING(c.configValue, 40,50), p.name FROM config c JOIN path p ON c.pathID = p.id AND c.configKey='罐容表' AND p.type = '液态化工品储罐'

1. 查询时间段及对应数组pathID的对应降序部分数据：

SQl:
SELECT * FROM rdb WHERE `timeStamp` >= '2016-12-13 16:34:00' AND `timeStamp` <= '2016-12-15 16:36:00' AND 0.1 >= RAND() 
                  AND pathID in (702, 432) 
                  ORDER BY `timeStamp` DESC

Mybatis的写法：
SELECT `pathID`, `value`, `timeStamp` FROM rdb WHERE `timeStamp` >= #{begin} AND `timeStamp` <![CDATA [<=] ]> #{end} AND #{rate} >= RAND() } 
                  AND pathID IN
                  <foreach item="item" index="index" collection="_parameter['pathID']" open="(" separator="," close=")">
                           #{item}
                  </foreach>
                  ORDER BY `timeStamp` DESC

Java的使用：(对应sql的foreach，可以提升速度)
(
集合的使用：Set<Map.Entry<String, Object>> entries = keyValues.entrySet();

List<RDBModel> list = new  ArrayList<>();
for(Map.Entry<String, Object> entry: entries)
{
    RDBModel rdbModel = new RDBModel();
    rdbModel.setPathID( entry.getKey());
    rdbModel.setValue(String.valueOf(entry.getValue()));
    rdbModel.setTimeStamp(date);
    list.add(rdbModel);
}

   rdbMapper.insertList(list);
)



2. 计算总和:
SQl:
SELECT sum(amount) AS amount_total FROM `location_mess_change` WHERE locationID IN 
(SELECT DISTINCT id FROM `location` WHERE locationCode IN ("TCPSPC_TCYH_T101","TCPSPC_TCYH_T201") ) 
AND beginTime >= "2016-12-01" and endTime <= "2017-1-12"

结果显示：
amount_total
68.2988

Mybatis的写法：
<select id="queryByLocationID" resultType="DECIMAL" parameterType="map">
SELECT sum(amount) AS amount_total  FROM `location_mess_change` WHERE locationID IN 
(SELECT DISTINCT id FROM `location` WHERE locationCode IN
<foreach item="item" index="index" collection="_parameter['locationCodes']" open="(" separator="," close=")">
         #{item}
</foreach>
) 
AND beginTime >= #{beginTime} <![CDATA[ and endTime <= #{endTime}]]>
</select>


3. 查找rdbModels的最前面不同key的数据：
// 处理开始阶段, 取起点时间到起点附近结束时间之内的数据
List<String> beginKeys = keys.subList(0, keys.size());

List<RDBModel> beginModels = rdbMapper.findByBeginEnd(request);
//循环每条数据
for(RDBModel model: beginModels)
{
String pathID = model.getPathID();
String value = model.getValue();
Date timeStamp = model.getTimeStamp();

//建立不同key数组
List<String> newBeginKeys = new ArrayList<>();
	//一次只能找一个然后跳出循环
	for(String beginKey: beginKeys)
	{
	    if(!pathID.equals(beginKey))
	    {
	        newBeginKeys.add(beginKey);
	        continue;
	    }

	    // 构造接近数据集
	    CloseSet closeSet = new CloseSet();
	    closeSet.setKey(beginKey);
	    closeSet.setBeginTime(timeStamp);
	    closeSet.setBeginValue(value);

	    // 将接近集加入回头可以用
	    keyCloseSetMap.put(beginKey, closeSet);
	    result.add(closeSet);
	}

	// 去除已经找到的Key
	beginKeys = newBeginKeys;

	// 找齐了
	if(beginKeys.size() == 0)
	{
	    break;
    }
}


4. 
原版：
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

匹配版：
       try {
           connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/streamdb?useUnicode=true&characterEncoding=utf8&mysqlEncoding=utf8&zeroDateTimeBehavior=convertToNull",
                   "root", "");
       } catch (SQLException e) {
           e.printStackTrace();
       }

       queryRunner = new QueryRunner();

       Object[][] params = new Object[entries.size()][];
       int index = 0;
           Object[] param = {entry.getKey(), String.valueOf(entry.getValue()), date};
           params[index++] = param;
        }


        






