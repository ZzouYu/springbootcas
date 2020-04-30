package com.cas.casseckill.config;

import org.jasig.cas.client.authentication.AuthenticationFilter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
import org.jasig.cas.client.util.HttpServletRequestWrapperFilter;
import org.jasig.cas.client.validation.Cas30ProxyReceivingTicketValidationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zouyu
 * @description
 * @date 2020/4/29
 */

@Configuration
public class CasConfig {
    @Value("${spring.cas.casServerUrl}")
    private String serverUrl;
    @Value("${spring.cas.casClientUrl}")
    private String clientUrl;

    @Value("${server.port}")
    private String port;
    @Value("${ignore-host-url}")
    private String ignoreHostUrl;



    private static boolean casEnabled  = true;



    /**
     * 用于实现单点登出功能
     */
    @Bean
    public ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> singleSignOutHttpSessionListener() {
        ServletListenerRegistrationBean<SingleSignOutHttpSessionListener> listener = new ServletListenerRegistrationBean<>();
        listener.setEnabled(casEnabled);
        listener.setListener(new SingleSignOutHttpSessionListener());
        listener.setOrder(1);
        return listener ;
    }

    /**
     * 该过滤器用于实现单点登出功能，单点退出配置，一定要放在其他filter之前
     */
    @Bean
    public FilterRegistrationBean singleSignOutFilter() {
        FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
        filterRegistration.setFilter(new SingleSignOutFilter());
        filterRegistration.setEnabled(casEnabled);
        filterRegistration.addUrlPatterns("/*" );
        filterRegistration.addInitParameter("casServerUrlPrefix" ,"http://"+serverUrl+"cas");
        filterRegistration.setOrder(1);
        return filterRegistration ;
    }

    /**
     * 授权过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean filterAuthenticationRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new AuthenticationFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        Map<String,String> initParameters = new HashMap<>();
        initParameters.put("casServerLoginUrl", "http://"+serverUrl+"/cas/login");
        initParameters.put("serverName","http://"+clientUrl+":"+port);
        //忽略的url，"|"分隔多个url
        initParameters.put("ignorePattern", ignoreHostUrl);
        registration.setInitParameters(initParameters);
        // 设定加载的顺序
        registration.setOrder(3);
        return registration;
    }

    /**
     * description:过滤验证器
     *     * @param: []
     * @return: org.springframework.boot.web.servlet.FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean filterValidationRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new Cas30ProxyReceivingTicketValidationFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        Map<String,String>  initParameters = new HashMap<String, String>();
        initParameters.put("casServerUrlPrefix", "http://"+serverUrl+"/cas");
        initParameters.put("serverName", "http://"+clientUrl+":"+port);
        initParameters.put("useSession", "true");
        registration.setInitParameters(initParameters);
        // 设定加载的顺序
        registration.setOrder(4);
        return registration;
    }
    /**
     * wraper过滤器
     * @return
     */
    @Bean
    public FilterRegistrationBean filterWrapperRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new HttpServletRequestWrapperFilter());
        // 设定匹配的路径
        registration.addUrlPatterns("/*");
        // 设定加载的顺序
        registration.setOrder(5);
        return registration;
    }
}
