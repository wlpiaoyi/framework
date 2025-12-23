package org.wlpiaoyi.framework.generator.plugin;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.wlpiaoyi.framework.generator.plugin.model.ConfigModel;
import org.wlpiaoyi.framework.generator.plugin.model.PluginModel;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;

import java.util.Properties;

@Mojo( name = "mojo")
@Slf4j
public class PluginMojo extends AbstractMojo {


    @Parameter(name = "configDir", defaultValue = "\\src\\main\\resources\\generator.config.properties")
    private String configDir;

    @Parameter(name = "plugDir", defaultValue = "\\src\\main\\resources\\generator.plug.json")
    private String plugDir;

    @Parameter(name = "templateDir", defaultValue = "\\src\\main\\resources\\template")
    private String templateDir;

    @Parameter(name = "basePath", defaultValue = "")
    private String basePath;


    public PluginMojo() {
        super();
    }

    public PluginMojo(String configDir, String plugDir, String templateDir, String basePath) {
        super();
        this.configDir = configDir;
        this.plugDir = plugDir;
        this.templateDir = templateDir;
        this.basePath = basePath;
    }


    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            if(ValueUtils.isBlank(this.basePath)) {
                this.basePath = DataUtils.USER_DIR;
            }
            String pluginJson = ReaderUtils.loadString(this.basePath + this.plugDir, null);
            PluginModel.loadData(pluginJson);
            Properties properties = ReaderUtils.loadProperties(this.basePath + this.configDir);
            ConfigModel.loadData(properties);
            ConfigModel configModel = ConfigModel.getInstance();
            log.info("mojo execute:" +
                    "\n\tuserDir:" + this.basePath +
                    "\n\tconfigDir:" + this.configDir +
                    "\n\ttemplateDir:" + this.templateDir +
                    "\n\turl:" + configModel.getUrl() +
                    "\n\tuserName:" + configModel.getUserName() +
                    "\n\tpassword:" + configModel.getPassword() +
                    "\n\tdatabaseName:" + configModel.getDatabaseName() +
                    "\n\ttablePrefix:" + configModel.getTablePrefix() +
                    "\n\ttableNamePattern:" + configModel.getTableNamePattern() +
                    "\n\tpackagePath:" + configModel.getPackagePath() +
                    "\n\tprojectName:" + configModel.getProjectName() +
                    "\n\texcludeColumns:" + configModel.getExcludeColumns());
            PluginTable plugin = new PluginTable();
            String templatePath = this.basePath + templateDir;
            PluginClass pluginClass = new PluginClass(plugin, templatePath);
            pluginClass.run();
        }catch (Exception e){
            log.error("插件运行失败", e);
            throw e;
        }

    }
}
