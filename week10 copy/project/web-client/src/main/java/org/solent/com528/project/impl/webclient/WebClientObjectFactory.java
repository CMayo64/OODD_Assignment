package org.solent.com528.project.impl.webclient;

import java.util.Date;
import java.util.UUID;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.solent.com528.project.impl.service.rest.client.ClientObjectFactoryImpl;
import org.solent.com528.project.impl.service.rest.client.ConfigurationPoller;
import org.solent.com528.project.model.service.ServiceFacade;
import org.solent.com528.project.model.service.ServiceObjectFactory;

@WebListener
public class WebClientObjectFactory implements ServletContextListener {

    //Sets up logging
    final static Logger LOG = LogManager.getLogger(WebClientObjectFactory.class);

    private static ServiceFacade serviceFacade = null;

    private static ServiceObjectFactory clientObjectFactory = null;

    private static ConfigurationPoller configurationPoller = null;

    public static String getTicketMachineUuid() {
        return configurationPoller.getTicketMachineUuid();
    }

    public static void setTicketMachineUuid(String ticketMachineUuid) {
        LOG.debug("setting ticketMachineUuid=" + ticketMachineUuid);
        configurationPoller.setTicketMachineUuid(ticketMachineUuid);
    }

    public static String getStationName() {
        return configurationPoller.getStationName();
    }

    public static Integer getStationZone() {
        return configurationPoller.getStationZone();
    }

    public static Date getLastClientUpdateTime() {
        return configurationPoller.getLastClientUpdateTime();
    }

    public static Date getLastClientUpdateAttempt() {
        return configurationPoller.getLastClientUpdateAttempt();
    }

    public static ServiceFacade getServiceFacade() {
        if (serviceFacade == null) {
            synchronized (WebClientObjectFactory.class) {
                if (serviceFacade == null) {

                    LOG.debug("client web application starting");
                    clientObjectFactory = new ClientObjectFactoryImpl();
                    serviceFacade = clientObjectFactory.getServiceFacade();

                    configurationPoller = new ConfigurationPoller(serviceFacade);
                    //Sets a random UUID
                    String ticketMachineUuid = UUID.randomUUID().toString();
                    configurationPoller.setTicketMachineUuid(ticketMachineUuid);
                    long initialDelay = 0;
                    long delay = 30; //Every 30 seconds
                    LOG.debug("starting configuration poller initialDelay=" + initialDelay
                            + ", delay=" + delay
                            + ", ticketMachineUuid=" + ticketMachineUuid);
                    configurationPoller.init(initialDelay, delay);
                }
            }
        }
        return serviceFacade;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOG.debug("WEB CLIENT OBJECT FACTORY context initialised");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOG.debug("WEB CLIENT OBJECT FACTORY shutting down context");
        if (clientObjectFactory != null) {
            synchronized (WebClientObjectFactory.class) {
                LOG.debug("WEB OBJECT FACTORY shutting down configurationPoller");
                configurationPoller.shutdown();
                LOG.debug("WEB CLIENT OBJECT FACTORY  shutting down clientObjectFactory");
                clientObjectFactory.shutDown();
            }

        }
    }

}
