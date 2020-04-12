package com.webank.weid.service.impl.inner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

import com.webank.weid.constant.DataDriverConstant;
import com.webank.weid.constant.ErrorCode;
import com.webank.weid.constant.ParamKeyConstant;
import com.webank.weid.protocol.response.ResponseData;
import com.webank.weid.util.DataToolUtils;

/**
 * 读取存入数据库中的配置.
 * 
 * @author yanggang
 *
 */
public class PropertiesService extends InnerService {

    private static PropertiesService instance;

    // 存放内置配置得对象
    private static ConcurrentHashMap<String, String> properties = new ConcurrentHashMap<>();
    private static final String KEY = "aa6d69cde0904d2aad1713d75cafa718";
    private static final String DOMAIN = DataDriverConstant.DOMAIN_DEFAULT_INFO;
    private static final Long DEFAULT_INTEVAL_PERIOD = 60L * 5 * 1000;
    private static Timer timer;
    private static Long prevIntevalPeriod = DEFAULT_INTEVAL_PERIOD;

    PropertiesService() {
        load();
    }

    /**
     * 单例模式.
     * 
     * @return 返回配置对象
     */
    public static PropertiesService getInstance() {
        if (instance == null) {
            synchronized (PropertiesService.class) {
                if (instance == null) {
                    instance = new PropertiesService();
                    startMonitor();
                }
            }
        }
        return instance;
    }
    
    /**
     * 启动监听, 定时刷新配置.
     */
    private static void startMonitor() {
        Long intevalPeriod = getIntevalPeriod();
        if (intevalPeriod == null) {
            intevalPeriod = DEFAULT_INTEVAL_PERIOD;
        }
        prevIntevalPeriod = intevalPeriod;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                instance.load();
            }
        };
        timer = new Timer();
        timer.scheduleAtFixedRate(task, 0, intevalPeriod);
    }
    
    private static void reStartMonitor() {
        timer.cancel();
        startMonitor();
    }
    
    // 获取配置的调度时间
    private static Long getIntevalPeriod() {
        String intevalPeriodStr = instance.getProperty(ParamKeyConstant.INTEVAL_PERIOD);
        if (StringUtils.isNotBlank(intevalPeriodStr)) {
            return Long.valueOf(intevalPeriodStr);
        }
        return null;
    }
    
    /**
     * 根据key获取配置值.
     * 
     * @param key 传入需要获取的配置key
     * @return 返回配置值
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * 根据key获取配置值,如果配置值为null,则返回默认值defaultValue.
     * 
     * @param key          传入需要获取的配置key
     * @param defaultValue 当获取到的值为null时的默认值
     * @return 返回配置值
     */
    public String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取所有的配置项.
     * 
     * @return 返回所有的配置值
     */
    public Map<String, String> getAllProperty() {
        Map<String, String> result = new HashMap<String, String>();
        result.putAll(properties);
        return result;
    }

    /**
     * 更新properties缓存数据,后续可以用于定时更新.
     */
    private void load() {
        synchronized (properties) {
            Map<String, String> query = query();
            properties.clear();
            properties.putAll(query);
            if (instance != null) {
                Long intevalPeriod = getIntevalPeriod();
                if (intevalPeriod != null 
                    && intevalPeriod.longValue() != prevIntevalPeriod.longValue()) {
                    reStartMonitor();
                }
            }
        }
    }

    /**
     * 向数据库中添加配置集合.
     * 
     * @param data 存入的配置数据
     * @return 返回添加结果
     */
    public boolean addProperties(Map<String, String> data) {
        synchronized (properties) {
            Map<String, String> query = this.query();
            query.putAll(data);
            if (this.save(query)) {
                // 同步配置到内存
                load();
                return true;
            }
            return false;
        }
    }

    /**
     * 删除数据库中指定集合配置.
     * 
     * @param data 要删除得key集合
     * @return 返回删除结果
     */
    public synchronized boolean removeProperties(Set<String> data) {
        synchronized (properties) {
            // 查询数据库中得配置
            Map<String, String> query = this.query();
            // 删除对应的key
            for (String string : data) {
                query.remove(string);
            }
            // 更新到数据库中
            if (this.save(query)) {
                // 同步配置到内存
                load();
                return true;
            }
            return false;
        }
    }

    private Map<String, String> query() {
        Map<String, String> map = new HashMap<String, String>();
        ResponseData<String> responseData = super.getDataDriver().get(DOMAIN, KEY);
        if (StringUtils.isNotBlank(responseData.getResult())) {
            map = DataToolUtils.deserialize(responseData.getResult(), Map.class);
        }
        return map;
    }

    private boolean save(Map<String, String> data) {
        String value = DataToolUtils.serialize(data);
        ResponseData<Integer> update = super.getDataDriver().saveOrUpdate(DOMAIN, KEY, value);
        if (update.getErrorCode().intValue() == ErrorCode.SUCCESS.getCode()) {
            return true;
        }
        return false;
    }
}
