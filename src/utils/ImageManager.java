package utils;

import javafx.concurrent.Task;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import views.UserView.InventoryItem;

import java.util.HashMap;
import java.util.Map;

public class ImageManager {
    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final String DEFAULT_IMAGE_URL = "https://via.placeholder.com/150x150/cccccc/666666?text=No+Image";
    
    /**
     * Creates an ImageView for a product with async loading and caching
     */
    public static ImageView createProductImageView(InventoryItem item, double width, double height) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        
        // Set placeholder initially
        imageView.setImage(createPlaceholderImage());
        
        // Load actual image asynchronously
        loadImageAsync(item.getImageUrl(), item.getId(), imageView);
        
        return imageView;
    }
    
    /**
     * Loads image asynchronously with caching
     */
    private static void loadImageAsync(String imageUrl, String itemId, ImageView imageView) {
        // Check cache first
        String cacheKey = (imageUrl != null && !imageUrl.isBlank() ? imageUrl : DEFAULT_IMAGE_URL)
        + "#" + itemId;
        if (imageCache.containsKey(cacheKey)) {
            imageView.setImage(imageCache.get(cacheKey));
           return;
         }
        
        Task<Image> loadImageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                try {
                    String urlToLoad = (imageUrl != null && !imageUrl.trim().isEmpty()) 
                        ? imageUrl : DEFAULT_IMAGE_URL;
                    return new Image(urlToLoad, true); // true for background loading
                } catch (Exception e) {
                    return new Image(DEFAULT_IMAGE_URL, true);
                }
            }
        };
        
        loadImageTask.setOnSucceeded(e -> {
            Image image = loadImageTask.getValue();
            imageCache.put(cacheKey, image);
            imageView.setImage(image);
        });
        
        loadImageTask.setOnFailed(e -> {
            Image fallbackImage = createPlaceholderImage();
            imageCache.put(cacheKey, fallbackImage);
            imageView.setImage(fallbackImage);
        });
        
        Thread imageThread = new Thread(loadImageTask);
        imageThread.setDaemon(true);
        imageThread.start();
    }
    
    /**
     * Creates a simple placeholder image
     */
    private static Image createPlaceholderImage() {
        // For now, use a default online placeholder
        // You could also create a programmatic placeholder here
        try {
            return new Image(DEFAULT_IMAGE_URL, true);
        } catch (Exception e) {
            // If even the placeholder fails, return null
            return null;
        }
    }
    
    /**
     * Creates a placeholder StackPane when Image fails completely
     */
    public static StackPane createPlaceholderPane(double width, double height) {
        StackPane placeholder = new StackPane();
        
        Rectangle rect = new Rectangle(width, height);
        rect.setFill(Color.LIGHTGRAY);
        rect.setStroke(Color.GRAY);
        
        Label label = new Label("No Image");
        label.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        label.setTextFill(Color.DARKGRAY);
        
        placeholder.getChildren().addAll(rect, label);
        return placeholder;
    }
    
    /**
     * Clears the image cache (useful for memory management)
     */
    public static void clearCache() {
        imageCache.clear();
    }
}
