package spaceintruders;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;

public class AssetManager {

    private final Map<String, Object> assets;

    public AssetManager() {
        assets = new HashMap<>();
    }

    public void addAsset(String id, Object obj) {
        assets.put(id, obj);
    }

    public Object getAsset(String id) {
        return assets.get(id);
    }

    public void addSpriteSheet(String spriteSheetID, 
            URL url, 
            Map<String, Integer> spriteIDs, 
            int spriteSheetTileSizeX, 
            int spriteSheetTileSizeY) {
        SpriteSheet ss = new SpriteSheet();
        try {
            ss.setSheet(ImageIO.read(url));
        } catch (IOException e) {
            e.printStackTrace();
        }
        ss.setIDs(spriteIDs);
        ss.setSpriteSheetTileSize(spriteSheetTileSizeX, spriteSheetTileSizeY);
        assets.put(spriteSheetID, ss);
    }

    public Sprite getSpriteFromSheet(String spriteSheetID, String spriteName) {
        SpriteSheet spriteSheet = (SpriteSheet) assets.get(spriteSheetID);
        Image spriteImage = SwingFXUtils.toFXImage(spriteSheet.getSheet().getSubimage(spriteSheet.getSpriteSheetTileSizeX() * spriteSheet.getIDs().get(spriteName), 0, spriteSheet.getSpriteSheetTileSizeX(), spriteSheet.getSpriteSheetTileSizeY()), null);
        return new Sprite(spriteName, spriteImage, spriteSheet.getSpriteSheetTileSizeX(), spriteSheet.getSpriteSheetTileSizeY());
    }

}
