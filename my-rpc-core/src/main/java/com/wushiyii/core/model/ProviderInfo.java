package com.wushiyii.core.model;


import lombok.Data;

@Data
public class ProviderInfo {

    //全路径方法名
    private String providerName;

    //方法对应的反射类
    private Class<?> providerClazz;

}
