package org.solent.com528.project.impl.dao.jaxb;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.solent.com528.project.model.dao.PriceCalculatorDAO;
import org.solent.com528.project.model.dto.Rate;
import org.solent.com528.project.model.dto.PriceBand;
import org.solent.com528.project.model.dto.PricingDetails;

public class PriceCalculatorDAOJaxbImpl implements PriceCalculatorDAO {

    private final static Logger LOG = LogManager.getLogger(PriceCalculatorDAOJaxbImpl.class);

    private final String pricingDetailsFile;

    private Double offpeakPricePerZone = 0.0;
    private Double peakPricePerZone = 0.0;

    //Only adds new priceband and returns list sorted by timeInMinutes
    private TreeMap<Integer, PriceBand> priceBandTreeMap = new TreeMap();

    public PriceCalculatorDAOJaxbImpl(String pricingDetailsFile) {
        this.pricingDetailsFile = pricingDetailsFile;
        load();
    }

    private PriceBand getNearestBand(Date startTime) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(startTime);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);

        Integer key = priceBandTreeMap.floorKey(hour * 60 + minutes);
        return priceBandTreeMap.get(key);
    }

    @Override
    public synchronized Rate getRate(Date startTime) {
        return getNearestBand(startTime).getRate();
    }

    @Override
    public synchronized Double getPricePerZone(Date startTime) {
        Rate rate = getRate(startTime);
        if (Rate.OFFPEAK == rate) {
            return this.offpeakPricePerZone;
        } else {
            return this.peakPricePerZone;
        }
    }

    @Override
    public synchronized PricingDetails getPricingDetails() {
        PricingDetails pricingDetails = new PricingDetails();
        pricingDetails.setOffpeakPricePerZone(offpeakPricePerZone);
        pricingDetails.setPeakPricePerZone(peakPricePerZone);
        pricingDetails.setPriceBandList(new ArrayList(priceBandTreeMap.values()));
        return pricingDetails;
    }

    @Override
    public synchronized PricingDetails savePricingDetails(PricingDetails pricingDetails) {
        pricingDetailsSave(pricingDetails);
        save();
        return getPricingDetails();
    }

    private void pricingDetailsSave(PricingDetails pricingDetails) {
        this.offpeakPricePerZone = pricingDetails.getOffpeakPricePerZone();
        this.peakPricePerZone = pricingDetails.getPeakPricePerZone();
        priceBandTreeMap.clear();
        for (PriceBand pb : pricingDetails.getPriceBandList()) {
            priceBandTreeMap.put(pb.getTimeInMinutes(), pb);
        }
    }

    @Override
    public synchronized Double getOffpeakPricePerZone() {
        return offpeakPricePerZone;
    }

    @Override
    public synchronized void setOffpeakPricePerZone(Double offpeakPricePerZone) {
        this.offpeakPricePerZone = offpeakPricePerZone;
        save();
    }

    @Override
    public synchronized Double getPeakPricePerZone() {
        return peakPricePerZone;
    }

    @Override
    public synchronized void setPeakPricePerZone(Double peakPricePerZone) {
        this.peakPricePerZone = peakPricePerZone;
        save();
    }

    @Override
    public synchronized void addPriceBand(PriceBand priceBand) {
        priceBandTreeMap.put(priceBand.getTimeInMinutes(), priceBand);
        save();
    }

    @Override
    public synchronized void deletePriceBand(PriceBand priceBand) {
        priceBandTreeMap.remove(priceBand.getTimeInMinutes());
        save();
    }

    @Override
    public synchronized void deleteAll() {
        PricingDetails pd = new PricingDetails();
        savePricingDetails(pd);
        save();
    }

    private void load() {

        File file = new File(pricingDetailsFile);
        LOG.debug("loading pricingDetailsFile from " + file.getAbsolutePath());

        if (!file.exists()) {
            LOG.debug("pricingDetailsFile does not exist - creating new file ");
            deleteAll(); //This initialises with at least 0:00 time zone
        } else {
            try {
                //This contains a list of Jaxb annotated classes for the context to parse
                JAXBContext jaxbContext = JAXBContext.newInstance("org.solent.com528.project.model.dto");
                Unmarshaller jaxbUnMarshaller = jaxbContext.createUnmarshaller();

                PricingDetails pricingDetails = (PricingDetails) jaxbUnMarshaller.unmarshal(file);
                this.pricingDetailsSave(pricingDetails);

            } catch (JAXBException e) {
                throw new RuntimeException("problem testing jaxb marshalling", e);
            }
        }
    }

    private void save() {

        File file = new File(pricingDetailsFile);
        LOG.debug("saving pricing details to " + file.getAbsolutePath());

        //First remove old file before writing the new data
        if (file.exists()) {
            LOG.debug("deleting old file ");
            file.delete();
        }

        try {
            //Create parent directories if they are needed
            File parent = new File(file.getParent());
            LOG.debug("parent file: "+parent.getAbsolutePath());
            if (!parent.exists()) {
                parent.mkdirs();
            }

            //This contains a list of Jaxb annotated classes for the context to parse
            JAXBContext jaxbContext = JAXBContext.newInstance("org.solent.com528.project.model.dto");

            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            PricingDetails pricingDetails = getPricingDetails();

            //StringWriter is used to compare the received object
            if (LOG.isDebugEnabled()) {
                LOG.debug("pricingDetails to write to file: " + pricingDetails);
                StringWriter sw1 = new StringWriter();
                jaxbMarshaller.marshal(pricingDetails, sw1);
                LOG.debug("save writing pricingDetails to file: " + sw1);
            }

            jaxbMarshaller.marshal(pricingDetails, file);

        } catch (JAXBException e) {
            throw new RuntimeException("problem marshalling", e);
        }
    }
}
