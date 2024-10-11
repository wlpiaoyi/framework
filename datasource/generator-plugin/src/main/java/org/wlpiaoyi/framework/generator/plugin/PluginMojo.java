package org.wlpiaoyi.framework.generator.plugin;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.wlpiaoyi.framework.generator.plugin.model.ConfigModel;
import org.wlpiaoyi.framework.generator.plugin.utils.PluginUtils;
import org.wlpiaoyi.framework.utils.MapUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.data.ReaderUtils;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
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


    @SneakyThrows
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            if(ValueUtils.isBlank(this.basePath)) {
                this.basePath = DataUtils.USER_DIR;
            }
            String pluginJson = ReaderUtils.loadString(this.basePath + this.plugDir, null);
            if(ValueUtils.isNotBlank(pluginJson)){
                Map pluginMap = GsonBuilder.gsonDefault().fromJson(pluginJson, Map.class);
                for(Object key : pluginMap.keySet()){
                    try{
                        Field field = PluginUtils.class.getDeclaredField(key.toString());
                        Map value = MapUtils.get(pluginMap, key);
                        field.set(null, value);
                    }catch (Exception e){}
                }
            }
            Properties properties = ReaderUtils.loadProperties(this.basePath + this.configDir);
            ConfigModel configModel = new ConfigModel(properties);
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
            PluginTable plugin = new PluginTable(configModel);
            String templatePath = this.basePath + templateDir;
            PluginClass pluginClass = new PluginClass(plugin, templatePath, configModel);
            pluginClass.run();
        }catch (Exception e){
            log.error("插件运行失败", e);
            throw e;
        }

    }

    public static void main(String[] args) throws MojoExecutionException, MojoFailureException {
        PluginMojo mogo = new PluginMojo();
        mogo.configDir = "\\src\\main\\resources\\generator.config.properties";
        mogo.plugDir = "\\src\\main\\resources\\generator.plug.json";
        mogo.templateDir = "\\src\\main\\resources\\template";
        mogo.basePath = DataUtils.USER_DIR + "\\datasource\\generator-plugin";

        mogo.execute();
    }
}
