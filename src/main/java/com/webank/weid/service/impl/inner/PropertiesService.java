package com.webank.weid.service.impl.inner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    
    private static final Logger logger =  LoggerFactory.getLogger(PropertiesService.class);
    
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
        timer.scheduleAtFixedRate(task, intevalPeriod, intevalPeriod);
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
        logger.info("[load] begin load properties.");
        synchronized (properties) {
            try {
                Map<String, String> query = query();
                processUpdate(query);
                if (instance != null) {
                    Long intevalPeriod = getIntevalPeriod();
                    if (intevalPeriod != null 
                        && intevalPeriod.longValue() != prevIntevalPeriod.longValue()) {
                        reStartMonitor();
                    }
                }
            } catch (Throwable e) {
                logger.error("[load] the properties load error.", e);
            }
        }
    }

    /*
     * properties 中存放的是现内存中的所有配置数据
     * queryMap 中为数据库中查询出来最新的配置数据
     * 此处更新内存中的数据，并日志输出，需要删除的key, 新增的key，更新的key
     * 
     * 更新过程： 先把更新数据库中的key全量放入内存中，然后处理需要删除的key
     */
    private void processUpdate(Map<String, String> queryMap) {
        // 判断两个map是否一致，如果一致则不用后续处理
        if (queryMap.equals(properties)) {
            logger.info("[processUpdate] configuration not changed.");
            return;
        }
        Set<String> delKey = getDeleteKey(queryMap);
        if (delKey.size() > 0) {
            logger.info("[processUpdate] del key = {}.", delKey);
        }
        Set<String> updateKey = getUpdateKey(queryMap);
        if (updateKey.size() > 0) {
            logger.info("[processUpdate] update key = {}.", updateKey); 
            print(queryMap, updateKey);
        }
        properties.putAll(queryMap);
        delKey.stream().forEach(key -> properties.remove(key));
        logger.info("[processUpdate] configuration change complete.");
    }

    private void print(Map<String, String> queryMap, Set<String> keys) {
        // 日志输出变化项, 此处未来需要对隐秘数据进行过滤
        keys.stream().forEach(key -> logger.info(
            "[processUpdate] key: {}, value: {} ---> {}", 
            key, 
            properties.get(key), 
            queryMap.get(key)
        ));
    }
    
    private Set<String> getDeleteKey(Map<String, String> queryMap) {
        return properties.entrySet()
            .stream()
            .filter(entry -> !queryMap.containsKey(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).keySet();
    }

    private Set<String> getUpdateKey(Map<String, String> queryMap) {
        return queryMap.entrySet()
            .stream()
            .filter(entry -> (properties.containsKey(entry.getKey()) 
                && !StringUtils.equals(entry.getValue(), properties.get(entry.getKey()))))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)).keySet();
    }

    /**
     * 向数据库中添加配置集合.
     * 
     * @param data 存入的配置数据
     * @return 返回添加结果
     */
    public boolean saveProperties(Map<String, String> data) {
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
            data.stream().forEach(key -> query.remove(key));
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
        // 处理value为null的转换成空字符串, 因为ConcurrentHashMap中不允许null值存在
        map.entrySet().stream().forEach(
            entry -> {
                if (entry.getValue() == null) {
                    entry.setValue(StringUtils.EMPTY); 
                }
            }
        );
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
