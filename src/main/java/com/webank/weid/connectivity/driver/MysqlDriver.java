/*
 *       Copyright© (2018-2019) WeBank Co., Ltd.
 *
 *       This file is part of weidentity-java-sdk.
 *
 *       weidentity-java-sdk is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Lesser General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       weidentity-java-sdk is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Lesser General Public License for more details.
 *
 *       You should have received a copy of the GNU Lesser General Public License
 *       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.webank.weid.connectivity.driver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.PropertyUtils;

/**
 * mysql operations.
 *
 * @author tonychen 2019年3月18日
 */
public class MysqlDriver implements DataDriver {

    private static final Logger logger = LoggerFactory.getLogger(MysqlDriver.class);

    /**
     * sql for query.
     */
    private static final String SQL_QUERY = "select id,data,created from sdk_all_data where id =?";

    /**
     * sql for save.
     */
    private static final String SQL_SAVE = "insert into sdk_all_data(id, data) values(?,?)";

    /**
     * sql for update.
     */
    private static final String SQL_UPDATE = "update sdk_all_data set data = ?, updated=? where "
        + "id = ?";

    /**
     * sql for delete.
     */
    private static final String SQL_DELETE = "delete from sdk_all_data where id = ?";

    /**
     * Mysql connection.
     */
    private static Connection connection;

    public MysqlDriver() {
        init();
    }

    /**
     * initialize database connection.
     */
    public void init() {

        try {
        	
        	//initialize jdbc properties.
            PropertyUtils.loadProperties(DataDriverConstant.JDBC_PROPERTIES);
            String dbUrl = PropertyUtils.getProperty(DataDriverConstant.JDBC_URL);
            String userName = PropertyUtils.getProperty(DataDriverConstant.JDBC_USER_NAME);
            String passWord = PropertyUtils.getProperty(DataDriverConstant.JDBC_USER_PASSWORD);
            
            // 1. initialize mysql jdbc driver
            Class.forName("com.mysql.jdbc.Driver");

            // 2. initialize mysql connection
            connection = DriverManager.getConnection(dbUrl, userName, passWord);

        } catch (SQLException e) {
            logger.error("Initialize mysql connection with exception ", e);
        } catch (ClassNotFoundException e) {
            logger.error("Initialize failed with exception ", e);
        } catch (IOException e) {
        	logger.error("Initialize failed with exception ", e);
		}
    }

    @Override
    public ResponseData<String> getData(String id) {

        ResponseData<String> result = new ResponseData<String>();
        PreparedStatement ps;
        String data = null;
        try {
            ps = connection.prepareStatement(SQL_QUERY);
            ps.setString(DataDriverConstant.SQL_INDEX_FIRST, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                data = rs.getString(DataDriverConstant.SQL_COLUMN_DATA);
            }
            rs.close();
            ps.close();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(data);
        } catch (SQLException e) {
            logger.error("Query data from mysql with exception", e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(StringUtils.EMPTY);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#save(java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Integer> save(String id, String data) {

        ResponseData<Integer> result = new ResponseData<Integer>();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(SQL_SAVE);
            ps.setString(DataDriverConstant.SQL_INDEX_FIRST, id);
            ps.setString(DataDriverConstant.SQL_INDEX_SECOND, data);
            int rs = ps.executeUpdate();
            ps.close();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(rs);
        } catch (SQLException e) {
            logger.error("Save data to mysql with exception", e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#batchSave(java.util.List, java.util.List)
     */
    @Override
    public ResponseData<Integer> batchSave(List<String> ids, List<String> dataList) {

        ResponseData<Integer> result = new ResponseData<Integer>();
        try {
            connection.setAutoCommit(false);
            PreparedStatement psts = connection.prepareStatement(SQL_SAVE);
            for (int i = 0; i < ids.size(); i++) {
                psts.setString(DataDriverConstant.SQL_INDEX_FIRST, ids.get(i));
                psts.setString(DataDriverConstant.SQL_INDEX_SECOND, dataList.get(i));
                psts.addBatch();
            }

            psts.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            logger.error("Batch save data to mysql with exception", e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#delete(java.lang.String)
     */
    @Override
    public ResponseData<Integer> delete(String id) {

        ResponseData<Integer> result = new ResponseData<Integer>();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(SQL_DELETE);
            ps.setString(DataDriverConstant.SQL_INDEX_FIRST, id);
            int rs = ps.executeUpdate();
            ps.close();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(rs);
        } catch (SQLException e) {
            logger.error("Delete data from mysql with exception", e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see com.webank.weid.connectivity.driver.DBDriver#update(java.lang.String, java.lang.String)
     */
    @Override
    public ResponseData<Integer> update(String id, String data) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());

        ResponseData<Integer> result = new ResponseData<Integer>();
        PreparedStatement ps;
        try {
            ps = connection.prepareStatement(SQL_UPDATE);
            ps.setString(DataDriverConstant.SQL_INDEX_FIRST, data);
            ps.setString(DataDriverConstant.SQL_INDEX_SECOND, date);
            ps.setString(DataDriverConstant.SQL_INDEX_THIRD, id);
            int rs = ps.executeUpdate();
            ps.close();
            result.setErrorCode(ErrorCode.SUCCESS);
            result.setResult(rs);
        } catch (SQLException e) {
            logger.error("Update data into mysql with exception", e);
            result.setErrorCode(ErrorCode.SQL_EXECUTE_FAILED);
            result.setResult(DataDriverConstant.SQL_EXECUTE_FAILED_STATUS);
        }
        return result;
    }

    /**
     * close mysql connection.
     */
    public void close() {

        try {
            connection.close();
            logger.info("successfully closed the connection.");
        } catch (SQLException e) {
            logger.error("close connection failed with exception. ", e);
        }
    }
}