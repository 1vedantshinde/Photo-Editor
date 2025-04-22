import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import filters.BlackAndWhiteFilter;
import filters.BlueFilter;
import filters.VignetteFilter;
import transforms.FlipTransform;
import transforms.RotateTransform;
import transforms.Transform;
import database.ImageDAO; // Add this to the import section

import exceptions.ImageSaveException;

public class Main extends Application {

    private ImageCanvas imageCanvas;

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
        fileMenu.getItems().add(openItem);

        MenuItem saveToDbItem = new MenuItem("Save Image to DB");
        saveToDbItem.setOnAction(e -> saveImageToDatabase());
        fileMenu.getItems().add(saveToDbItem);


        // Filters Menu
        Menu filtersMenu = createFiltersMenu();
        menuBar.getMenus().addAll(fileMenu, filtersMenu);

        // Transforms Menu
        Menu transformsMenu = createTransformsMenu();
        menuBar.getMenus().add(transformsMenu);



        

        return menuBar;
    }

    private Menu createFiltersMenu() {
        Menu filtersMenu = new Menu("Filters");

        MenuItem bwFilterItem = new MenuItem("Black and White");
        bwFilterItem.setOnAction(e -> applyBlackAndWhiteFilter());

        MenuItem blueFilterItem = new MenuItem("Blue Filter");
        blueFilterItem.setOnAction(e -> applyBlueFilter());

        MenuItem vignetteFilterItem = new MenuItem("Vignette");
        vignetteFilterItem.setOnAction(e -> applyVignetteFilter());

        filtersMenu.getItems().addAll(bwFilterItem, blueFilterItem, vignetteFilterItem);
        return filtersMenu;
    }

    private Menu createTransformsMenu() {
        Menu transformsMenu = new Menu("Transforms");

        MenuItem flipItem = new MenuItem("Flip");
        flipItem.setOnAction(e -> applyFlipTransform());

        MenuItem rotateItem = new MenuItem("Rotate");
        rotateItem.setOnAction(e -> applyRotateTransform());

        transformsMenu.getItems().addAll(flipItem, rotateItem);
        return transformsMenu;
    }

    private void applyBlackAndWhiteFilter() {
        imageCanvas.applyFilter(new BlackAndWhiteFilter());
    }

    private void applyBlueFilter() {
        imageCanvas.applyFilter(new BlueFilter());
    }

    private void applyVignetteFilter() {
        imageCanvas.applyFilter(new VignetteFilter());
    }

    private void applyFlipTransform() {
        imageCanvas.applyTransform(new FlipTransform(true)); // Flip horizontally
    }

    private void applyRotateTransform() {
        imageCanvas.applyTransform(new RotateTransform(90)); // Rotate by 90 degrees
    }

    private void openImage(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try (FileInputStream fis = new FileInputStream(selectedFile)) {
                Image image = new Image(fis);
                imageCanvas.setImage(image);
            } catch (FileNotFoundException ex) {
                System.out.println("File not found: " + ex.getMessage());
            } catch (IOException ex) {
                System.out.println("Error reading image file: " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Unexpected error: " + ex.getMessage());
            }
        }
    }

    private void saveImageToDatabase() {
        try {
            Image image = imageCanvas.getCurrentImage();
            if (image != null) {
                ImageDAO.saveImageToDB(image, "EditedImage_" + System.currentTimeMillis());
                System.out.println("Image saved to database.");
            } else {
                throw new exceptions.ImageNotLoadedException("No image to save.");
            }
        } catch (Exception ex) {
            System.err.println("Error saving image to DB: " + ex.getMessage());
        }
    }
    

    public static void main(String[] args) {
        launch(args);
    }
}