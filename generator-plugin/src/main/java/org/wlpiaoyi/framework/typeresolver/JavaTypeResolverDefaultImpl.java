package org.wlpiaoyi.framework.typeresolver;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Properties;

public class JavaTypeResolverDefaultImpl extends org.mybatis.generator.internal.types.JavaTypeResolverDefaultImpl {

    private boolean useBasType;

    public JavaTypeResolverDefaultImpl() {
        super();
        typeMap.put(Types.DATE, new JdbcTypeInformation("DATE", //$NON-NLS-1$
                new FullyQualifiedJavaType(LocalDate.class.getName())));
        typeMap.put(Types.TIME, new JdbcTypeInformation("TIME", //$NON-NLS-1$
                new FullyQualifiedJavaType(LocalTime.class.getName())));
        typeMap.put(Types.TIMESTAMP, new JdbcTypeInformation("TIMESTAMP", //$NON-NLS-1$
                new FullyQualifiedJavaType(LocalDateTime.class.getName())));
    }

    @Override
    public void addConfigurationProperties(Properties properties) {
        super.addConfigurationProperties(properties);
        if(properties.getProperty("useBasType") != null){
            this.useBasType = Boolean.valueOf(properties.getProperty("useBasType")); //$NON-NLS-1$
        }
        if (this.useBasType){

            typeMap.put(Types.BIGINT, new JdbcTypeInformation("BIGINT", //$NON-NLS-1$
                    new FullyQualifiedJavaType(long.class.getName())));
            typeMap.put(Types.BIT, new JdbcTypeInformation("BIT", //$NON-NLS-1$
                    new FullyQualifiedJavaType(boolean.class.getName())));
            typeMap.put(Types.BOOLEAN, new JdbcTypeInformation("BOOLEAN", //$NON-NLS-1$
                    new FullyQualifiedJavaType(boolean.class.getName())));
            typeMap.put(Types.DOUBLE, new JdbcTypeInformation("DOUBLE", //$NON-NLS-1$
                    new FullyQualifiedJavaType(double.class.getName())));
            typeMap.put(Types.FLOAT, new JdbcTypeInformation("FLOAT", //$NON-NLS-1$
                    new FullyQualifiedJavaType(double.class.getName())));
            typeMap.put(Types.INTEGER, new JdbcTypeInformation("INTEGER", //$NON-NLS-1$
                    new FullyQualifiedJavaType(int.class.getName())));
            typeMap.put(Types.REAL, new JdbcTypeInformation("REAL", //$NON-NLS-1$
                    new FullyQualifiedJavaType(float.class.getName())));
            typeMap.put(Types.SMALLINT, new JdbcTypeInformation("SMALLINT", //$NON-NLS-1$
                    new FullyQualifiedJavaType(short.class.getName())));
            typeMap.put(Types.TINYINT, new JdbcTypeInformation("TINYINT", //$NON-NLS-1$
                    new FullyQualifiedJavaType(byte.class.getName())));
        }
    }

}
