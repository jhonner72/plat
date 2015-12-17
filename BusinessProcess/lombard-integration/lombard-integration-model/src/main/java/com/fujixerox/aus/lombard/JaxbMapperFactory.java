package com.fujixerox.aus.lombard;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.impl.ClassNameIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

import javax.xml.bind.annotation.XmlType;
import java.util.Collection;

/**
 * Created by warwick on 12/03/2015.
 */
public class JaxbMapperFactory
{
    public static ObjectMapper createWithoutAnnotations() {
        ObjectMapper objectMapper = new ObjectMapper();

        JaxbAnnotationModule jaxbAnnotationModule = new JaxbAnnotationModule();
        objectMapper.registerModule(jaxbAnnotationModule);

        return objectMapper;
    }

    public static ObjectMapper createWithAnnotations() {
        ObjectMapper objectMapper = new ObjectMapper();

        JaxbAnnotationModule jaxbAnnotationModule = new JaxbAnnotationModule();
        objectMapper.registerModule(jaxbAnnotationModule);
        //objectMapper.setDateFormat(new SimpleDateFormat(StringUtilsShared.DATE_FORMAT_INTERNAL));
        TypeResolverBuilder<?> typer = new Jaxb2TypeResolverBuilder(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        objectMapper.setDefaultTyping(typer);

        return objectMapper;
    }

    public static class Jaxb2TypeResolverBuilder extends ObjectMapper.DefaultTypeResolverBuilder
    {
        public Jaxb2TypeResolverBuilder(ObjectMapper.DefaultTyping defaultTyping, String propertyName)
        {
            super(defaultTyping);
            init(JsonTypeInfo.Id.CLASS, null);
            inclusion(JsonTypeInfo.As.PROPERTY);
            typeProperty(propertyName);
        }

        protected TypeIdResolver idResolver(MapperConfig<?> config, JavaType baseType,
                                            Collection<NamedType> subtypes, boolean forSer, boolean forDeser)
        {
            return new Jaxb2IdResolver(baseType, config.getTypeFactory());
        }
    }

    public static class Jaxb2IdResolver extends ClassNameIdResolver
    {
        public Jaxb2IdResolver(JavaType baseType, TypeFactory typeFactory)
        {
            super(baseType, typeFactory);
        }

        @Override
        public String idFromValue(Object value) {
            return idFromInternal(value, value.getClass());
        }

        @Override
        public String idFromValueAndType(Object value, Class<?> type) {
            return idFromInternal(value, type);
        }

        protected String idFromInternal(Object value, Class<?> cls)
        {
            // if jaxb type then type it as its interface,
            // else use default super implementation (super._idFrom())
            XmlType xmlType = cls.getAnnotation(XmlType.class);
            if (xmlType == null)
            {
                return super._idFrom(value, cls);
            }
            String typeName = xmlType.name();
            String typePackage = cls.getPackage().getName();
            if (typePackage != null && typePackage.endsWith(".impl"))
            {
                typePackage = typePackage.substring(0, typePackage.length() - 5);
            }
            return typePackage + "." + typeName;
        }
    }
}
