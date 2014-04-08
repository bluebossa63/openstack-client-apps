/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.client.fx.ui;

import java.nio.file.FileSystems;

import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import ch.niceneasy.openstack.client.fx.util.EffectUtilities;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.User;
import com.woorea.openstack.swift.model.Container;

/**
 * The Class OpenStackTreeItem.
 * 
 * @author Daniele
 */
public class OpenStackTreeItem extends TreeItem<String> {

	/** The separator. */
	private static String separator = FileSystems.getDefault().getSeparator();

	/** The object. */
	com.woorea.openstack.swift.model.Object object;

	/** The container. */
	Container container;

	/** The tenant. */
	Tenant tenant;

	/** The user. */
	User user;

	/**
	 * Instantiates a new open stack tree item.
	 * 
	 * @param container
	 *            the container
	 * @param node
	 *            the node
	 */
	public OpenStackTreeItem(Container container, Node node) {
		super(container.getName(), node);
		setContainer(container);
	}

	/**
	 * Instantiates a new open stack tree item.
	 * 
	 * @param container
	 *            the container
	 */
	public OpenStackTreeItem(Container container) {
		super(container.getName());
		setContainer(container);
	}

	/**
	 * Instantiates a new open stack tree item.
	 * 
	 * @param object
	 *            the object
	 * @param node
	 *            the node
	 */
	public OpenStackTreeItem(com.woorea.openstack.swift.model.Object object,
			Node node) {
		super(EffectUtilities.cleanName(object.getName()), node);
		setObject(object);
	}

	/**
	 * Instantiates a new open stack tree item.
	 * 
	 * @param object
	 *            the object
	 */
	public OpenStackTreeItem(com.woorea.openstack.swift.model.Object object) {
		super(EffectUtilities.cleanName(object.getName()));
		setObject(object);
	}

	/**
	 * Instantiates a new open stack tree item.
	 * 
	 * @param tenant
	 *            the tenant
	 * @param node
	 *            the node
	 */
	public OpenStackTreeItem(Tenant tenant, Node node) {
		super(tenant.getName(), node);
		setTenant(tenant);
	}

	/**
	 * Instantiates a new open stack tree item.
	 * 
	 * @param tenant
	 *            the tenant
	 */
	public OpenStackTreeItem(Tenant tenant) {
		super(tenant.getName());
		setTenant(tenant);
	}

	/**
	 * Instantiates a new open stack tree item.
	 * 
	 * @param user
	 *            the user
	 * @param node
	 *            the node
	 */
	public OpenStackTreeItem(User user, Node node) {
		super(user.getUsername(), node);
		setUser(user);
	}

	/**
	 * Instantiates a new open stack tree item.
	 * 
	 * @param user
	 *            the user
	 */
	public OpenStackTreeItem(User user) {
		super(user.getUsername());
		setUser(user);
	}

	/**
	 * Gets the object.
	 * 
	 * @return the object
	 */
	public com.woorea.openstack.swift.model.Object getObject() {
		return object;
	}

	/**
	 * Sets the object.
	 * 
	 * @param object
	 *            the new object
	 */
	public void setObject(com.woorea.openstack.swift.model.Object object) {
		this.object = object;
	}

	/**
	 * Gets the container.
	 * 
	 * @return the container
	 */
	public Container getContainer() {
		return container;
	}

	/**
	 * Sets the container.
	 * 
	 * @param container
	 *            the new container
	 */
	public void setContainer(Container container) {
		this.container = container;
	}

	/**
	 * Gets the tenant.
	 * 
	 * @return the tenant
	 */
	public Tenant getTenant() {
		return tenant;
	}

	/**
	 * Sets the tenant.
	 * 
	 * @param tenant
	 *            the new tenant
	 */
	public void setTenant(Tenant tenant) {
		this.tenant = tenant;
	}

	/**
	 * Gets the user.
	 * 
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user.
	 * 
	 * @param user
	 *            the new user
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * Gets the path.
	 * 
	 * @return the path
	 */
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
