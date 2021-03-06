package org.solent.com528.project.impl.dao.jpa.test;

import java.net.URL;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.solent.com528.project.impl.dao.jaxb.StationDAOJaxbImpl;
import org.solent.com528.project.impl.dao.jpa.DAOFactoryJPAImpl;
import org.solent.com528.project.model.dao.DAOFactory;
import org.solent.com528.project.model.dao.StationDAO;
import org.solent.com528.project.model.dao.TicketMachineDAO;
import org.solent.com528.project.model.dto.Station;
import org.solent.com528.project.model.dto.TicketMachine;

public class TicketMachineDAOJpaImplTest {

    final static Logger LOG = LogManager.getLogger(TicketMachineDAOJpaImplTest.class);

    private TicketMachineDAO ticketMachineDao = null;
    private StationDAO stationDao;

    private DAOFactory daoFactory = new DAOFactoryJPAImpl();

    @Before
    public void before() {
        ticketMachineDao = daoFactory.getTicketMachineDAO();
        stationDao = daoFactory.getStationDAO();
        assertNotNull(ticketMachineDao);
        assertNotNull(stationDao);
    }

    @Test
    public void createTicketMachinesDAOJpaImplTest() {
        LOG.debug("start of createTicketMachinesDAOJpaImplTest");
        //This test runs before the method
        LOG.debug("end of createTicketMachinesDAOJpaImplTest");
    }

    @Test
    public void createTicketMachinesTest() {
        LOG.debug("start of createTicketMachinesTest");

        ticketMachineDao.deleteAll();
        List<TicketMachine> testTicketMachineList = ticketMachineDao.findAll();
        assertTrue(testTicketMachineList.isEmpty());

        List<TicketMachine> dummyTicketMachineList = new ArrayList<TicketMachine>();

        for (Integer i = 0; i < 10; i++) {
            TicketMachine t = new TicketMachine();
            t = ticketMachineDao.save(t);
            assertNotNull(t.getId());
            dummyTicketMachineList.add(t);
        }
        testTicketMachineList = ticketMachineDao.findAll();
        assertEquals(dummyTicketMachineList.size(), testTicketMachineList.size());

        //Checks the ticket machines match
        for (TicketMachine dummyTicketMachine : dummyTicketMachineList) {
            String uuid = dummyTicketMachine.getUuid();
            TicketMachine foundTicketMachine = ticketMachineDao.findByUuid(uuid);
            assertNotNull(foundTicketMachine);
            assertEquals(dummyTicketMachine.getId(), foundTicketMachine.getId());
            assertEquals(dummyTicketMachine.getUuid(), foundTicketMachine.getUuid());
        }

        //Checks nothing is returned if no stations are applied
        testTicketMachineList = ticketMachineDao.findByStationName("Waterloo");
        assertTrue(testTicketMachineList.isEmpty());

        LOG.debug("end of createTicketMachinesTest(");
    }

    @Test
    public void createStationsWithTicketMachinesTest() {
        LOG.debug("start of createStationsWithTicketMachinesTest");

        ticketMachineDao.deleteAll();
        List<TicketMachine> testTicketMachineList = ticketMachineDao.findAll();
        assertTrue(testTicketMachineList.isEmpty());

        //This loads a list of stations to use in the tests
        URL res = getClass().getClassLoader().getResource("londonStations.xml");
        String fileName = res.getPath();
        System.out.println("loading from london underground fileName:   " + fileName);
        StationDAOJaxbImpl stationDAOjaxb = new StationDAOJaxbImpl(fileName);
        List<Station> dummyStationList = stationDAOjaxb.findAll();
        
        //Creates one ticket machine per station
        for(Station dummyStation: dummyStationList){
            dummyStation = stationDao.save(dummyStation);
            TicketMachine exampleTicketMachine = new TicketMachine();
            exampleTicketMachine.setStation(dummyStation);
            ticketMachineDao.save(exampleTicketMachine);
        }
        
        testTicketMachineList = ticketMachineDao.findAll();
        assertEquals(dummyStationList.size(),testTicketMachineList.size());
        
        testTicketMachineList = ticketMachineDao.findByStationName("Acton Town");
        assertEquals(1,testTicketMachineList.size());
        assertTrue("Acton Town".equals(testTicketMachineList.get(0).getStation().getName()));
        assertEquals(Integer.valueOf(3),testTicketMachineList.get(0).getStation().getZone());
        

        LOG.debug("end of createStationsWithTicketMachinesTest");
    }

}
