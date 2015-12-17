package com.fujixerox.aus.inclearings;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import sun.misc.BASE64Decoder;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

/**
 * Enhanced IE Parser
 * 
 * User: Eloisa.Redubla 
 * Date: 21/04/15 
 * Time: 10:50 AM 
 * Modified : Alex Park on 11/11/2015
 * 
 */
public class ImageExchangeFileXmlParser {

	protected Map<String, String> headerMap = null;
	protected Map<String, Map<String, String>> itemMap = null;
	protected Map<String, String> trailerMap;
	public static final String VOUCHER_FRONT_IMAGE_PATTERN = "VOUCHER_%s_%s_FRONT.JPG";
	public static final String VOUCHER_REAR_IMAGE_PATTERN = "VOUCHER_%s_%s_REAR.JPG";

	public static final String TRANSACTION_ID = "Transaction Identifier";
	public static final String TRANSMISSION_DATE = "Transmission Date";

	public ImageExchangeFileXmlParser() {
		headerMap = new HashMap<String, String>();
		itemMap = new HashMap<String, Map<String, String>>();
		trailerMap = new HashMap<String, String>();
	}

	public Map<String, Map<String, String>> getItemMap() {
		return this.itemMap;
	}

	public Map<String, String> getMapHeader() {
		return this.headerMap;
	}

	public Map<String, String> getMapTrailer() {
		return this.trailerMap;
	}

	public void parseIeXmlFile(File file) throws Exception {
        InputStream in = null;
        XMLStreamReader streamReader = null;
        try {
            in = new FileInputStream(file);
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            inputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            inputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
			streamReader = inputFactory.createXMLStreamReader(in);
			setHeader(streamReader);
			setItems(streamReader, file.getAbsolutePath());
			setTrailer(streamReader);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
            if (in != null) {
                in.close();
            }
            if (streamReader != null) {
                streamReader.close();
            }
        }
	}

	private void setHeader(XMLStreamReader streamReader) {
		try {
			streamReader.nextTag(); // Advance to "xml" element
			streamReader.nextTag(); // Advance to "tran" element

			if (streamReader.hasNext() && streamReader.isStartElement()
					&& "tranheader".equals(streamReader.getLocalName())) {
				streamReader.nextTag(); // Advance to "tranheader" element

				while (streamReader.hasNext()) {
					if (streamReader.isStartElement()) {
						switch (streamReader.getLocalName()) {
						case "rfield":
							headerMap.put(
									streamReader
											.getAttributeValue(null, "name"),
									streamReader.getAttributeValue(null,
											"value").trim());
							break;
						}
					} else if (streamReader.isEndElement()) {
						switch (streamReader.getLocalName()) {
						case "tranheader":
							streamReader.nextTag(); // Advance to "tranheader"
													// element
							return;
						}
					}
					streamReader.next();
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	private void setItems(XMLStreamReader streamReader, String path) {
		try {
			while (streamReader.hasNext()) {

				Map<String, String> rfieldMap = null;
				while (streamReader.hasNext()) {
					if (streamReader.isStartElement()) {
						switch (streamReader.getLocalName()) {
						case "item":
							rfieldMap = new HashMap<String, String>();
							break;
						case "rfield":
                            if (rfieldMap != null) {
                                rfieldMap.put(
                                        streamReader.getAttributeValue(null, "name"),
                                        streamReader.getAttributeValue(null, "value").trim());
                            }
							break;

						case "frontimage":
						case "rearimage":
                            if (rfieldMap != null) {
                                String processingDate = rfieldMap.get(TRANSMISSION_DATE);
                                processingDate = processingDate.substring(6)
                                        + processingDate.substring(4, 6)
                                        + processingDate.substring(0, 4);

                                decodeToImage(streamReader.getElementText(), path,
                                        streamReader.getLocalName(),
                                        rfieldMap.get(TRANSACTION_ID),
                                        processingDate);
                                break;
                            }
						case "trantrailer":

							return;
						}
					} else if (streamReader.isEndElement()) {
						switch (streamReader.getLocalName()) {
						case "item":
                            if (rfieldMap != null) {
                                getItemMap().put((String) rfieldMap.get(TRANSACTION_ID), rfieldMap);
                                break;
                            }
						}
					}
					streamReader.next();
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}
	
	private void setTrailer(XMLStreamReader streamReader) {
		try {
			if (streamReader.hasNext() && streamReader.isStartElement()
					&& "trantrailer".equals(streamReader.getLocalName())) {
				streamReader.nextTag(); // Advance to "trantrailer" element

				while (streamReader.hasNext()) {
					if (streamReader.isStartElement()) {
						switch (streamReader.getLocalName()) {
						case "rfield":
							trailerMap.put(streamReader.getAttributeValue(null, "name"),
									streamReader.getAttributeValue(null, "value").trim());
							break;
						}
					} else if (streamReader.isEndElement()) {
						switch (streamReader.getLocalName()) {
						case "trantrailer":
							streamReader.nextTag(); // Advance to "trantrailer" // element
							return;
						}
					}
					streamReader.next();
				}
			}
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
	}

	private void decodeToImage(String imageString, String imageLocation, String side, String drn, String procDate) {
		File file = new File(imageLocation.substring(0, imageLocation.lastIndexOf(File.separator)));
		if (!file.exists()) {
			throw new RuntimeException("Image directory does not exist:"
					+ file.getAbsolutePath());
		}

		if (side == "frontimage") {
			file = new File(file, String.format(VOUCHER_FRONT_IMAGE_PATTERN, procDate, drn));
		} else {
			file = new File(file, String.format(VOUCHER_REAR_IMAGE_PATTERN, procDate, drn));
		}
        
		try (OutputStream stream = new BufferedOutputStream(new FileOutputStream(file))) {
			BASE64Decoder decoder = new BASE64Decoder();
			byte[] data = decoder.decodeBuffer(imageString);
			stream.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
