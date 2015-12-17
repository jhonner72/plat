package com.fujixerox.aus.asset.model.beans.generated;

import java.lang.reflect.Field;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.fujixerox.aus.asset.api.util.CoreUtils;

/**
 * @author Andrey B. Panfilov <andrew@panfilov.tel>
 */
@XmlTransient
public class ExtensionBase {

    @SuppressWarnings("unchecked")
    protected <T> T getAttribute(String name) {
        try {
            return (T) getFieldByName(name).get(this);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    protected <T> void setAttribute(String name, T value) {
        try {
            getFieldByName(name).set(this, value);
        } catch (IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Field getFieldByName(String name) {
        for (Field field : getClass().getDeclaredFields()) {
            XmlAttribute annotation = field.getAnnotation(XmlAttribute.class);
            if (annotation == null) {
                continue;
            }
            if (CoreUtils.inList(name, annotation.name(), field.getName())) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new IllegalArgumentException("No field with name " + name);
    }

}
