package org.solent.com528.project.model.dto.test;


import java.io.File;
import java.io.StringWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.solent.com528.project.model.dto.Ticket;

public class TicketModelJaxbTest {

    @Test
    public void testTransactionJaxb() {

        try {

            //Test the file we will write and read. 
            File file = new File("target/testTicketTransactionData.xml");
            System.out.println("writing test file to " + file.getAbsolutePath());

            //This contains a list of Jaxb annotated classes for the context to parse
            JAXBContext jaxbContext = JAXBContext.newInstance("org.solent.com528.project.model.dto");

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            Ticket ticket = new Ticket();
            double cost = 1.5;
            ticket.setCost(cost);

            //Create XML from the object
            //And marshal the object lists to System.out and a file 
            jaxbMarshaller.marshal(ticket, System.out);
            jaxbMarshaller.marshal(ticket, file);

            //A StringWriter is used to compare the received object
            StringWriter sw1 = new StringWriter();
            jaxbMarshaller.marshal(ticket, sw1);

            //Once written the file I read in the file for test
            Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();
            Ticket receivedTransactionResult = (Ticket) jaxbUnMarshaller.unmarshal(file);

            StringWriter sw2 = new StringWriter();
            jaxbMarshaller.marshal(receivedTransactionResult, sw2);

            //Check the transmitted and recieved messages are the same
            assertEquals(sw1.toString(), sw2.toString());

        } catch (JAXBException e) {
            throw new RuntimeException("problem testing jaxb marshalling", e);
        }
    }
}
