/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.client.fx.controller;

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javax.activation.MimeType;
import javax.activation.MimetypesFileTypeMap;
import javax.imageio.ImageIO;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import org.apache.commons.io.IOUtils;

import ch.niceneasy.openstack.client.fx.services.OpenStackClientService;
import ch.niceneasy.openstack.client.fx.services.SignupService;
import ch.niceneasy.openstack.client.fx.services.model.LoginConfirmation;
import ch.niceneasy.openstack.client.fx.ui.OpenStackTreeItem;
import ch.niceneasy.openstack.client.fx.util.EffectUtilities;
import ch.niceneasy.openstack.client.fx.util.PseudoFileSystem;

import com.woorea.openstack.keystone.model.Tenant;
import com.woorea.openstack.keystone.model.Tenants;
import com.woorea.openstack.keystone.model.User;
import com.woorea.openstack.swift.Swift;
import com.woorea.openstack.swift.model.Container;
import com.woorea.openstack.swift.model.ObjectDownload;
import com.woorea.openstack.swift.model.Objects;

/**
 * @author Daniele
 */

public class OpenStackClientController {

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
	// fx:id="treeView"
	private TreeView<String> treeView;

	private SignupService signupService = SignupService.getInstance();

	OpenStackClientService openStackClientService = OpenStackClientService
			.getInstance();

	private LoginConfirmation loginConfirmation;

	private File openStack;

	@FXML
	// This method is called by the FXMLLoader when initialization is complete
	void initialize() {

		File userHome = new File(System.getProperty("user.home"));
		openStack = new File(userHome, "OpenStack");
		if (!openStack.exists()) {
			openStack.mkdir();
		}

		assert button != null : "fx:id=\"button\" was not injected: check your FXML file 'OpenStackClient.fxml'.";

		if (button != null) {
			button.setOnAction(new LoginEvent(this));
		}

		assert treeView != null : "fx:id=\"treeView\" was not injected: check your FXML file 'OpenStackClient.fxml'.";
		treeView.setOnMouseClicked(new DownloadEvent());

		//performLogin();
		

	}

	/**
	 * 
	 */
	void loadTree() {

		TreeItem<String> rootItem = new OpenStackTreeItem(
				signupService.getUser());
		TreeItem<String> current = null;
		Tenants tenants = openStackClientService.getKeystone().tenants().list()
				.execute();
		for (Tenant tenant : tenants) {
			current = rootItem;
			System.out.println(tenant.getDescription());
			OpenStackTreeItem child1 = new OpenStackTreeItem(tenant);
			current.getChildren().add(child1);
			System.out.println(child1.getPath());
			File tenantDir = new File(openStack, child1.getPath());
			if (!tenantDir.exists()) {
				tenantDir.mkdir();
			}
			Swift swift = openStackClientService.getSwift(tenant.getId());
			List<Container> tempList = swift.containers().list().execute()
					.getList();
			for (Container container : tempList) {
				current = child1;
				System.out.println(container.getName());
				OpenStackTreeItem child2 = new OpenStackTreeItem(container);
				current.getChildren().add(child2);
				System.out.println(child2.getPath());
				File containerDir = new File(openStack, child2.getPath());
				if (!containerDir.exists()) {
					containerDir.mkdir();
				}
				Objects objects = swift.containers()
						.container(container.getName()).list().execute();
				PseudoFileSystem pfs = PseudoFileSystem
						.readFromObjects(objects);
				readDirectory(child2, pfs);
			}
		}
		treeView.setRoot(rootItem);
	}

	/**
	 * 
	 */
	void performLogin() {
		signupService
				.setSignupURL("https://openstack.niceneasy.ch:7443/account-management/rest/users/");
		loginConfirmation = signupService.login();

		openStackClientService.setKeystoneAuthUrl(loginConfirmation
				.getKeystoneAuthUrl());
		openStackClientService.setKeystoneAdminAuthUrl(loginConfirmation
				.getKeystoneAdminAuthUrl());
		openStackClientService.setKeystoneEndpoint(loginConfirmation
				.getKeystoneEndpoint());
		openStackClientService.setTenantName(loginConfirmation.getTenantName());
		openStackClientService.setKeystonePassword(signupService.getUser()
				.getPassword());
		openStackClientService.setKeystoneUsername(signupService.getUser()
				.getUsername());
		signupService.getUser().setId(loginConfirmation.getUser().getId());
		signupService.getUser().setTenantId(
				loginConfirmation.getUser().getTenantId());
	}

	/**
	 * @param current
	 * @param parent
	 * @param pfs
	 */
	private void readDirectory(OpenStackTreeItem parent, PseudoFileSystem pfs) {
		for (com.woorea.openstack.swift.model.Object object : pfs.getFiles()
				.values()) {
			OpenStackTreeItem child3 = new OpenStackTreeItem(object);
			parent.getChildren().add(child3);
			System.out.println(child3.getPath());
			File thisDir = new File(openStack, child3.getPath());
			if (!thisDir.exists()) {
				try {
					downloadFile(child3);
					// MimetypesFileTypeMap.getDefaultFileTypeMap().
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if (thisDir.getPath().endsWith("jpg")) {
				Image image;
				try {
					image = new Image(new FileInputStream(thisDir),100,100,false,false);
					child3.setGraphic(new ImageView(image));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				try {
					Image image = createImage(javax.swing.filechooser.FileSystemView
							.getFileSystemView().getSystemIcon(thisDir));
					child3.setGraphic(new ImageView(image));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// ;
		}
		;
		for (PseudoFileSystem pfsChild : pfs.getDirectories().values()) {
			System.out.println(pfsChild.getMetaData().getName());
			OpenStackTreeItem child3 = new OpenStackTreeItem(
					pfsChild.getMetaData());
			parent.getChildren().add(child3);
			System.out.println(child3.getPath());
			File thisDir = new File(openStack, child3.getPath());
			if (!thisDir.exists()) {
				thisDir.mkdir();
			}
			readDirectory(child3, pfsChild);
		}
	}

	public void registerStage(Stage stage, Node dragNode) {
		EffectUtilities.makeDraggable(stage, dragNode);
	}

	public class LoginEvent implements EventHandler<ActionEvent> {
		
		OpenStackClientController openStackClientController;
		
		public LoginEvent(OpenStackClientController openStackClientController) {
			this.openStackClientController = openStackClientController;
		}
		@Override
		public void handle(ActionEvent event) {
			try {
				FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(
						"ch/niceneasy/openstack/client/fx/LoginDialog.fxml"));
				Stage stage = new Stage();
				AnchorPane page = (AnchorPane) loader.load();
				Scene scene = new Scene(page);
				stage.setScene(scene);
				stage.initOwner(button.getScene().getWindow());
				stage.initModality(Modality.APPLICATION_MODAL);
				stage.show();
				
				LoginController controller = 
	        			  loader.<LoginController>getController();
	            controller.setParentController(openStackClientController);
				controller.setStage(stage);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public File downloadFile(OpenStackTreeItem item) throws IOException {
		OpenStackTreeItem search = item;
		Container container = search.getContainer();
		while (container == null) {
			search = (OpenStackTreeItem) search.getParent();
			container = search.getContainer();
		}
		Swift swift = openStackClientService
				.getSwift(((OpenStackTreeItem) search.getParent()).getTenant()
						.getId());
		ObjectDownload download = swift.containers()
				.container(container.getName())
				.download(item.getObject().getName()).execute();
		File tempFile = new File(openStack, item.getPath());
		FileOutputStream fos = new FileOutputStream(tempFile);
		IOUtils.copy(download.getInputStream(), fos);
		fos.close();
		return tempFile;

	}

	public class DownloadEvent implements EventHandler<MouseEvent> {
		@Override
		public void handle(MouseEvent mouseEvent) {
			if (mouseEvent.getClickCount() == 2) {
				OpenStackTreeItem item = (OpenStackTreeItem) treeView
						.getSelectionModel().getSelectedItem();
				try {
					Desktop.getDesktop().open(downloadFile(item));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		/**
		 * @param item
		 * @throws IOException
		 */

	}

	public static javafx.scene.image.Image createImage(javax.swing.Icon icon)
			throws IOException {
		BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(),
				icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics g = bufferedImage.createGraphics();
		icon.paintIcon(null, g, 0, 0);
		g.dispose();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write((RenderedImage) bufferedImage, "png", out);
		out.flush();
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		return new javafx.scene.image.Image(in);
	}

	public SignupService getSignupService() {
		return signupService;
	}

	public OpenStackClientService getOpenStackClientService() {
		return openStackClientService;
	}
}
