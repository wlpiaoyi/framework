package org.wlpiaoyi.framework.utils.reflect;

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
