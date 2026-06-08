package util;

import javafx.scene.image.Image;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * 活動封面圖片儲存與載入。
 */
public class EventImageUtil {

    private static final String IMAGE_DIR = "data/event_images";

    public static String save(String activityId, File source) {
        if (source == null || !source.exists() || activityId == null) return "";
        try {
            Path dir = Paths.get(IMAGE_DIR);
            Files.createDirectories(dir);
            String ext = getExtension(source.getName());
            Path dest = dir.resolve(activityId + ext);
            Files.copy(source.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);
            return dest.toString().replace("\\", "/");
        } catch (Exception e) {
            System.out.println("圖片儲存失敗: " + e.getMessage());
            return "";
        }
    }

    public static void delete(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return;
        try {
            Files.deleteIfExists(Paths.get(imagePath));
        } catch (Exception ignored) {}
    }

    public static Image loadImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) return null;
        try {
            Path path = Paths.get(imagePath);
            if (!Files.exists(path)) return null;
            Image img = new Image(path.toUri().toString(), false);
            return img.isError() ? null : img;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getExtension(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) return ".png";
        String ext = filename.substring(dot).toLowerCase();
        return ext.matches("\\.(png|jpe?g|gif|bmp|webp)") ? ext : ".png";
    }
}
