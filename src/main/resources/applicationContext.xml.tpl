<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~       Copyright© (2018) WeBank Co., Ltd.
  ~
  ~       This file is part of weidentity-java-sdk.
  ~
  ~       weidentity-java-sdk is free software: you can redistribute it and/or modify
  ~       it under the terms of the GNU Lesser General Public License as published by
  ~       the Free Software Foundation, either version 3 of the License, or
  ~       (at your option) any later version.
  ~
  ~       weidentity-java-sdk is distributed in the hope that it will be useful,
  ~       but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~       GNU Lesser General Public License for more details.
  ~
  ~       You should have received a copy of the GNU Lesser General Public License
  ~       along with weidentity-java-sdk.  If not, see <https://www.gnu.org/licenses/>.
  -->

<beans xmlns:context="http://www.springframework.org/schema/context"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xmlns="http://www.springframework.org/schema/beans"
  xsi:schemaLocation="http://www.springframework.org/schema/beans
    http://www.springframework.org/schema/beans/spring-beans-3.0.xsd
    http://www.springframework.org/schema/context
    http://www.springframework.org/schema/context/spring-context-3.0.xsd">

  <bean class="org.springframework.beans.factory.config.PreferencesPlaceholderConfigurer"
    id="appConfig">
    <property name="properties">
      <props>
        <prop key="weId.contractaddress">${WEID_ADDRESS}</prop>
        <prop key="cpt.contractaddress">${CPT_ADDRESS}</prop>
        <prop key="issuer.contractaddress">${ISSUER_ADDRESS}</prop>
        <prop key="evidence.contractaddress">${EVIDENCE_ADDRESS}</prop>
      </props>
    </property>
    <!--  <property name="location" value="classpath:application.properties" />-->
  </bean>

  <bean class="org.bcos.web3j.utils.Async" id="async">
    <constructor-arg ref="pool"
      type="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor"/>
  </bean>
  <bean class="org.bcos.web3j.utils.AttemptsConf" id="attemptsConf">
    <constructor-arg index="0" value="1200"/>
    <constructor-arg index="1" value="10"/>
  </bean>
  <bean class="org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor" id="pool">
    <property name="corePoolSize" value="150"/>
    <property name="maxPoolSize" value="200"/>
    <property name="queueCapacity" value="20"/>
    <property name="keepAliveSeconds" value="60"/>
    <property name="rejectedExecutionHandler">
      <bean class="java.util.concurrent.ThreadPoolExecutor.AbortPolicy"/>
    </property>
  </bean>

  <context:component-scan base-package="com.webank.weid.config"/>

  <bean class="org.bcos.contract.tools.ToolConf" id="toolConf">
    <property name="systemProxyAddress" value="0x3ca60c68c6264ab08c05ae2df4bfc384c81ebcef"/>
    <property name="privKey"
      value="bcec428d5205abe0f0cc8a734083908d9eb8563e31f943d760786edf42ad67dd"/>
    <property name="account" value="0x1914dc80628aaabd66cf23297fbd6feb14da6ce7"/>
    <property name="outPutpath" value="./output/"/>
  </bean>

  <bean class="org.bcos.channel.proxy.Server" id="server">
  </bean>
  <bean class="org.bcos.channel.client.Service" id="channelService">
    <property name="orgID" value="WB"/>
    <property name="connectSeconds" value="10"/>
    <property name="connectSleepPerMillis" value="10"/>
    <property name="allChannelConnections">
      <map>
        <entry key="WB">
          <bean class="org.bcos.channel.handler.ChannelConnections">
            <property name="caCertPath" value="classpath:ca.crt"/>
            <property name="clientKeystorePath" value="classpath:client.keystore"/>
            <property name="keystorePassWord" value="123456"/>
            <property name="clientCertPassWord" value="123456"/>
            <property name="connectionsStr">
              <list>
                ${BLOCKCHIAN_NODE_INFO}
              </list>
            </property>
          </bean>
        </entry>
      </map>
    </property>
  </bean>
</beans>
