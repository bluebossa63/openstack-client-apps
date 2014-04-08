/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.client.fx;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.niceneasy.openstack.client.fx.controller.OpenStackClientController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * The Class Main.
 * 
 * @author Daniele
 */
public class Main extends Application {

    /**
     * @param args the command line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
//    	File file = new File( "C:\\Users\\daniele.NE\\Desktop\\links.docx" );
//    	Desktop.getDesktop().open( file );
        Application.launch(Main.class, (java.lang.String[])null);
    }

    @Override
    public void start(Stage primaryStage) {
    	
        try {
        	FXMLLoader loader = new FXMLLoader(
        			  getClass().getResource(
        					  "OpenStackClient.fxml"
        			  )
        			);

            AnchorPane page = (AnchorPane)loader.load();
            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.initStyle(StageStyle.TRANSPARENT);
            //primaryStage.setTitle("Hello World Sample");
            primaryStage.show();

            OpenStackClientController controller = 
        			  loader.<OpenStackClientController>getController();
        		controller.registerStage(primaryStage, page);

        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
