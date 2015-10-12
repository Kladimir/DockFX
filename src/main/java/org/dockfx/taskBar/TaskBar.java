/**
 * @file TaskBar.java
 * @brief Bottom horizontal bar where minimized nodes are displayed
 *
 * @section License
 *
 *          This file is a part of the DockFX Library. Copyright (C) 2015 Robert B. Colton
 *
 *          This program is free software: you can redistribute it and/or modify it under the terms
 *          of the GNU Lesser General Public License as published by the Free Software Foundation,
 *          either version 3 of the License, or (at your option) any later version.
 *
 *          This program is distributed in the hope that it will be useful, but WITHOUT ANY
 *          WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *          PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 *          You should have received a copy of the GNU Lesser General Public License along with this
 *          program. If not, see <http://www.gnu.org/licenses/>.
 **/

package org.dockfx.taskBar;

import org.dockfx.DockNode;

import javafx.scene.layout.HBox;

/**
 * Horizontal bar where TaskBarItems are displayed.
 *
 */
public class TaskBar extends HBox {

  /**
   * Height of the task bar.
   */
  public static final int TASK_BAR_HEIGHT = 25;

  /**
   * Creates TaskBar as HBox and sets height.
   */
  public TaskBar() {
    super();
    this.setMaxHeight(TASK_BAR_HEIGHT);
  }

  /**
   * Adds TaskBatItem to TaskBar. Called when node is minimized.
   *
   * @param dockNode Node that was minimized.
   */
  public void addTaskBarItemForNode(DockNode dockNode) {
    TaskBarItem taskBarItem = new TaskBarItem(dockNode);
    getChildren().add(taskBarItem);
  }
}
