package com.mulesoft.services.elk.appender;

import com.mulesoft.services.elk.client.Client;
import com.mulesoft.services.elk.pojos.Config;
import com.mulesoft.services.elk.pojos.Host;
import com.mulesoft.services.elk.utils.Utils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.status.StatusLogger;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;

@Plugin(name = "Elk",
        category = "Core",
        elementType = "appender",
        printObject = true)
public final class Appender extends AbstractAppender {
    private static final Logger LOGGER = StatusLogger.getLogger();

    private Client client;

    public Appender(String name,
                    Config config,
                    Filter filter,
                    Layout<? extends Serializable> layout) throws IOException {
        super(name, filter, layout, true);
        client = Client.getInstance(config);
    }

    @PluginFactory
    public static Appender create(@PluginAttribute("name") String name,
                                  @PluginAttribute(value = "index") String index,
                                  @PluginAttribute(value = "server") String server,
                                  @PluginAttribute(value = "username") String username,
                                  @PluginAttribute(value = "password") String password,
                                  @PluginElement("Layout") Layout layout,
                                  @PluginElement("Filters") Filter filter) {

        Host host = null;
        try {
            host = new Host(server);
        } catch (MalformedURLException e) {
            LOGGER.error("Error creating host based on server '"+ server +"'",e);
        }

        Config config = new Config(
                name,
                index,
                host,
                username,
                password
        );


        try {
            return new Appender(
                    name,
                    config,
                    filter,
                    layout
            );
        } catch (IOException e) {
            LOGGER.error("Error creating appender named '"+ name +"'",e);
            return null;
        }
    }

    @Override
    public void append(LogEvent logEvent) {
        try {
            client.storeXContentDocument(
                    Utils.docBuilder(logEvent, ThreadContext.getContext()),
                    logEvent.isEndOfBatch());
            ThreadContext.clearAll();
        } catch (Throwable e) {
            LOGGER.error("Error sending log to ELK for logger '"+ logEvent.getLoggerName() +"'",e);
            if (!true)
                throw new AppenderLoggingException(e);
        }
    }
}