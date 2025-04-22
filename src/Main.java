import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.control.TextInputDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import java.util.Optional;

import filters.BlackAndWhiteFilter;
import filters.BlueFilter;
import filters.VignetteFilter;
import transforms.FlipTransform;
import transforms.RotateTransform;

import database.ImageDAO;
import exceptions.ImageNotLoadedException;

public class Main extends Application {

    private ImageCanvas imageCanvas;
    private int currentImageId = -1;
    private String currentImageName;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Photo Editor");
        imageCanvas = new ImageCanvas();
        MenuBar menuBar = createMenuBar(primaryStage);
        BorderPane root = new BorderPane();
        root.setTop(menuBar);
        root.setCenter(imageCanvas.getCanvasPane());
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private MenuBar createMenuBar(Stage stage) {
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");

        MenuItem openItem = new MenuItem("Open Image");
        openItem.setOnAction(e -> openImage(stage));

        MenuItem saveItem = new MenuItem("Save to DB");
        saveItem.setOnAction(e -> saveImageToDatabase());

        MenuItem loadItem = new MenuItem("Load from DB");
        loadItem.setOnAction(e -> loadImageFromDatabase());

        MenuItem renameItem = new MenuItem("Rename Image");
        renameItem.setOnAction(e -> renameImageInDatabase());

        MenuItem replaceItem = new MenuItem("Replace Data in DB");
        replaceItem.setOnAction(e -> replaceImageInDatabase());

        MenuItem deleteItem = new MenuItem("Delete Image");
        deleteItem.setOnAction(e -> deleteImageFromDatabase());

        fileMenu.getItems().addAll(
            openItem, saveItem, loadItem, renameItem, replaceItem, deleteItem
        );

        Menu filtersMenu = new Menu("Filters");
        filtersMenu.getItems().addAll(
            createMenuItem("Black & White", () -> applyFilter(new BlackAndWhiteFilter())),
            createMenuItem("Blue Filter",      () -> applyFilter(new BlueFilter())),
            createMenuItem("Vignette",        () -> applyFilter(new VignetteFilter()))
        );

        Menu transformsMenu = new Menu("Transforms");
        transformsMenu.getItems().addAll(
            createMenuItem("Flip",   () -> applyTransform(new FlipTransform(true))),
            createMenuItem("Rotate", () -> applyTransform(new RotateTransform(90)))
        );

        menuBar.getMenus().addAll(fileMenu, filtersMenu, transformsMenu);
        return menuBar;
    }

    private MenuItem createMenuItem(String text, Runnable action) {
        MenuItem item = new MenuItem(text);
        item.setOnAction(e -> action.run());
        return item;
    }

    private void openImage(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open Image");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            try (FileInputStream fis = new FileInputStream(file)) {
                Image img = new Image(fis);
                imageCanvas.setImage(img);
                currentImageId = -1;
                currentImageName = file.getName();
            } catch (IOException ex) {
                System.err.println("Error loading file: " + ex.getMessage());
            }
        }
    }

    private void saveImageToDatabase() {
        try {
            Image img = imageCanvas.getCurrentImage();
            if (img == null) {
                throw new ImageNotLoadedException("No image to save.");
            }
            int id = ImageDAO.saveImageToDB(img, currentImageName);
            currentImageId   = id;
            System.out.println("Saved image ID=" + id);
        } catch (Exception ex) {
            System.err.println("Error saving: " + ex.getMessage());
        }
    }

    private void loadImageFromDatabase() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Load Image");
        dlg.setHeaderText("Enter ID to load:");
        Optional<String> res = dlg.showAndWait();
        res.ifPresent(s -> {
            try {
                int id = Integer.parseInt(s.trim());
                Image img = ImageDAO.getImageFromDB(id);
                if (img != null) {
                    imageCanvas.setImage(img);
                    currentImageId   = id;
                    currentImageName = ""; // name retrieval not implemented
                    System.out.println("Loaded image ID=" + id);
                } else {
                    System.out.println("No record for ID=" + id);
                }
            } catch (Exception ex) {
                System.err.println("Error loading: " + ex.getMessage());
            }
        });
    }

    private void renameImageInDatabase() {
        if (currentImageId < 0) return;
        TextInputDialog dlg = new TextInputDialog(currentImageName);
        dlg.setTitle("Rename Image");
        dlg.setHeaderText("New name:");
        dlg.showAndWait().ifPresent(name -> {
            try {
                boolean ok = ImageDAO.updateImageName(currentImageId, name);
                if (ok) currentImageName = name;
            } catch (SQLException ex) {
                System.err.println("Error renaming: " + ex.getMessage());
            }
        });
    }

    private void replaceImageInDatabase() {
        if (currentImageId < 0) return;
        try {
            Image img = imageCanvas.getCurrentImage();
            boolean ok = ImageDAO.updateImageData(currentImageId, img);
            System.out.println(ok
                ? "Replaced data for ID=" + currentImageId
                : "No update for ID=" + currentImageId
            );
        } catch (Exception ex) {
            System.err.println("Error replacing: " + ex.getMessage());
        }
    }

    private void deleteImageFromDatabase() {
        if (currentImageId < 0) return;
        try {
            boolean ok = ImageDAO.deleteImage(currentImageId);
            if (ok) {
                imageCanvas.setImage(null);
                System.out.println("Deleted ID=" + currentImageId);
                currentImageId = -1;
            }
        } catch (SQLException ex) {
            System.err.println("Error deleting: " + ex.getMessage());
        }
    }

    private void applyFilter(filters.Filter filter) {
        imageCanvas.applyFilter(filter);
    }

    private void applyTransform(transforms.Transform transform) {
        imageCanvas.applyTransform(transform);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
