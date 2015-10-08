package org.dockfx.taskBar;

import org.dockfx.DockNode;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class TaskBarItem extends HBox {

  public static final int TASK_BAR_ITEM_PREF_WIDTH = 150;

  Label label;

  Button button;

  Pane fillPane;

  ImageView imageView;

  public TaskBarItem(DockNode dockNode) {
    super();
    setPrefWidth(TASK_BAR_ITEM_PREF_WIDTH);
    setPadding(new Insets(3));

    ImageView nodeImageView = (ImageView)dockNode.getGraphic();
    imageView = new ImageView(nodeImageView.getImage());

    label = new Label(dockNode.getTitle());

    button = new Button();
    button.getStyleClass().add("dock-state-button");

    fillPane = new Pane();
    HBox.setHgrow(fillPane, Priority.ALWAYS);

    setBackground(new Background(new BackgroundFill(Color.TOMATO, null, null)));

    getChildren().addAll(imageView, label, fillPane, button);

  }

}
