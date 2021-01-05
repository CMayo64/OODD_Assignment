package org.solent.com528.project.clientservice.impl.test;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.*;
import org.solent.com528.project.clientservice.impl.TicketEncoderImpl;
import org.solent.com528.project.model.dto.Ticket;

public class TicketEncoderImplTest {

    final static Logger LOG = LogManager.getLogger(TicketEncoderImplTest.class);

    @Test
    public void testTicketEncodeValidate() {
        Ticket ticket = new Ticket();
        double cost = 5.50;
        ticket.setCost(cost);
        ticket.setStartStation("Waterloo");

        String encodedTicketStr = TicketEncoderImpl.encodeTicket(ticket);
        LOG.debug("encoded ticket to validate :\n" + encodedTicketStr);

        boolean valid = TicketEncoderImpl.validateTicket(encodedTicketStr);

        assertTrue(valid);
    }

    @Test
    public void testTicketEncodeInvalid() throws JAXBException {
        Ticket ticket = new Ticket();
        double cost = 6.25;
        ticket.setCost(cost);
        ticket.setStartStation("Waterloo");

        String encodedTicketStr = TicketEncoderImpl.encodeTicket(ticket);
        LOG.debug("encoded ticket to validate :\n" + encodedTicketStr);

        //Unmarshal encodedTicket ticket to xml
        JAXBContext jaxbContext = JAXBContext.newInstance("org.solent.com528.project.model.dto");
        Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();
        StringReader sr = new StringReader(encodedTicketStr);
        Ticket encodedTicket = (Ticket) jaxbUnMarshaller.unmarshal(sr);

        //Then re-marshal the encoded ticket and test it
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        StringWriter sw1 = new StringWriter();
        jaxbMarshaller.marshal(encodedTicket, sw1);
        String ticketXml = sw1.toString();
        LOG.debug("recovered ticket to validate :\n" + encodedTicketStr);
        
        //Check it is still valid
        boolean valid = TicketEncoderImpl.validateTicket(ticketXml);
        assertTrue(valid);

        //Change the cost and re marshal it
        cost = 2.00;
        encodedTicket.setCost(cost);
        
        sw1 = new StringWriter();
        jaxbMarshaller.marshal(encodedTicket, sw1);
        ticketXml = sw1.toString();
        LOG.debug("incorrect ticket to validate :\n" +  ticketXml);
        
        //Check that it is not valid now
        valid = TicketEncoderImpl.validateTicket(ticketXml);
        assertFalse(valid);

    }

}
