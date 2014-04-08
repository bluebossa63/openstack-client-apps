/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.client.fx.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.jboss.resteasy.client.core.executors.ApacheHttpClient4Executor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.connector.RESTEasyConnector;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.authentication.TokenAuthentication;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.keystone.utils.KeystoneUtils;
import com.woorea.openstack.swift.Swift;

/**
 * @author Daniele
 */

public class OpenStackClientService {

	private static OpenStackClientService INSTANCE;

	public static OpenStackClientService getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new OpenStackClientService();
		}
		return INSTANCE;
	}

	private RESTEasyConnector connector;
	private Keystone keystone;
	private Keystone adminKeystone;
	private Access access;
	private Access adminAccess;
	private Map<String, Swift> swiftMap = new HashMap<String, Swift>();
	private ObjectMapper defaultMapper = new ObjectMapper();
	private ObjectMapper wrappedMapper = new ObjectMapper();
	private String keystoneAuthUrl = "http://192.168.0.20:5000/v2.0/";
	private String keystoneAdminAuthUrl = "http://192.168.0.20:35357/v2.0/";
	private String keystoneUsername = "admin";
	private String keystonePassword = "adminPassword";
	private String keystoneEndpoint = "http://192.168.0.20:8776/v2.0/";
	private String tenantName = "demo";
	private String novaEndpoint = "http://192.168.0.20:8774/v2/";
	private String ceilometerEndpoint = "";

	private OpenStackClientService() {
		defaultMapper.setSerializationInclusion(Include.NON_NULL);
		defaultMapper.enable(SerializationFeature.INDENT_OUTPUT);
		defaultMapper
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		defaultMapper
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		wrappedMapper.setSerializationInclusion(Include.NON_NULL);
		wrappedMapper.enable(SerializationFeature.INDENT_OUTPUT);
		wrappedMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);
		wrappedMapper.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		wrappedMapper
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
		wrappedMapper
				.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		try {
			SSLSocketFactory sf = new SSLSocketFactory(new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
					return true;
				}
			}, new AllowAllHostnameVerifier());

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", 7443, sf));
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					registry);
			ApacheHttpClient4Executor executor = new ApacheHttpClient4Executor(
					new DefaultHttpClient(ccm));
			connector = new RESTEasyConnector(executor);
			RESTEasyConnector.getProviderFactory().addMessageBodyReader(ObjectDownloadMessageBodyReader.class);
			//ResteasyProviderFactory.getInstance().registerProvider(); 
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public ObjectMapper getContext(Class<?> type) {
		return type.getAnnotation(JsonRootName.class) == null ? defaultMapper
				: wrappedMapper;
	}

	public String getKeystoneAuthUrl() {
		return keystoneAuthUrl;
	}

	public void setKeystoneAuthUrl(String keystoneAuthUrl) {
		this.keystoneAuthUrl = keystoneAuthUrl;
	}

	public String getKeystoneAdminAuthUrl() {
		return keystoneAdminAuthUrl;
	}

	public void setKeystoneAdminAuthUrl(String keystoneAdminAuthUrl) {
		this.keystoneAdminAuthUrl = keystoneAdminAuthUrl;
	}

	public String getKeystoneUsername() {
		return keystoneUsername;
	}

	public void setKeystoneUsername(String keystoneUsername) {
		this.keystoneUsername = keystoneUsername;
	}

	public String getKeystonePassword() {
		return keystonePassword;
	}

	public void setKeystonePassword(String keystonePassword) {
		this.keystonePassword = keystonePassword;
	}

	public String getKeystoneEndpoint() {
		return keystoneEndpoint;
	}

	public void setKeystoneEndpoint(String keystoneEndpoint) {
		this.keystoneEndpoint = keystoneEndpoint;
	}

	public String getTenantName() {
		return tenantName;
	}

	public void setTenantName(String tenantName) {
		this.tenantName = tenantName;
	}

	public String getNovaEndpoint() {
		return novaEndpoint;
	}

	public void setNovaEndpoint(String novaEndpoint) {
		this.novaEndpoint = novaEndpoint;
	}

	public String getCeilometerEndpoint() {
		return ceilometerEndpoint;
	}

	public void setCeilometerEndpoint(String ceilometerEndpoint) {
		this.ceilometerEndpoint = ceilometerEndpoint;
	}

	public Keystone getKeystone() {
		if (keystone == null) {
			keystone = new Keystone(getKeystoneAuthUrl(), connector);
			try {
				keystone.setTokenProvider(new OpenStackSimpleTokenProvider(
						getAccess().getToken().getId()));
			} catch (RuntimeException e) {
				keystone = null;
				throw e;
			}
		}
		return keystone;
	}

	public Keystone getAdminKeystone() {
		if (adminKeystone == null) {
			adminKeystone = new Keystone(getKeystoneAdminAuthUrl(), connector);
			adminKeystone.token(getAdminAccess().getToken().getId());
		}
		return adminKeystone;
	}

	public Access getAccess() {
		if (access == null) {
			access = getKeystone()
					.tokens()
					.authenticate(
							new UsernamePassword(getKeystoneUsername(),
									getKeystonePassword())).execute();
		}
		return access;
	}

	public Access getAdminAccess() {
		if (adminAccess == null) {
			adminAccess = getKeystone()
					.tokens()
					.authenticate(
							new TokenAuthentication(getAccess().getToken()
									.getId())).withTenantName("admin")
					.execute();

		}
		return adminAccess;
	}

	public Swift getSwift(String tenantId) {
		Swift swift = swiftMap.get(tenantId);
		if (swift == null) {
			Access access = getKeystone()
					.tokens()
					.authenticate(
							new TokenAuthentication(getAccess().getToken()
									.getId())).withTenantId(tenantId).execute();
			// swift = new Swift(
			// KeystoneUtils.findEndpointURL(access.getServiceCatalog(),
			// "object-store", null, "public"), connector);
			String url = KeystoneUtils.findEndpointURL(
					access.getServiceCatalog(), "object-store", null, "public");
			// TODO: replace with configuration on server!
			url = url.replace("http://192.168.0.7:8080",
					"https://openstack.niceneasy.ch:7443/swift");
			swift = new Swift(url, connector);
			swift.setTokenProvider(new OpenStackSimpleTokenProvider(access
					.getToken().getId()));
			swiftMap.put(tenantId, swift);
		}
		return swift;
	}

	public static ObjectMapper mapper(Class<?> type) {
		return OpenStackClientService.getInstance().getContext(type);
	}

	public static InputStream copyStream(InputStream stream) throws IOException {
		byte[] entity = new byte[4096];
		int entitySize = 0;
		ByteArrayBuilder baos = new ByteArrayBuilder();
		while ((entitySize = stream.read(entity)) != -1) {
			baos.write(entity, 0, entitySize);
		}
		InputStream is = new ByteArrayInputStream(baos.toByteArray());
		baos.close();
		return is;
	}

	public static void copy(InputStream stream, OutputStream os)
			throws IOException {
		byte[] entity = new byte[4096];
		int entitySize = 0;
		while ((entitySize = stream.read(entity)) != -1) {
			os.write(entity, 0, entitySize);
		}
	}

	public boolean isLoggedIn() {
		return access != null;
	}

	public void resetConnection() {
		this.access = null;
		this.adminAccess = null;
		this.keystone = null;
		this.adminKeystone = null;
		this.swiftMap.clear();
		// OpenstackApplicationState.getInstance().clear();
	}

}
