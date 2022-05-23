package com.wushiyii.core.model;


import lombok.Data;

@Data
public class MethodInfo {

    //全路径方法名
    private String methodName;

    //方法对应的反射类
    private Class<?> methodClazz;

}
