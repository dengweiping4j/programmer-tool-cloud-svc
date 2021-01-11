package com.programmer.util.service;

import com.programmer.util.domain.DataConnection;
import com.programmer.util.domain.DriverPath;
import com.programmer.util.domain.Result;
import com.programmer.util.domain.dto.DataConnectionDTO;
import com.programmer.util.domain.mapper.DataConnectionMapper;
import com.programmer.util.domain.vo.DataConnectionVO;
import com.programmer.util.repository.DataConnectionRepository;
import com.programmer.util.repository.DataConnectionSpecifications;
import com.programmer.util.repository.DriverPathRepository;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author: DengWeiPing
 * @time: 2020/6/10 9:15
 */
@Service
@Transactional(rollbackOn = Exception.class)
public class DataConnectionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataConnectionService.class);

    @Autowired
    private DataConnectionRepository dataConnectionRepository;

    @Autowired
    private DriverPathRepository driverPathRepository;

    public DataConnection getDataConnectionById(String id) {
        DataConnection dataConnection = dataConnectionRepository.findById(id).orElse(null);
        return dataConnection;
    }

    public Result create(DataConnectionDTO dataConnectionDTO) {
        DataConnection dataConnection = DataConnectionMapper.toEntity(dataConnectionDTO);
        Result result = testConnect(dataConnection);
        if (result.succeed()) {
            List<DataConnection> dataConnections = dataConnectionRepository.findAll();
            boolean isExisted = isDataConnectionExisted(dataConnections, dataConnection);
            if (isExisted) {
                return Result.error("检查数据库引擎类型、IP地址、端口号、数据库名称、用户名时，发现已存在");
            }
            try {
                DataConnection resultDataConnection = dataConnectionRepository.save(dataConnection);
                if (resultDataConnection != null) {
                    return Result.success();
                }
            } catch (Exception e) {
                LOGGER.error("modify dataConnection error : {}", e);
                return Result.error("保存数据连接时出错", e.getMessage());
            }
        }
        return Result.error("数据库连接异常，请检查数据库连接，配置参数等是否正常", result.getMessage());
    }

    public Result modify(String id, DataConnectionDTO dataConnectionDTO) {
        DataConnection dataConnection = DataConnectionMapper.toEntity(dataConnectionDTO);
        Result result = testConnect(dataConnection);
        if (result.succeed()) {
            List<DataConnection> dataConnections = dataConnectionRepository.findAllByIdNotIn(Collections.singletonList(id));
            boolean isExisted = isDataConnectionExisted(dataConnections, dataConnection);
            if (isExisted) {
                return Result.error("检查数据库引擎类型、IP地址、端口号、数据库名称、用户名时，发现已存在");
            }
            try {
                DataConnection resultDataConnection = dataConnectionRepository.save(dataConnection);
                if (resultDataConnection != null) {
                    return Result.success();
                }
            } catch (Exception e) {
                LOGGER.error("modify dataConnection error : {}", e);
                return Result.error("保存数据连接时出错", e.getMessage());
            }
        }
        return Result.error("数据库连接异常，请检查数据库连接，配置参数等是否正常", result.getMessage());
    }

    public Result delete(String id) {
        DataConnection dataConnection = new DataConnection();
        dataConnection.setId(id);

        // todo...

        return Result.success();
    }

    /**
     * 测试数据库连接
     *
     * @param dataConnection
     * @return
     */
    public Result testConnect(DataConnection dataConnection) {
        Connection conn = null;
        try {
            Result result = getConnection(dataConnection);
            if (result.getData() == null) {
                return result;
            }
            conn = (Connection) result.getData();
            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("连接数据库失败：{}", e.getMessage());
            return Result.error(e.getMessage());
        } finally {
            disConnect(conn, null, null);
        }
    }

    /**
     * 获取数据库连接
     *
     * @param dataConnection
     * @return
     */
    public Result getConnection(DataConnection dataConnection) {
        DataConnectionDTO dataConnectionDTO = DataConnectionMapper.toDTO(dataConnection);
        String ip = dataConnectionDTO.getIp();
        String port = dataConnectionDTO.getPort();
        String database = dataConnectionDTO.getDatabase();
        String username = dataConnectionDTO.getUsername();
        String password = dataConnectionDTO.getPassword();
        String type = dataConnectionDTO.getType();
        String driver = dataConnectionDTO.getDriver();
        String url = dataConnectionDTO.getUrl();
        if (StringUtils.isBlank(ip)) {
            return Result.error("IP地址不能为空");
        }
        if (StringUtils.isBlank(port)) {
            return Result.error("端口号不能为空");
        }
        if (StringUtils.isBlank(database)) {
            return Result.error("数据库名称不能为空");
        }
        if (StringUtils.isBlank(username)) {
            return Result.error("用户名不能为空");
        }
        if (StringUtils.isBlank(password)) {
            return Result.error("密码不能为空");
        }
        if (StringUtils.isBlank(driver)) {
            return Result.error("未找到数据库驱动");
        }

        if (StringUtils.isBlank(url)) {
            switch (type) {
                case DataConnection.MYSQL:
                    url = "jdbc:mysql://" + ip + ":" + port + "/" + database;
                    break;
                case DataConnection.POSTGRESQL:
                case DataConnection.T_BASE:
                    url = "jdbc:postgresql://" + ip + ":" + port + "/" + database;
                    break;
                case DataConnection.ORACLE:
                    url = "jdbc:oracle:thin:@" + ip + ":" + port + "/" + database;
                    break;
                case DataConnection.SQL_SERVER:
                    url = "jdbc:sqlserver://" + ip + ":" + port + ";DatabaseName=" + database;
                    break;
                default:
            }
        }
        final String connUrl = url;

        String schema = dataConnectionDTO.getSchema();
        if (type.equals(DataConnection.POSTGRESQL) || type.equals(DataConnection.T_BASE)) {
            if (StringUtils.isBlank(schema)) {
                schema = "public";
            }
        }
        final String dbSchema = schema;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<Result> future = new FutureTask<>(() -> {
            try {
                //动态加载数据库驱动
                Properties properties = new Properties();
                properties.put("user", username);
                properties.put("password", password);
                Driver driverClass = getDriverLoaderByName(driver, type);
                Connection conn = driverClass.connect(connUrl, properties);
                if (conn == null) {
                    return Result.error("连接失败");
                }

                PreparedStatement preparedStatement;
                if (type.equals(DataConnection.POSTGRESQL) || type.equals(DataConnection.T_BASE)) {
                    preparedStatement = conn.prepareStatement("ALTER USER \"" + username + "\" SET search_path to " + dbSchema);
                    preparedStatement.execute();
                }
                return Result.success(conn);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("connection error: {}", e.getMessage());
                return Result.error("连接失败", e.getMessage());
            } catch (Throwable t) {
                t.printStackTrace();
                LOGGER.error("connection error: {}", t.getMessage());
                return Result.error("连接失败，可能是指定的数据库版本不正确", t.getMessage());
            }
        });

        try {
            executor.execute(future);
            // 指定超时时间
            Result result = future.get(5000, TimeUnit.MILLISECONDS);
            return result;
        } catch (TimeoutException e) {
            return Result.error("数据库连接超时");
        } catch (Exception e) {
            return Result.error("系统异常");
        } finally {
            future.cancel(true);
            executor.shutdown();
        }
    }

    /**
     * 动态加载JDBC驱动
     *
     * @param driverPath 驱动类路径
     * @return
     * @throws Exception
     */
    public Driver getDriverLoaderByName(String driverPath, String type) throws Exception {
        if (StringUtils.isBlank(driverPath)) {
            return null;
        }
        DriverPath driverPath1 = driverPathRepository.findByDriverAndType(driverPath, type);
        File file = new File(driverPath1.getPath());
        if (!file.exists()) {
            System.out.println(driverPath + " 对应的驱动jar不存在.");
            return null;
        }

        URLClassLoader loader = new URLClassLoader(new URL[]{file.toURI().toURL()}, null);
        loader.clearAssertionStatus();

        Driver driver = (Driver) Class.forName(driverPath, true, loader).newInstance();
        return driver;
    }


    /**
     * 关闭数据库连接
     *
     * @param conn
     * @param preparedStatement
     * @param resultSet
     */
    public void disConnect(Connection conn, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            LOGGER.error("disConnect exception: ", e);
        }
    }

    /**
     * 关闭数据库连接
     *
     * @param conn
     * @param Statement
     * @param resultSet
     */
    public void disConnect(Connection conn, Statement Statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (Statement != null) {
                Statement.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            LOGGER.error("disConnect exception: ", e);
        }
    }

    /**
     * 获取所有数据连接信息
     *
     * @return
     */
    public List<DataConnectionVO> getAll() {
        List<DataConnection> dataConnections = dataConnectionRepository.findAllByOrderByName();
        List<DataConnectionVO> list = new ArrayList<>();
        for (DataConnection dataConnection : dataConnections) {
            DataConnectionVO dataConnectionVO = DataConnectionMapper.toVO(dataConnection);
            list.add(dataConnectionVO);
        }
        return list;
    }

    public List<DataConnectionVO> queryWhere(DataConnectionDTO queryDTO) {
        List<DataConnection> dataConnectionList = dataConnectionRepository.findAll(DataConnectionSpecifications.queryList(queryDTO));
        List<DataConnectionVO> resultList = new ArrayList<>();
        for (DataConnection dataConnection : dataConnectionList) {
            resultList.add(DataConnectionMapper.toVO(dataConnection));
        }
        return resultList;
    }

    public Page<DataConnectionDTO> query(DataConnectionDTO queryDTO, Pageable pageable) {
        return dataConnectionRepository.findAll(DataConnectionSpecifications.queryList(queryDTO), pageable).map(dataConnection -> DataConnectionMapper.toDTO(dataConnection));
    }

    /**
     * 判断数据连接是否存在
     *
     * @param list
     * @param target
     * @return
     */
    public boolean isDataConnectionExisted(List<DataConnection> list, DataConnection target) {
        for (DataConnection item : list) {
            if (target.equals(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 执行指定查询语句
     *
     * @param sql            sql语句
     * @param dataConnection 数据库连接信息
     * @param params         查询参数
     * @return
     */
    public <T> Result executeQueryBySql(String sql, DataConnection dataConnection, List<T> params) {
        if (dataConnection == null) {
            return Result.error("未找到指定的数据连接！");
        }
        Result getConnectionResult = getConnection(dataConnection);
        Connection conn = null;
        if (getConnectionResult.succeed()) {
            conn = (Connection) getConnectionResult.getData();
        }
        if (conn == null) {
            return Result.error("数据库连接失败！");
        }
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = conn.prepareStatement(sql);
            if (params != null && !params.isEmpty()) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }
            resultSet = preparedStatement.executeQuery();
            List<Map<String, Object>> resultList = new ArrayList<>();
            ResultSetMetaData rsmd = resultSet.getMetaData();
            int columnCount = rsmd.getColumnCount();
            while (resultSet.next()) {
                Map<String, Object> map = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rsmd.getColumnName(i);
                    switch (rsmd.getColumnTypeName(i).toUpperCase()) {
                        case "YEAR":
                            String yearStr = resultSet.getString(i);
                            if (yearStr != null) {
                                map.put(columnName, yearStr.substring(0, 4));
                            }
                            break;
                        case "DATETIME":
                            String str = resultSet.getString(i);
                            if (str != null) {
                                Timestamp dateTime = resultSet.getTimestamp(i);
                                if (dateTime != null) {
                                    String dateTimeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateTime);
                                    map.put(columnName, dateTimeStr);
                                } else {
                                    map.put(columnName, null);
                                }
                            }
                            break;
                        case "TIMESTAMP":
                            String timestampStr = resultSet.getString(i);
                            map.put(columnName, timestampStr);
                            break;
                        case "DATE":
                            String dateStr = resultSet.getString(i);
                            if (dateStr != null) {
                                if (resultSet.getDate(i) != null) {
                                    if (dataConnection.equals(DataConnection.ORACLE)) {
                                        dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(resultSet.getTimestamp(i));
                                    } else {
                                        dateStr = new SimpleDateFormat("yyyy - MM - dd").format(resultSet.getTimestamp(i));
                                    }
                                }
                                map.put(columnName, dateStr);
                            }
                            break;
                        case "TIME":
                            Timestamp time = resultSet.getTimestamp(i);
                            if (time != null) {
                                String timeStr = new SimpleDateFormat("HH:mm:ss").format(time);
                                map.put(columnName, timeStr);
                            } else {
                                map.put(columnName, null);
                            }
                            break;
                        case "BIT":
                        case "VARBIT":
                            map.put(rsmd.getColumnName(i), Long.toBinaryString(resultSet.getLong(i)));
                            break;
                        case "BLOB":
                        case "BINARY":
                        case "VARBINARY":
                        case "TINYBLOB":
                        case "MEDIUMBLOB":
                        case "LONGBLOB":
                        case "BYTEA":
                            InputStream in = null;
                            try {
                                in = resultSet.getBinaryStream(i);
                                if (in == null) {
                                    break;
                                }
                                StringBuilder sb = new StringBuilder();
                                String line;
                                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                                while ((line = br.readLine()) != null) {
                                    sb.append(line);
                                }
                                map.put(columnName, sb.toString());
                            } finally {
                                if (in != null) {
                                    in.close();
                                }
                            }
                            break;
                        case "CIRCLE":
                        case "PATH":
                        case "POINT":
                        case "POLYGON":
                        case "LINE":
                        case "LSEG":
                        case "BOX":
                            map.put(rsmd.getColumnName(i), resultSet.getString(i));
                            break;
                        default:
                            map.put(rsmd.getColumnName(i), resultSet.getObject(i));
                    }
                }
                resultList.add(map);
            }
            return Result.success(resultList);
        } catch (Exception e) {
            LOGGER.error("execute query sql exception: ", e);
            LOGGER.error("error sql : ", sql);
            e.printStackTrace();
            return Result.error("执行SQL查询语句时出错 ", e.getMessage());
        } finally {
            disConnect(conn, preparedStatement, resultSet);
        }
    }

    /**
     * 执行自定义SQL语句
     *
     * @param sql
     * @param dataConnection
     * @param params
     * @param <T>
     * @return
     */
    public <T> Result executeBySql(String sql, DataConnection dataConnection, List<T> params) {
        if (dataConnection == null) {
            return Result.error("未找到指定的数据连接！");
        }
        Result getConnectionResult = getConnection(dataConnection);
        Connection conn = null;
        if (getConnectionResult.succeed()) {
            conn = (Connection) getConnectionResult.getData();
        }
        if (conn == null) {
            return Result.error("数据库连接失败！");
        }
        PreparedStatement preparedStatement = null;
        int result;
        try {
            preparedStatement = conn.prepareStatement(sql);
            if (params != null && !params.isEmpty()) {
                for (int i = 0; i < params.size(); i++) {
                    preparedStatement.setObject(i + 1, params.get(i));
                }
            }
            preparedStatement.executeUpdate();
            return Result.success("执行成功");
        } catch (Exception e) {
            LOGGER.error("execute query sql exception: ", e);
            LOGGER.error("error sql : ", sql);
            e.printStackTrace();
            return Result.error("执行SQL语句时出错 ", e.getMessage());
        } finally {
            disConnect(conn, preparedStatement, null);
        }
    }

    /**
     * 获取数据库表和视图列表
     *
     * @param dataConnectionId
     * @return
     */
    public Result getTableAndViewList(String dataConnectionId, String schema) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //获取表
        Result tableResult = getTableListByDataConnectionId(dataConnectionId, schema);
        if (tableResult.errored()) {
            return tableResult;
        }
        resultList.addAll((List<Map<String, Object>>) tableResult.getData());
        //获取视图
        Result viewResult = getViewListByDataConnectionId(dataConnectionId, schema);
        if (viewResult.errored()) {
            return viewResult;
        }
        resultList.addAll((List<Map<String, Object>>) viewResult.getData());
        return Result.success(resultList);
    }

    /**
     * 根据数据源id查询数据库表列表
     *
     * @param dataConnectionId
     * @return
     */
    public Result getTableListByDataConnectionId(String dataConnectionId, String schema) {
        DataConnection dataConnection = dataConnectionRepository.findById(dataConnectionId).orElse(null);
        if (dataConnection == null) {
            return Result.error("未找到指定的数据连接！");
        }
        DataConnectionDTO dataConnectionDTO = DataConnectionMapper.toDTO(dataConnection);
        StringBuffer sql = new StringBuffer();
        List<String> params = new ArrayList<>();
        switch (dataConnectionDTO.getType()) {
            case DataConnection.MYSQL:
                sql.append("select distinct table_name,table_comment from information_schema.tables where Table_type = 'BASE TABLE' and table_schema=? order by table_name");
                params.add(dataConnectionDTO.getDatabase());
                break;
            case DataConnection.POSTGRESQL:
            case DataConnection.T_BASE:
                sql.append("select distinct relname as table_name, cast(obj_description(relfilenode, 'pg_class') as varchar) as table_comment"
                        + " from pg_class c left join pg_namespace p on c.relnamespace = p.oid"
                        + " inner join information_schema.table_privileges i on i.table_name=c.relname"
                        + " where p.nspname = ? and i.table_schema=? and relkind = 'r' and relname not like 'pg_%' and relname not like 'sql_%' order by relname");
                params.add(dataConnectionDTO.getSchema());
                params.add(dataConnectionDTO.getSchema());
                break;
            case DataConnection.ORACLE:
                sql.append("select a.table_name,b.COMMENTS table_comment from ALL_TABLES a,ALL_TAB_COMMENTS b WHERE a.TABLE_NAME=b.TABLE_NAME and a.OWNER= ? order by a.OWNER,table_name");
                if (StringUtils.isNotBlank(schema)) {
                    params.add(schema);
                } else {
                    params.add(dataConnectionDTO.getUsername().toUpperCase());
                }
                break;
            case DataConnection.SQL_SERVER:
                sql.append("select a.name AS table_name,convert(varchar(100), isnull(g.[value], '')) AS table_comment"
                        + " from sys.tables a left join sys.extended_properties g on (a.object_id = g.major_id AND g.minor_id = 0) order by a.name");
                break;
        }
        Result result = executeQueryBySql(sql.toString(), dataConnection, params);
        if (result.succeed()) {
            List<Map<String, Object>> tableList = (List<Map<String, Object>>) result.getData();
            return Result.success(tableList);
        }
        return result;
    }

    /**
     * 根据数据源id查询数据库视图列表
     *
     * @param dataConnectionId
     * @return
     */
    public Result getViewListByDataConnectionId(String dataConnectionId, String schema) {
        DataConnection dataConnection = dataConnectionRepository.findById(dataConnectionId).orElse(null);
        if (dataConnection == null) {
            return Result.error("未找到指定的数据连接！");
        }
        DataConnectionDTO dataConnectionDTO = DataConnectionMapper.toDTO(dataConnection);
        StringBuffer sql = new StringBuffer();
        List<String> params = new ArrayList<>();
        switch (dataConnectionDTO.getType()) {
            case DataConnection.MYSQL:
                sql.append("SELECT distinct TABLE_NAME as view_name FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = ? AND  TABLE_TYPE ='VIEW'");
                params.add(dataConnectionDTO.getDatabase());
                break;
            case DataConnection.POSTGRESQL:
            case DataConnection.T_BASE:
                sql.append("select distinct view_name from (select distinct v.viewname as view_name"
                        + " from pg_views v"
                        + " inner join information_schema.table_privileges i on v.schemaname = i.table_schema"
                        + " WHERE schemaname = ?) t"
                        + " order by view_name");
                params.add(dataConnectionDTO.getSchema());
                break;
            case DataConnection.ORACLE:
                sql.append("select distinct view_name from ALL_VIEWS where OWNER= ? order by view_name");
                if (StringUtils.isNotBlank(schema)) {
                    params.add(schema);
                } else {
                    params.add(dataConnectionDTO.getUsername().toUpperCase());
                }
                break;
            case DataConnection.SQL_SERVER:
                sql.append("select [name] as view_name from sysobjects where xtype='V'");
                break;
        }
        Result result = executeQueryBySql(sql.toString(), dataConnection, params);
        if (result.succeed()) {
            List<Map<String, Object>> viewList = (List<Map<String, Object>>) result.getData();
            return Result.success(viewList);
        }
        return result;
    }

    /**
     * 获取数据库类型
     *
     * @param dataConnectionId
     * @return
     */
    public String getDataConnectionTypeById(String dataConnectionId) {
        DataConnection dataConnection = getDataConnectionById(dataConnectionId);
        if (dataConnection != null) {
            return DataConnectionMapper.toVO(dataConnection).getType();
        }
        return null;
    }

    /**
     * 查询数据库表空间
     *
     * @param dataConnectionId
     * @return
     */
    public List<String> getSchemas(String dataConnectionId) {
        String sql = "SELECT username FROM all_users ORDER BY username";
        DataConnection dataConnection = dataConnectionRepository.findById(dataConnectionId).get();
        Result executeQueryResult = executeQueryBySql(sql, dataConnection, null);
        if (executeQueryResult.errored()) {
            return null;
        }
        List<Map<String, Object>> data = (List<Map<String, Object>>) executeQueryResult.getData();
        List<String> result = new ArrayList<>();
        for (Map<String, Object> map : data) {
            result.add(map.get("USERNAME").toString());
        }
        if (data != null && data.size() > 0) {

        }
        return result;
    }
}
