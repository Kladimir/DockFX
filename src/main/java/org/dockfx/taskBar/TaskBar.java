package org.dockfx.taskBar;

import org.dockfx.DockNode;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

public class TaskBar extends HBox {

  public static final int TASK_BAR_HEIGHT = 25;

  public TaskBar() {
    super();
    setBackground(new Background(new BackgroundFill(Color.CORNFLOWERBLUE, null, null)));
    this.setMaxHeight(TASK_BAR_HEIGHT);
  }

  public void addTaskBarItemForNode(DockNode dockNode) {
    TaskBarItem taskBarItem = new TaskBarItem(dockNode);
    getChildren().add(taskBarItem);
  }
}
