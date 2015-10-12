/**
 * @file NodeManager.java
 * @brief Class maintaining list of all docked and floating nodes. Performs some node operations
 *        like tiling.
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

package org.dockfx;

import java.lang.Math;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.dockfx.events.DockNodeEvent;
import org.dockfx.events.DockNodeEventListener;
import org.dockfx.events.DockNodeEventListenerInterface;
import org.dockfx.taskBar.TaskBar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;

/**
 * NodeManager maintains list of all active DockNodes. DockNodes should be created using this class
 * by calling getDockNode with appropriate parameters. Node manager performs nodes operations like
 * cascading and tiling nodes.
 *
 * @since DockFX 0.1
 *
 */
public class NodeManager {

  /**
   * Offset of cascaded windows.
   */
  public static final int CASCADE_OFFSET = 40;

  /**
   * Holds all nodes, floating or docked.
   */
  private ObservableList<DockNode> dockNodes = FXCollections.observableArrayList();

  /**
   * Set of listeners listening to NodeEvents of all managed nodes.
   */
  private Set<DockNodeEventListenerInterface> listeners =
      new HashSet<DockNodeEventListenerInterface>();

  /**
   * DockPane this manager manages nodes for.
   */
  private DockPane dockPane;

  /**
   * Invisible bar at bottom of the stage where minimized nodes live
   */
  private TaskBar taskBar;

  /**
   * Creates dock manager. Applies key listener for cascading/tiling shortcuts. Scene that DockPane
   * will be added to is required for listening to key events.
   *
   * @param dockPane DockPane this manager manages nodes for.
   * @param scene Scene dockPane will be added to
   *
   */
  public NodeManager(DockPane dockPane, Scene scene) {
    this.dockPane = dockPane;
    scene.setOnKeyReleased((e) -> handleKeyReleased(e));

    taskBar = new TaskBar();
    dockPane.setAlignment(Pos.BOTTOM_CENTER);
    dockPane.getChildren().add(taskBar);
  }

  /**
   * Creates dockNode, dockNode creation documented in documented in {@link org.dockfx.DockNode}
   *
   * @param contents The contents of the dock node which may be a tree or another scene graph node.
   * @param title The caption title of this dock node which maintains bidirectional state with the
   *        title bar and stage.
   * @param graphic The caption graphic of this dock node which maintains bidirectional state with
   *        the title bar and stage.
   * @return DockNode instance
   */
  public DockNode getDockNode(Node contents, String title, Node graphic) {
    DockNode dockNode = new DockNode(contents, title, graphic);
    handleNodeCreated(dockNode);
    return dockNode;
  }

  /**
   * Creates dockNode, dockNode creation documented in documented in {@link org.dockfx.DockNode}
   *
   * @param contents The contents of the dock node which may be a tree or another scene graph node.
   * @param title The caption title of this dock node which maintains bidirectional state with the
   *        title bar and stage.
   * @return DockNode instance
   */
  public DockNode getDockNode(Node contents, String title) {
    return getDockNode(contents, title, null);
  }

  /**
   * Creates dockNode, dockNode creation documented in documented in {@link org.dockfx.DockNode}
   *
   * @param contents The contents of the dock node which may be a tree or another scene graph node.
   * @return DockNode instance
   */
  public DockNode getDockNode(Node contents) {
    return getDockNode(contents, null, null);
  }

  /**
   * Creates dockNode, dockNode creation documented in documented in {@link org.dockfx.DockNode}
   *
   * @param FXMLPath path to fxml file.
   * @param title The caption title of this dock node which maintains bidirectional state with the
   *        title bar and stage.
   * @param graphic The caption graphic of this dock node which maintains bidirectional state with
   *        the title bar and stage.
   * @return DockNode instance
   */
  public DockNode getDockNode(String fxmlPath, String title, Node graphic) {
    DockNode dockNode = new DockNode(fxmlPath, title, graphic);
    handleNodeCreated(dockNode);
    return dockNode;
  }

  /**
   * Creates dockNode, dockNode creation documented in documented in {@link org.dockfx.DockNode}
   *
   * @param FXMLPath path to fxml file.
   * @param title The caption title of this dock node which maintains bidirectional state with the
   *        title bar and stage.
   * @return DockNode instance
   */
  public DockNode getDockNode(String fxmlPath, String title) {
    DockNode dockNode = new DockNode(fxmlPath, title);
    handleNodeCreated(dockNode);
    return dockNode;
  }

  /**
   * Creates dockNode, dockNode creation documented in documented in {@link org.dockfx.DockNode}
   *
   * @param FXMLPath path to fxml file.
   * @return DockNode instance
   */
  public DockNode getDockNode(String fxmlPath) {
    DockNode dockNode = new DockNode(fxmlPath);
    handleNodeCreated(dockNode);
    return dockNode;
  }


  /**
   * Adds new dock node to managed nodes list. Adds event listeners to node. Listens for close event
   * and keyboard events.
   *
   * @param newDockNode Newly created dock node.
   */
  private void handleNodeCreated(DockNode newDockNode) {
    dockNodes.add(newDockNode);
    newDockNode.setOnKeyReleased((e) -> handleKeyReleased(e));

    newDockNode.addEventListener(new DockNodeEventListener() {

      @Override
      public void dockNodeClosed(DockNodeEvent e) {
        handleNodeClosed(e.getSource());
        for (DockNodeEventListenerInterface listener : listeners) {
          listener.dockNodeClosed(e);
        }
      }

      @Override
      public void dockNodeDocked(DockNodeEvent e) {
        for (DockNodeEventListenerInterface listener : listeners) {
          listener.dockNodeDocked(e);
        }
      }

      @Override
      public void dockNodeFloated(DockNodeEvent e) {
        for (DockNodeEventListenerInterface listener : listeners) {
          listener.dockNodeFloated(e);
        }
      }

      @Override
      public void dockNodeMaximized(DockNodeEvent e) {
        for (DockNodeEventListenerInterface listener : listeners) {
          listener.dockNodeMaximized(e);
        }
        System.out.println("Maximized");
      }

      @Override
      public void dockNodeMinimized(DockNodeEvent e) {
        for (DockNodeEventListenerInterface listener : listeners) {
          listener.dockNodeMinimized(e);
        }
        taskBar.addTaskBarItemForNode(e.getSource());
        System.out.println("Minimized");
      }

      @Override
      public void dockNodeRestored(DockNodeEvent e) {
        for (DockNodeEventListenerInterface listener : listeners) {
          listener.dockNodeRestored(e);
        }
        System.out.println("Restored");
      };

      @Override
      public void dockNodeWindowed(DockNodeEvent e) {
        for (DockNodeEventListenerInterface listener : listeners) {
          listener.dockNodeWindowed(e);
        }
      }

    });
  };

  /**
   * Removes closed node from managed nodes list.
   *
   * @param closedDockNode Node that was closed.
   */
  private void handleNodeClosed(DockNode closedDockNode) {
    dockNodes.remove(closedDockNode);
  };

  /**
   * Adds new event lister for DockNode events.
   *
   * @param eventListener Listener to add.
   */
  public void addEventListener(DockNodeEventListenerInterface eventListener) {
    listeners.add(eventListener);
  }

  /**
   * Removes event listener from DockNode eventListeners.
   *
   * @param eventListener Listener to remove.
   */
  public void removeEventListener(DockNodeEventListenerInterface eventListener) {
    listeners.remove(eventListener);
  }

  /**
   * Cascades all nodes
   */
  public void cascadeNodes() {
    cascadeNodes(dockNodes);
  }

  /**
   * Cascades selected nodes. Nodes are floated and arranged in cascading fashion.
   *
   * @param nodesToCascade Nodes to be cascaded.
   */
  public void cascadeNodes(List<DockNode> nodesToCascade) {
    Window dockPaneWidndow = dockPane.getScene().getWindow();
    int i = 1;
    for (DockNode dockNode : nodesToCascade) {
      if (dockNode.isFloatable()) {
        dockNode.setFloating(true);
        dockNode.getStage().setWidth(Math.max(dockNode.getPrefWidth(), dockNode.getContents().prefWidth(0)));
        dockNode.getStage().setHeight(Math.max(dockNode.getPrefHeight(), dockNode.getContents().prefHeight(0)));
        dockNode.getStage().setX(dockPaneWidndow.getX() + i * CASCADE_OFFSET);
        dockNode.getStage().setY(dockPaneWidndow.getY() + i * CASCADE_OFFSET);
        i++;
      }
    }
  }

  /**
   * Tiles all nodes horizontally.
   */
  public void tileHorizontally() {
    dockPane.tileNodes(dockNodes, TileOrientation.HORIZOTAL);
  }

  /**
   * Tiles all nodes vertically.
   */
  public void tileVertically() {
    dockPane.tileNodes(dockNodes, TileOrientation.VERTICAL);
  }

  /**
   * Returns list of all managed nodes
   *
   * @return List of all managed nodes
   */
  public ObservableList<DockNode> getDockNodes() {
    return dockNodes;
  }

  /**
   * Key released handler. Currently handles keyboard shortcuts for cascading and tiling.
   *
   * @param e Key event
   */
  public void handleKeyReleased(KeyEvent e) {

    KeyCombination keyCombCascade = new KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN);
    KeyCombination keyCombTileHor = new KeyCodeCombination(KeyCode.H, KeyCombination.ALT_DOWN);
    KeyCombination keyCombTileVer = new KeyCodeCombination(KeyCode.V, KeyCombination.ALT_DOWN);

    if (keyCombCascade.match(e)) {
      cascadeNodes();
    } else if (keyCombTileHor.match(e)) {
      tileHorizontally();
    } else if (keyCombTileVer.match(e)) {
      tileVertically();
    }
    e.consume();
  }
}
