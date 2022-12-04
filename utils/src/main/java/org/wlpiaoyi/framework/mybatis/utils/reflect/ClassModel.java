package org.wlpiaoyi.framework.mybatis.utils.reflect;

public class ClassModel {

    ClassModel(){

    }

    public Class<?> getClass(String classPath){
        try {
            return Class.forName(classPath).getDeclaringClass();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
