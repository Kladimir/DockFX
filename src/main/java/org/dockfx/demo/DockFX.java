/**
 * @file DockFX.java
 * @brief Driver demonstrating basic dock layout with prototypes. Maintained in a separate package
 *        to ensure the encapsulation of org.dockfx private package members.
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

package org.dockfx.demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

import org.dockfx.DockNode;
import org.dockfx.DockPane;
import org.dockfx.DockPos;
import org.dockfx.NodeManager;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;

public class DockFX extends Application {

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) {
    launchDemo(primaryStage);
  }

  @SuppressWarnings("unchecked")
  private void launchDemo(Stage primaryStage) {
    primaryStage.setTitle("DockFX");

    // create a dock pane that will manage our dock nodes and handle the layout
    DockPane dockPane = new DockPane();
    Scene scene = new Scene(dockPane, 800, 500);
    NodeManager nodeManager = new NodeManager(dockPane);

    // create a default test node for the center of the dock area
    TabPane tabs = new TabPane();
    HTMLEditor htmlEditor = new HTMLEditor();
    try {
      htmlEditor.setHtmlText(new String(Files.readAllBytes(Paths.get("readme.html"))));
    } catch (IOException e) {
      e.printStackTrace();
    }

    // empty tabs ensure that dock node has its own background color when floating
    tabs.getTabs().addAll(new Tab("Tab 1", htmlEditor), new Tab("Tab 2"), new Tab("Tab 3"));

    TableView<String> tableView = new TableView<String>();
    // this is why @SupressWarnings is used above
    // we don't care about the warnings because this is just a demonstration
    // for docks not the table view
    tableView.getColumns().addAll(new TableColumn<String, String>("A"),
        new TableColumn<String, String>("B"), new TableColumn<String, String>("C"));

    // load an image to caption the dock nodes
    Image dockImage = new Image(DockFX.class.getResource("docknode.png").toExternalForm());

    // create and dock some prototype dock nodes to the middle of the dock pane
    // the preferred sizes are used to specify the relative size of the node
    // to the other nodes

    // we can use this to give our central content a larger area where
    // the top and bottom nodes have a preferred width of 300 which means that
    // when a node is docked relative to them such as the left or right dock below
    // they will have 300 / 100 + 300 (400) or 75% of their previous width
    // after both the left and right node's are docked the center docks end up with 50% of the width

    Label label1 = new Label("Try cascading or tiling subwindows with keyboard shortcuts :");
    Label label2 = new Label(
        " alt + c - cascade all \n alt + v - tile all vertically \n alt + h - tile all horizontally");
    VBox vBox = new VBox(label1, label2);
    vBox.setPadding(new Insets(10));

    DockNode tabsDock = nodeManager.getDockNode(tabs, "Tabs Dock", new ImageView(dockImage));
    tabsDock.setPrefSize(300, 500);
    tabsDock.dock(dockPane, DockPos.TOP);

    DockNode tableDock = nodeManager.getDockNode(vBox, "Tabs Dock", new ImageView(dockImage));
    // let's disable our table from being undocked
    // tableDock.setDockTitleBar(null);
    tableDock.setPrefSize(300, 500);
//    tableDock.dock(dockPane, DockPos.BOTTOM);
    //tableDock.setFloating(true);
    //tableDock.floatNode(null, dockPane);

    // loads interface from fxml file
    DockNode loginDock = nodeManager.getDockNode("demo/LoginForm.fxml", "Personal info", new ImageView(dockImage));
    loginDock.setPrefSize(200, 500);
    loginDock.dock(dockPane, DockPos.LEFT);

    primaryStage.setScene(scene);
    primaryStage.sizeToScene();
    primaryStage.show();

    // can be created and docked before or after the scene is created
    // and the stage is shown
    DockNode treeDock =
        nodeManager.getDockNode(generateRandomTree(), "Tree Dock", new ImageView(dockImage));
    treeDock.setPrefSize(115, 500);
//    treeDock.dock(dockPane, DockPos.LEFT);
    treeDock = nodeManager.getDockNode(generateRandomTree(), "Tree Dock", new ImageView(dockImage));
    treeDock.setPrefSize(115, 500);
    //treeDock.dock(dockPane, DockPos.RIGHT);

    treeDock.setMaximizable(false);
    treeDock.setMinimizable(false);
    treeDock.setClosable(false);

    // test the look and feel with both Caspian and Modena
    Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
    // initialize the default styles for the dock pane and undocked nodes using the DockFX
    // library's internal Default.css stylesheet
    // unlike other custom control libraries this allows the user to override them globally
    // using the style manager just as they can with internal JavaFX controls
    // this must be called after the primary stage is shown
    // https://bugs.openjdk.java.net/browse/JDK-8132900
    DockPane.initializeDefaultUserAgentStylesheet();

    // TODO: after this feel free to apply your own global stylesheet using the StyleManager class

    //loginDock.floatNode(null, dockPane, false);

  }

  private TreeView<String> generateRandomTree() {
    // create a demonstration tree view to use as the contents for a dock node
    TreeItem<String> root = new TreeItem<String>("Root");
    TreeView<String> treeView = new TreeView<String>(root);
    treeView.setShowRoot(false);

    // populate the prototype tree with some random nodes
    Random rand = new Random();
    for (int i = 4 + rand.nextInt(8); i > 0; i--) {
      TreeItem<String> treeItem = new TreeItem<String>("Item " + i);
      root.getChildren().add(treeItem);
      for (int j = 2 + rand.nextInt(4); j > 0; j--) {
        TreeItem<String> childItem = new TreeItem<String>("Child " + j);
        treeItem.getChildren().add(childItem);
      }
    }

    return treeView;
  }
}
