package ch.niceneasy.openstack.client.fx.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import com.woorea.openstack.swift.model.ObjectDownload;

/**
 * The Class ObjectDownloadMessageBodyReader.
 * 
 * @author Daniele
 */
public class ObjectDownloadMessageBodyReader implements
		MessageBodyReader<ObjectDownload> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.MessageBodyReader#isReadable(java.lang.Class,
	 * java.lang.reflect.Type, java.lang.annotation.Annotation[],
	 * javax.ws.rs.core.MediaType)
	 */
	@Override
	public boolean isReadable(Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return type == ObjectDownload.class;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ws.rs.ext.MessageBodyReader#readFrom(java.lang.Class,
	 * java.lang.reflect.Type, java.lang.annotation.Annotation[],
	 * javax.ws.rs.core.MediaType, javax.ws.rs.core.MultivaluedMap,
	 * java.io.InputStream)
	 */
	@Override
	public ObjectDownload readFrom(Class<ObjectDownload> type,
			Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		ObjectDownload objectDownload = new ObjectDownload();
		objectDownload.setInputStream(copyStream(entityStream));
		return objectDownload;

	}

	/**
	 * Copy stream.
	 * 
	 * @param stream
	 *            the stream
	 * @return the input stream
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private InputStream copyStream(InputStream stream) throws IOException {
		byte[] entity = new byte[4096];
		int entitySize = 0;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		while ((entitySize = stream.read(entity)) != -1) {
			baos.write(entity, 0, entitySize);
		}
		return new ByteArrayInputStream(baos.toByteArray());
	}
}
