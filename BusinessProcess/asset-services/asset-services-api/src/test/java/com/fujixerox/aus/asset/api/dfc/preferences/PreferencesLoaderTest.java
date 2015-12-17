package com.fujixerox.aus.asset.api.dfc.preferences;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.documentum.fc.common.DfPreferences;
import com.fujixerox.aus.asset.api.dfc.preferences.PreferencesLoader;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
public class PreferencesLoaderTest {

    private List<String> _docbrokerHostsList;

    private String[] _docbrokerHostsArray;

    private Object[] _docbrokerHostsArrayObj;

    private List<Integer> _docbrokerPortsList;

    private Integer[] _docbrokerPortsArray;

    private Object[] _docbrokerPortsArrayObj;

    private int[] _docbrokerPortsArrayPrim;

    private Integer _docbrokerTimeout;

    private int _primitiveDocbrokerTimeout;

    @Before
    public void setUp() {
        _docbrokerHostsList = Arrays.asList("docbroker1", "docbroker2");
        _docbrokerHostsArray = new String[] {"docbroker3", "docbroker4" };
        _docbrokerHostsArrayObj = new Object[] {"docbroker5", "docbroker6" };

        _docbrokerPortsList = Arrays.asList(1489, 1589);
        _docbrokerPortsArray = new Integer[] {1689, 1789 };
        _docbrokerPortsArrayObj = new Object[] {1889, 1989 };
        _docbrokerPortsArrayPrim = new int[] {2089, 2189 };
    }

    @Test
    public void testLists() {
        new PreferencesLoader(new Properties() {
            {
                put(DfPreferences.DFC_DOCBROKER_HOST, _docbrokerHostsList);
                put(DfPreferences.DFC_DOCBROKER_PORT, _docbrokerPortsList);
            }
        }).load();

        DfPreferences preferences = DfPreferences.getInstance();
        Assert.assertEquals(_docbrokerHostsList.get(0), preferences
                .getDocbrokerHost(0));
        Assert.assertEquals(_docbrokerHostsList.get(1), preferences
                .getDocbrokerHost(1));
        Assert.assertEquals(_docbrokerPortsList.get(0).intValue(), preferences
                .getDocbrokerPort(0));
        Assert.assertEquals(_docbrokerPortsList.get(1).intValue(), preferences
                .getDocbrokerPort(1));
    }

    @Test
    public void testArrays() {
        new PreferencesLoader(new Properties() {
            {
                put(DfPreferences.DFC_DOCBROKER_HOST, _docbrokerHostsArray);
                put(DfPreferences.DFC_DOCBROKER_PORT, _docbrokerPortsArray);
            }
        }).load();

        DfPreferences preferences = DfPreferences.getInstance();
        Assert.assertEquals(_docbrokerHostsArray[0], preferences
                .getDocbrokerHost(0));
        Assert.assertEquals(_docbrokerHostsArray[1], preferences
                .getDocbrokerHost(1));
        Assert.assertEquals(_docbrokerPortsArray[0].intValue(), preferences
                .getDocbrokerPort(0));
        Assert.assertEquals(_docbrokerPortsArray[1].intValue(), preferences
                .getDocbrokerPort(1));
    }

    @Test
    public void testArrayObj() {
        new PreferencesLoader(new Properties() {
            {
                put(DfPreferences.DFC_DOCBROKER_HOST, _docbrokerHostsArrayObj);
                put(DfPreferences.DFC_DOCBROKER_PORT, _docbrokerPortsArrayObj);
            }
        }).load();

        DfPreferences preferences = DfPreferences.getInstance();
        Assert.assertEquals(_docbrokerHostsArrayObj[0], preferences
                .getDocbrokerHost(0));
        Assert.assertEquals(_docbrokerHostsArrayObj[1], preferences
                .getDocbrokerHost(1));
        Assert.assertEquals(_docbrokerPortsArrayObj[0], preferences
                .getDocbrokerPort(0));
        Assert.assertEquals(_docbrokerPortsArrayObj[1], preferences
                .getDocbrokerPort(1));
    }

    @Test
    public void testArrayPrim() {
        new PreferencesLoader(new Properties() {
            {
                put(DfPreferences.DFC_DOCBROKER_HOST, _docbrokerHostsArrayObj);
                put(DfPreferences.DFC_DOCBROKER_PORT, _docbrokerPortsArrayPrim);
            }
        }).load();

        DfPreferences preferences = DfPreferences.getInstance();
        Assert.assertEquals(_docbrokerHostsArrayObj[0], preferences
                .getDocbrokerHost(0));
        Assert.assertEquals(_docbrokerHostsArrayObj[1], preferences
                .getDocbrokerHost(1));
        Assert.assertEquals(_docbrokerPortsArrayPrim[0], preferences
                .getDocbrokerPort(0));
        Assert.assertEquals(_docbrokerPortsArrayPrim[1], preferences
                .getDocbrokerPort(1));
    }

    @Test
    public void testReadonlyPreference() {
        new PreferencesLoader(new Properties() {
            {
                put(DfPreferences.DFC_PRIVILEGE_ENABLE, false);
            }
        }).load();

        DfPreferences preferences = DfPreferences.getInstance();
        Assert.assertTrue(preferences.isPrivilegeEnabled());
    }

    @Test
    public void testReadWritePreference() {
        new PreferencesLoader(new Properties() {
            {
                put(DfPreferences.DFC_VERIFY_REGISTRATION, false);
            }
        }).load();

        DfPreferences preferences = DfPreferences.getInstance();
        Assert.assertFalse(preferences.getVerifyRegistration());
    }

}
