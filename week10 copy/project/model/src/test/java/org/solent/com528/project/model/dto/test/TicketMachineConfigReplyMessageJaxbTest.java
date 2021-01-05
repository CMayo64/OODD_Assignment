package org.solent.com528.project.model.dto.test;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.solent.com528.project.model.dto.Rate;
import org.solent.com528.project.model.dto.PriceBand;
import org.solent.com528.project.model.dto.PricingDetails;
import org.solent.com528.project.model.dto.ReplyMessage;
import org.solent.com528.project.model.dto.Station;
import org.solent.com528.project.model.dto.Ticket;
import org.solent.com528.project.model.dto.TicketMachineConfig;

public class TicketMachineConfigReplyMessageJaxbTest {

    @Test
    public void testTransactionJaxb() {

        try {

            //Test the file we will write and read 
            File file = new File("target/testTicketMachineConfigData.xml");
            System.out.println("writing test file to " + file.getAbsolutePath());

            //This contains a list of Jaxb annotated classes for the context to parse
            JAXBContext jaxbContext = JAXBContext.newInstance("org.solent.com528.project.model.dto");

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            ReplyMessage replyMessage = new ReplyMessage();
            
            replyMessage.setCode(200);
            replyMessage.setDebugMessage("no problem debug message");
            
            TicketMachineConfig ticketMachineConfig = new TicketMachineConfig();
            replyMessage.setTicketMachineConfig(ticketMachineConfig);
            PricingDetails pricingDetails = new PricingDetails();
            pricingDetails.setOffpeakPricePerZone(2.50);
            pricingDetails.setPeakPricePerZone(5.00);
            
            List<PriceBand> priceBandList = new ArrayList<PriceBand>();
            PriceBand pb1 = new PriceBand();
            pb1.setRate(Rate.PEAK);
            pb1.setHour(0);
            pb1.setMinutes(0);
            priceBandList.add(pb1);
            
            pricingDetails.setPriceBandList(priceBandList);
            ticketMachineConfig.setPricingDetails(pricingDetails);
            
            List<Station> stationList= new ArrayList<Station>();
            Station londonBridgeStation = new Station();
            londonBridgeStation.setName("London Bridge");
            londonBridgeStation.setZone(1);
            stationList.add(londonBridgeStation);
            
            ticketMachineConfig.setStationList(stationList);
            ticketMachineConfig.setStationName("Waterloo");
            ticketMachineConfig.setUuid(UUID.randomUUID().toString());
            
            //Create an XML from the object
            //And marshal the object lists to System.out and a file
            jaxbMarshaller.marshal(replyMessage, System.out);
            jaxbMarshaller.marshal(replyMessage, file);

            //A StringWriter is used to compare the received object
            StringWriter sw1 = new StringWriter();
            jaxbMarshaller.marshal(replyMessage, sw1);

            //Having written the file now I read in the file for test
            Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();
            ReplyMessage receivedTransactionResult = (ReplyMessage) jaxbUnMarshaller.unmarshal(file);

            StringWriter sw2 = new StringWriter();
            jaxbMarshaller.marshal(receivedTransactionResult, sw2);

            //Check that transmitted and recieved messages are the same
            assertEquals(sw1.toString(), sw2.toString());

        } catch (JAXBException e) {
            throw new RuntimeException("problem testing jaxb marshalling", e);
        }
    }
}
