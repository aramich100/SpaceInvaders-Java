package spaceintruders;

import java.awt.image.BufferedImage;
import java.util.Map;

public class SpriteSheet extends Object {
    
    BufferedImage sheet;
    private Map<String, Integer> IDs;
    private int spriteSheetTileSizeX;
    private int spriteSheetTileSizeY;

    public SpriteSheet() {
        
    }
    
    public void setSpriteSheetTileSize(int _spriteSheetTileSizeX, int _spriteSheetTileSizeY) {
        spriteSheetTileSizeX = _spriteSheetTileSizeX;
        spriteSheetTileSizeY = _spriteSheetTileSizeY;
    }
    
    public int getSpriteSheetTileSizeX() {
        return spriteSheetTileSizeX;
    }
    
    public int getSpriteSheetTileSizeY() {
        return spriteSheetTileSizeY;
    }
    
    public void setSheet(BufferedImage _sheet) {
        sheet = _sheet;
    }
    
    public BufferedImage getSheet() {
        return sheet;
    }
    
    public void setIDs(Map<String, Integer> _IDs) {
        IDs = _IDs;
    }
    
    public Map<String, Integer> getIDs() {
        return IDs;
    }
}
