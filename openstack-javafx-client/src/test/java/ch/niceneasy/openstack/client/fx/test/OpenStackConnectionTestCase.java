package ch.niceneasy.openstack.client.fx.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Logger;

import org.junit.Test;

import ch.niceneasy.openstack.client.fx.services.OpenStackClientService;
import ch.niceneasy.openstack.client.fx.services.SignupService;
import ch.niceneasy.openstack.client.fx.services.model.LoginConfirmation;

import com.woorea.openstack.base.client.OpenStackSimpleTokenProvider;
import com.woorea.openstack.keystone.Keystone;
import com.woorea.openstack.keystone.model.Access;
import com.woorea.openstack.keystone.model.Role;
import com.woorea.openstack.keystone.model.Roles;
import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.keystone.model.User;
import com.woorea.openstack.keystone.model.authentication.UsernamePassword;
import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.Objects;

public class OpenStackConnectionTestCase {
	
	Map<String, User> pendingConfirmations = new HashMap<String, User>();

	private Map<String, String> configuration = new HashMap<String, String>();
	private Map<String, Role> roles = new HashMap<String, Role>();

//	public static Configuration INSTANCE;
//
//	public Configuration() {
//		INSTANCE = this;
//	}

	
	@Test
	public void testLogin() {
		SignupService.getInstance().setSignupURL("https://openstack.niceneasy.ch:7443/account-management/rest/users/");
		User user = SignupService.getInstance().getUser();
		user.setUsername("daniele");
		user.setPassword("comella1");
		LoginConfirmation loginConfirmation = SignupService.getInstance().login();
		OpenStackClientService service = OpenStackClientService
				.getInstance();
		service.setKeystoneAuthUrl(loginConfirmation
				.getKeystoneAuthUrl());
		service.setKeystoneAdminAuthUrl(loginConfirmation
				.getKeystoneAdminAuthUrl());
		service.setKeystoneEndpoint(loginConfirmation
				.getKeystoneEndpoint());
		service.setTenantName(loginConfirmation.getTenantName());
		service.setKeystonePassword(SignupService.getInstance().getUser()
				.getPassword());
		service.setKeystoneUsername(SignupService.getInstance().getUser()
				.getUsername());
		SignupService.getInstance().getUser().setId(
				loginConfirmation.getUser().getId());
		SignupService.getInstance().getUser().setTenantId(
				loginConfirmation.getUser().getTenantId());
		Tenants tenants = service.getKeystone().tenants().list().execute();
		for (Tenant tenant : tenants) {
			System.out.println(tenant.getDescription());
			Swift swift = service.getSwift(tenant.getId());
			List<Container> tempList = swift.containers().list().execute()
					.getList();
			for (Container container : tempList) {
				System.out.println(container.getName());
				Objects objects =	swift.containers()
						.container(container.getName()).list().execute();
				for (com.woorea.openstack.swift.model.Object object : objects) {
					System.out.println(object.getName());
				}
			}
		}
		
	}
	
	private Logger logger;
	
	public void fetchConfiguration() {
		Properties props = new Properties();
		try {
			props.load(getClass().getClassLoader().getResourceAsStream(
					"openstack.properties"));
			for (Entry<Object, Object> entry : props.entrySet()) {
				configuration.put(entry.getKey().toString(), entry.getValue()
						.toString());
			}
			Roles rolesList = getKeystone().roles().list().execute();
			for (Role role : rolesList) {
				roles.put(role.getName(), role);
			}
		} catch (Exception e) {
			logger.severe(e.getMessage());
		}
	}
	
	public Keystone getKeystone() {
		Keystone keystone = new Keystone(configuration.get("keystoneAuthUrl"));
		Access access = keystone
				.tokens()
				.authenticate(
						new UsernamePassword(configuration
								.get("keystoneUsername"), configuration
								.get("keystonePassword")))
				.withTenantName("admin").execute();
		keystone = new Keystone(configuration.get("keystoneAdminAuthUrl"));
		keystone.setTokenProvider(new OpenStackSimpleTokenProvider(access
				.getToken().getId()));
		return keystone;
	}
	

}
