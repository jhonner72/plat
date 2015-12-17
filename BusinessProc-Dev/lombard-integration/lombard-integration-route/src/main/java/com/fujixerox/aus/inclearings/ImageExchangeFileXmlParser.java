package com.fujixerox.aus.inclearings;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import sun.misc.BASE64Decoder;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: Eloisa.Redubla
 * Date: 21/04/15
 * Time: 10:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageExchangeFileXmlParser extends DefaultHandler {

    protected Map<String, String> mapHeader;
    protected Map<String, List<Map<String, String>>> mapItemList;
    protected Map<String, String> mapTrailer;
    public static final String VOUCHER_FRONT_IMAGE_PATTERN = "VOUCHER_%s_%s_FRONT.JPG";
    public static final String VOUCHER_REAR_IMAGE_PATTERN = "VOUCHER_%s_%s_REAR.JPG";

    public Map<String, List<Map<String, String>>> getMapItemList() {
        if (mapItemList == null) {
            mapItemList = new HashMap<String, List<Map<String, String>>>();
        }
        return this.mapItemList;
    }

    public Map<String, String> getMapHeader() {
        if (mapHeader == null) {
            mapHeader = new HashMap<String, String>();
        }
        return this.mapHeader;
    }

    public Map<String, String> getMapTrailer() {
        if (mapTrailer == null) {
            mapTrailer = new HashMap<String, String>();
        }
        return this.mapTrailer;
    }

    public void parseIeXmlFile(File file) {
        mapHeader = new HashMap();
        mapTrailer = new HashMap();

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();

            SAXReader saxReader = new SAXReader(parser.getXMLReader());
            Document document = saxReader.read(file);
            setMapHeader(document);
            setMapItemList(document, file.getAbsolutePath());
            setMapTrailer(document);

        }catch(ParserConfigurationException pce) {
            pce.printStackTrace();
        }catch(DocumentException de) {
            de.printStackTrace();
        }catch(SAXException se) {
            se.printStackTrace();
        }
    }

    private void setMapHeader(Document document) {
        Element tranElement = document.getRootElement();
        for (Iterator iterator = tranElement.elementIterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            if (element.getName() == "tranheader") {
                for (Iterator iterator2 = element.elementIterator(); iterator2.hasNext();) {
                    Element rfieldElement = (Element) iterator2.next();
                    mapHeader.put(rfieldElement.attributeValue("name"), rfieldElement.attributeValue("value").trim());
                }
                break;
            }
        }
    }

    private void setMapItemList(Document document, String path) {
        int ctr = 1;
        String processingDate = "";

        Element tranElement = document.getRootElement();
        for (Iterator iterator = tranElement.elementIterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            if (element.getName() == "item") {
                String transactionId = String.valueOf(ctr++);
                List rfieldList = new ArrayList<Map<String, String>>();
                for (Iterator iter = element.elementIterator(); iter.hasNext();) {
                    Element rfieldElement = (Element) iter.next();
                    if (rfieldElement.getName() == "rfield") {
                        if (rfieldElement.attributeValue("name").equalsIgnoreCase("Transaction Identifier")) {
                            transactionId = rfieldElement.attributeValue("value").trim();
                        }
                        if (rfieldElement.attributeValue("name").equalsIgnoreCase("Transmission Date")) {
                            processingDate = rfieldElement.attributeValue("value").trim();
                            processingDate = processingDate.substring(6)+processingDate.substring(4,6)+processingDate.substring(0,4);
                        }
                        Map<String, String> mapItem = new HashMap<String, String>();
                        mapItem.put(rfieldElement.attributeValue("name"), rfieldElement.attributeValue("value").trim());
                        rfieldList.add(mapItem);
                    } else if (rfieldElement.getName() == "image") {
                        for (Iterator i = rfieldElement.elementIterator(); i.hasNext();) {
                            Element imageElement = (Element) i.next();
                            decodeToImage(imageElement.getStringValue(), path, imageElement.getName(), transactionId, processingDate);
                        }
                    }
                }
                getMapItemList().put(transactionId, rfieldList);
            }
        }
    }

    private void setMapTrailer(Document document) {
        Element tranElement = document.getRootElement();
        for (Iterator iterator = tranElement.elementIterator(); iterator.hasNext();) {
            Element element = (Element) iterator.next();
            if (element.getName() == "trantrailer") {
                for (Iterator iterator2 = element.elementIterator(); iterator2.hasNext();) {
                    Element rfieldElement = (Element) iterator2.next();
                    mapTrailer.put(rfieldElement.attributeValue("name"), rfieldElement.attributeValue("value").trim());
                }
                break;
            }
        }
    }

    private void decodeToImage(String imageString, String imageLocation, String side, String drn, String procDate) {
        File file = new File(imageLocation.substring(0,imageLocation.lastIndexOf(File.separator)));
        if (!file.exists()) {
            throw new RuntimeException("Image directory does not exist:" + file.getAbsolutePath());
        }

        if (side == "frontimage") {
            file = new File(file, String.format(VOUCHER_FRONT_IMAGE_PATTERN, procDate, drn));
        } else {
            file = new File(file, String.format(VOUCHER_REAR_IMAGE_PATTERN, procDate, drn));
        }

        byte[] imageByte;
        try
        {
            BASE64Decoder decoder = new BASE64Decoder();
            imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            BufferedImage image = ImageIO.read(bis);
            bis.close();

            setDpi(image, file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void setDpi(BufferedImage image, File file) {
        try {
            ImageWriter imageWriter = ImageIO.getImageWritersBySuffix("JPG").next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(file);
            imageWriter.setOutput(ios);
            ImageWriteParam jpegParams = imageWriter.getDefaultWriteParam();

            IIOMetadata data = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(image), jpegParams);
            org.w3c.dom.Element tree = (org.w3c.dom.Element)data.getAsTree("javax_imageio_jpeg_image_1.0");
            org.w3c.dom.Element jfif = (org.w3c.dom.Element)tree.getElementsByTagName("app0JFIF").item(0);
            jfif.setAttribute("Xdensity", Integer.toString(100));
            jfif.setAttribute("Ydensity", Integer.toString(100));
            jfif.setAttribute("resUnits", "1"); // density is dots per inch
            data.mergeTree("javax_imageio_jpeg_image_1.0",tree);

            imageWriter.write(data, new IIOImage(image, null, data), jpegParams);
            ios.close();
            imageWriter.dispose();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
