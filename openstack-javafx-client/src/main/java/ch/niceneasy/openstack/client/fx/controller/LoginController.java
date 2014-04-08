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
import javafx.scene.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.geometry.*;

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
	
	private OpenStackClientController openStackClientController;
	
	private Stage stage;

	@FXML
	// This method is called by the FXMLLoader when initialization is complete
	void initialize() {
		assert button != null : "fx:id=\"button\" was not injected: check your FXML file 'HelloWorld.fxml'.";

		if (button != null) {
			button.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					System.out.println("Hello World");
					User user = openStackClientController.getSignupService().getUser();
					user.setUsername(txtUsername.getText());
					user.setPassword(txtPassword.getText());
					try {
						openStackClientController.performLogin();
						openStackClientController.loadTree();
						stage.close();
					} catch (Exception e) {
						final Stage dialogStage = new Stage();
						dialogStage.initModality(Modality.WINDOW_MODAL);
						Button button = new Button("Ok");
						button.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent event) {
										dialogStage.close();
									}});
						dialogStage.setScene(new Scene(VBoxBuilder.create().
						    children(new Text(e.getLocalizedMessage()), button).
						    alignment(Pos.CENTER).padding(new Insets(5)).build()));
						dialogStage.show();
					}
				}
			});
		}
	}

	/**
	 * @param openStackClientController
	 */
	public void setParentController(
			OpenStackClientController openStackClientController) {
		this.openStackClientController =  openStackClientController;
		
	}

	/**
	 * @param stage
	 */
	public void setStage(Stage stage) {
		this.stage = stage;
		
	}

}
