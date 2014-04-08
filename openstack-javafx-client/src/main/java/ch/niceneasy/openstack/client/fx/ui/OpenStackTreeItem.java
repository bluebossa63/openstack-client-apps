/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.client.fx.ui;

import java.nio.file.FileSystems;

import ch.niceneasy.openstack.client.fx.util.EffectUtilities;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.keystone.model.User;

import javafx.scene.control.TreeItem;
import javafx.scene.Node;
/**
 * @author Daniele
 * Class OpenStackTreeItem
 */
public class OpenStackTreeItem extends TreeItem<String> {
	
	private static String separator = FileSystems.getDefault().getSeparator();
	
	com.woorea.openstack.swift.model.Object object;
	
	Container container;
	
	Tenant tenant;
	
	User user;
	
	public OpenStackTreeItem(Container container, Node node) {
		super(container.getName(),node);
		setContainer(container);
	}

	public OpenStackTreeItem(Container container) {
		super(container.getName());
		setContainer(container);
	}	
	
	public OpenStackTreeItem(com.woorea.openstack.swift.model.Object object, Node node) {
		super(EffectUtilities.cleanName(object.getName()),node);
		setObject(object);
	}

	public OpenStackTreeItem(com.woorea.openstack.swift.model.Object object) {
		super(EffectUtilities.cleanName(object.getName()));
		setObject(object);
	}	

	public OpenStackTreeItem(Tenant tenant, Node node) {
		super(tenant.getName(),node);
		setTenant(tenant);
	}

	public OpenStackTreeItem(Tenant tenant) {
		super(tenant.getName());
		setTenant(tenant);
	}
	
	public OpenStackTreeItem(User user, Node node) {
		super(user.getUsername(),node);
		setUser(user);
	}

	public OpenStackTreeItem(User user) {
		super(user.getUsername());
		setUser(user);
	}
	
	public com.woorea.openstack.swift.model.Object getObject() {
		return object;
	}

	public void setObject(com.woorea.openstack.swift.model.Object object) {
		this.object = object;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public Tenant getTenant() {
		return tenant;
	}

	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getPath() {
		String path = "";
		OpenStackTreeItem search = this;
		Container container = search.getContainer();
		Tenant tenant = search.getTenant();
		while (container == null && tenant == null) {
			path = separator + search.getValue() + path;
			search = (OpenStackTreeItem) search.getParent();
			container = search.getContainer();
			tenant = search.getTenant();
		}
		if (tenant == null) {
			path = separator + search.getValue() + path;
			search = (OpenStackTreeItem) search.getParent();
		}
		path = separator + search.getValue() + path;
		return path;
	}

}
