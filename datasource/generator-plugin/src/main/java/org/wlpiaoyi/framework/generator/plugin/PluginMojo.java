package org.wlpiaoyi.framework.generator.plugin;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;

import java.util.List;
import java.util.Properties;

@Mojo( name = "mojo")
@Slf4j
public class PluginMojo extends AbstractMojo {


    @Parameter(name = "configDir", defaultValue = "\\src\\main\\resources\\config.properties")
    private String configDir;

    @Parameter(name = "templateDir", defaultValue = "\\src\\main\\resources\\template")
    private String templateDir;

    @Parameter(name = "basePath", defaultValue = "")
    private String basePath;


    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if(ValueUtils.isBlank(this.basePath)) {
            this.basePath = DataUtils.USER_DIR;
        }
        Properties properties = ReaderUtils.loadProperties(this.basePath + this.configDir);
        String url = properties.getProperty("url");
        String userName = properties.getProperty("userName");
        String password = properties.getProperty("password");
        String databaseName = properties.getProperty("databaseName");
        String tablePrefix = properties.getProperty("tablePrefix");
        String tableNamePattern = properties.getProperty("tableNamePattern");
        String packagePath = properties.getProperty("packagePath");
        String projectName = properties.getProperty("projectName");
        String excludeColumns = properties.getProperty("excludeColumns");
        String classVersion = properties.getProperty("classVersion", "1.0");
        log.info("mojo execute:" +
                        "\n\tuserDir:" + this.basePath +
                        "\n\tconfigDir:" + this.configDir +
                        "\n\ttemplateDir:" + this.templateDir +
                        "\n\turl:" + url +
                        "\n\tuserName:" + userName +
                        "\n\tpassword:" + password +
                        "\n\tdatabaseName:" + databaseName +
                        "\n\ttablePrefix:" + tablePrefix +
                        "\n\ttableNamePattern:" + tableNamePattern +
                        "\n\tpackagePath:" + packagePath +
                        "\n\tprojectName:" + projectName +
                        "\n\texcludeColumns:" + excludeColumns);
        PluginTable plugin = new PluginTable(url, userName, password, databaseName, tablePrefix, tableNamePattern);
        String templatePath = this.basePath + templateDir;
        List<String> excludeColumn = ValueUtils.toStringList(excludeColumns);
        PluginClass pluginClass = new PluginClass(plugin, templatePath, projectName, packagePath, excludeColumn, classVersion);
        pluginClass.run();

    }

    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        PluginMojo mogo = new PluginMojo();
        mogo.configDir = "\\src\\main\\resources\\config.properties";
        mogo.templateDir = "\\src\\main\\resources\\template";
        mogo.basePath = DataUtils.USER_DIR + "\\datasource\\generator-plugin";

        mogo.execute();
    }
}
