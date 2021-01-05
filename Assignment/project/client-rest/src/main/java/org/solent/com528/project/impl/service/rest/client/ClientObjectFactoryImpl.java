package org.solent.com528.project.impl.service.rest.client;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.solent.com528.project.model.service.ServiceFacade;
import org.solent.com528.project.model.service.ServiceObjectFactory;

public class ClientObjectFactoryImpl implements ServiceObjectFactory {

    final static Logger LOG = LogManager.getLogger(ClientObjectFactoryImpl.class);
    
    private ServiceFacade serviceFacade = null;
    private String baseUrl = "http://localhost:8080/projectfacadeweb/rest/stationService";
    
    @Override
    public ServiceFacade getServiceFacade() {
        
        if (serviceFacade == null) {
            LOG.debug("creating new ServiceRestClientImpl for baseUrl=" + baseUrl);
            synchronized (this) {
                if (serviceFacade == null) {
                    serviceFacade = new StationServiceRestClientImpl(baseUrl);
                }
            }
        }
        
        return serviceFacade;
    }

    @Override
    public void shutDown() {
        //Does nothing
    }
    
}
