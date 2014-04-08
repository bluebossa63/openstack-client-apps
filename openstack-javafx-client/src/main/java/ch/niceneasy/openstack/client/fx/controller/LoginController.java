/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.client.fx.controller;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.keystone.model.User;
import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.Objects;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.control.TreeView;

/**
 * The Class LoginController.
 * 
 * @author Daniele
 */
public class LoginController {

	@FXML
	// ResourceBundle that was given to the FXMLLoader
	private ResourceBundle resources;
	@FXML
	// URL location of the FXML file that was given to the FXMLLoader
	private URL location;
	@FXML
	// fx:id="button"
	private Button button; // Value injected by FXMLLoader
	@FXML
	// fx:id="txtUsername"
	private TextField txtUsername;
	@FXML
	// fx:id="txtPassword"
	private TextField txtPassword;

	@FXML
	// This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert button != null : "fx:id=\"button\" was not injected: check your FXML file 'HelloWorld.fxml'.";

		if (button != null) {
			button.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					System.out.println("Hello World");
				}
			});
		}
	}

}
