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

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Mojo( name = "mojo")
@Slf4j
public class PluginMojo extends AbstractMojo {


    @Parameter(name = "configDir", defaultValue = "\\src\\main\\resources\\config.properties")
    private String configDir;

    @Parameter(name = "templateDir", defaultValue = "\\src\\main\\resources\\template")
    private String templateDir;

    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Properties properties = ReaderUtils.loadProperties(DataUtils.USER_DIR + this.configDir);
        String url = properties.getProperty("url");
        String userName = properties.getProperty("userName");
        String password = properties.getProperty("password");
        String tablePrefix = properties.getProperty("tablePrefix");
        String tableNamePattern = properties.getProperty("tableNamePattern");
        String packagePath = properties.getProperty("packagePath");
        String projectName = properties.getProperty("projectName");
        String excludeColumns = properties.getProperty("excludeColumns");
        log.info("mojo execute:" +
                        "\n\tuserDir:" + DataUtils.USER_DIR +
                        "\n\tconfigDir:" + this.configDir +
                        "\n\ttemplateDir:" + this.templateDir +
                        "\n\turl:" + url +
                        "\n\tuserName:" + userName +
                        "\n\tpassword:" + password +
                        "\n\ttablePrefix:" + tablePrefix +
                        "\n\tpackagePath:" + packagePath +
                        "\n\ttableNamePattern:" + tableNamePattern +
                        "\n\tprojectName:" + projectName +
                        "\n\texcludeColumns:" + excludeColumns);
        PluginTable plugin = new PluginTable(url, userName, password, tablePrefix, tableNamePattern);
        String templatePath = DataUtils.USER_DIR + templateDir;
        List<String> excludeColumn = ValueUtils.toStringList(excludeColumns);
        PluginClass pluginClass = new PluginClass(plugin, templatePath, projectName, packagePath, excludeColumn);
        pluginClass.run();

    }

}
