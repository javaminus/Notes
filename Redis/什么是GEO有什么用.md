GEO就是Geolocation的简写形式，代表地理坐标，Redis GEO 主要用于存储地理位置信息的，帮助我们根据经纬度来检索数据。  它主要支持如下命令：  

- **GEOADD**：添加一个地理空间信息，包含：经度（longitude）、纬度（latitude）、值（member） 
- **GEODIST**：计算指定的两个点之间的距离并返回 
- **GEOHASH**：将指定member的坐标转为hash字符串形式并返回 
- **GEOPOS**：返回指定member的坐标 
- **GEORADIUS**：指定圆心、半径，找到该圆内包含的所有member，并按照与圆心之间的距离排序后返回。 
- **GEOSEARCH**：在指定范围内搜索member，并按照与指定点之间的距离排序后返回。范围可以是圆形或矩形。 
- **GEOSEARCHSTORE**：与GEOSEARCH功能一致，不过可以把结果存储到一个指定的key。   