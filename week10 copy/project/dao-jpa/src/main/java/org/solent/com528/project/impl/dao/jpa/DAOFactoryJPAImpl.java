package org.solent.com528.project.impl.dao.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.solent.com528.project.model.dao.DAOFactory;
import org.solent.com528.project.model.dao.PriceCalculatorDAO;
import org.solent.com528.project.model.dao.StationDAO;
import org.solent.com528.project.model.dao.TicketMachineDAO;

public class DAOFactoryJPAImpl implements DAOFactory {

    protected static final String PERSISTENCE_UNIT_NAME = "modelPersistence";
    protected static EntityManagerFactory factory;
    protected static EntityManager em;
    protected static TicketMachineDAO ticketMachineDAO;
    protected static StationDAO stationDAO;

    static {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        em = factory.createEntityManager();

        //All DAO's must share the same entity manager (em)
        ticketMachineDAO = new TicketMachineDAOJpaImpl(em);
        stationDAO = new StationDAOJpaImpl(em);
    }

    @Override
    public void shutDown() {
        if (em != null) synchronized (this) {
            if (em != null) {
                em.close();
            }
        }
    }

    @Override
    public TicketMachineDAO getTicketMachineDAO() {
        return ticketMachineDAO;
    }

    @Override
    public StationDAO getStationDAO() {
        return stationDAO;
    }

    @Override
    public PriceCalculatorDAO getPriceCalculatorDAO() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
