package com.fujixerox.aus.lombard;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Maybe move somewhere more re-usable
 *
 * @author Warwick Slade
 */
public class JaxbDateAdapter extends XmlAdapter<String, Date>
{
    public static final String DATETIME_FORMAT_INTERNAL = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    public static final String DATE_FORMAT_INTERNAL = "yyyy-MM-dd";

    private SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT_INTERNAL);

    public static String marshalDate(Date v)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_INTERNAL);
        return dateFormat.format(v);
    }

    public static String marshalDateTime(Date v)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT_INTERNAL);
        return dateFormat.format(v);
    }

    public static Date unmarshalDate(String v)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_INTERNAL);
        try
        {
            return dateFormat.parse(v);
        }
        catch (ParseException e)
        {
            throw new RuntimeException("Could not parse date",e);
        }
    }

    public static Date unmarshalDateTime(String v)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATETIME_FORMAT_INTERNAL);
        try
        {
            return dateFormat.parse(v);
        }
        catch (ParseException e)
        {
            throw new RuntimeException("Could not parse date",e);
        }
    }

    @Override
    public String marshal(Date v) throws Exception {
        return dateFormat.format(v);
    }

    @Override
    public Date unmarshal(String v) throws Exception {
        return dateFormat.parse(v);
    }
}
