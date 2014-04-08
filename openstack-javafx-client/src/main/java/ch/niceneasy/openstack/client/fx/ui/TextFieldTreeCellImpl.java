/*
 * Copyright (c) 2014, daniele.ulrich@gmail.com, http://www.niceneasy.ch. All rights reserved.
 */
package ch.niceneasy.openstack.client.fx.ui;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * The Class TextFieldTreeCellImpl.
 * 
 * @author Daniele
 */
public class TextFieldTreeCellImpl extends TreeCell<String> {

	/** The text field. */
	private TextField textField;

	/**
	 * Instantiates a new text field tree cell impl.
	 */
	public TextFieldTreeCellImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.scene.control.TreeCell#startEdit()
	 */
	@Override
	public void startEdit() {
		super.startEdit();

		if (textField == null) {
			createTextField();
		}
		setText(null);
		setGraphic(textField);
		textField.selectAll();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.scene.control.TreeCell#cancelEdit()
	 */
	@Override
	public void cancelEdit() {
		super.cancelEdit();
		setText(getItem());
		setGraphic(getTreeItem().getGraphic());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.scene.control.Cell#updateItem(java.lang.Object, boolean)
	 */
	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			if (isEditing()) {
				if (textField != null) {
					textField.setText(getString());
				}
				setText(null);
				setGraphic(textField);
			} else {
				setText(getString());
				setGraphic(getTreeItem().getGraphic());
			}
		}
	}

	/**
	 * Creates the text field.
	 */
	private void createTextField() {
		textField = new TextField(getString());
		textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent t) {
				if (t.getCode() == KeyCode.ENTER) {
					commitEdit(textField.getText());
				} else if (t.getCode() == KeyCode.ESCAPE) {
					cancelEdit();
				}
			}
		});
	}

	/**
	 * Gets the string.
	 * 
	 * @return the string
	 */
	private String getString() {
		return getItem() == null ? "" : getItem().toString();
	}
}
