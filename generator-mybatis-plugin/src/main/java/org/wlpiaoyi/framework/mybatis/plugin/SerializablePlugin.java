package org.wlpiaoyi.framework.mybatis.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;

import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * @Author wlpiaoyi
 * @Date 2022/3/9 2:31 PM
 * @Version 1.0
 */
public class SerializablePlugin extends org.mybatis.generator.plugins.SerializablePlugin{

    private FullyQualifiedJavaType serializable;
    private FullyQualifiedJavaType gwtSerializable;
    private boolean addGWTInterface;
    private boolean suppressJavaInterface;
    private Random random = new Random(System.currentTimeMillis());
    private long serialVersionUID = 1L;

    public SerializablePlugin() {
        super();
        serializable = new FullyQualifiedJavaType("java.io.Serializable"); //$NON-NLS-1$
        gwtSerializable = new FullyQualifiedJavaType("com.google.gwt.user.client.rpc.IsSerializable"); //$NON-NLS-1$
    }

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        if(properties.getProperty("serialVersionUID") != null){
            this.serialVersionUID = Long.valueOf(properties.getProperty("serialVersionUID")); //$NON-NLS-1$
        }
    }

    protected void makeSerializable(TopLevelClass topLevelClass,
                                    IntrospectedTable introspectedTable) {
        if (this.addGWTInterface) {
            topLevelClass.addImportedType(gwtSerializable);
            topLevelClass.addSuperInterface(gwtSerializable);
        }

        if (!suppressJavaInterface) {
            topLevelClass.addImportedType(serializable);
            topLevelClass.addSuperInterface(serializable);

            Field field = new Field();
            field.setFinal(true);
            field.setInitializationString(serialVersionUID + "L"); //$NON-NLS-1$
            field.setName("serialVersionUID"); //$NON-NLS-1$
            field.setStatic(true);
            field.setType(new FullyQualifiedJavaType("long")); //$NON-NLS-1$
            field.setVisibility(JavaVisibility.PRIVATE);

            if (introspectedTable.getTargetRuntime() == IntrospectedTable.TargetRuntime.MYBATIS3_DSQL) {
                context.getCommentGenerator().addFieldAnnotation(field, introspectedTable,
                        topLevelClass.getImportedTypes());
            } else {
                context.getCommentGenerator().addFieldComment(field, introspectedTable);
            }

            topLevelClass.addField(field);
        }
    }
}
