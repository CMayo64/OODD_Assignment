package org.solent.com528.project.impl.service.test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.solent.com528.project.impl.service.ServiceFacadeImpl;
import org.solent.com528.project.impl.service.ServiceObjectFactoryJpaImpl;
import org.solent.com528.project.model.service.ServiceFacade;
import org.solent.com528.project.model.service.ServiceObjectFactory;

public class ServiceFacadeImplTest {
    
    ServiceObjectFactory serviceObjectFactory = null;
    ServiceFacade serviceFacade = null;


    @Before
    public void loadFactory() {

        serviceObjectFactory = new ServiceObjectFactoryJpaImpl();
        
        serviceFacade = serviceObjectFactory.getServiceFacade();

    }

    @Test
    public void testFactory() {
        System.out.println("start ServiceFacadeTest testFactory");
        assertNotNull(serviceFacade);

        System.out.println("end ServiceFacadeTest testFactory");
    }
    
}