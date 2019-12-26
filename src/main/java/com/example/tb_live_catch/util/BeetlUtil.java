package com.example.tb_live_catch.util;

import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.StringTemplateResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * beetl渲染模板引擎辅助类
 */
public class BeetlUtil {

    private BeetlUtil() {
    }

    private static final Logger logger = LoggerFactory.getLogger(BeetlUtil.class);

    /**
     * 渲染
     * @param template  模板
     * @param map  参数
     * @return
     */
    public static String render(String template, Map<String, Object> map) {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg;
        try {
            cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            Template t = gt.getTemplate(template);
            t.binding(map);
            return t.render();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static String render(String template, String var, Object obj) {
        StringTemplateResourceLoader resourceLoader = new StringTemplateResourceLoader();
        Configuration cfg;
        try {
            cfg = Configuration.defaultConfiguration();
            GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
            Template t = gt.getTemplate(template);
            t.binding(var, obj);
            return t.render();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

}
