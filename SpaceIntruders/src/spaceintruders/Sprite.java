package spaceintruders;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Sprite {
    
    private String spriteName;
    private Image spriteImage;
    private ImageView spriteView;
    private int spriteTileSizeX;
    private int spriteTileSizeY;
    
    public Sprite (String _spriteName, Image _spriteImage, int _spriteTileSizeX, int _spriteTileSizeY) {
        spriteName = _spriteName;
        spriteImage = _spriteImage;
        spriteTileSizeX = _spriteTileSizeX;
        spriteTileSizeY = _spriteTileSizeY;
        spriteView = new ImageView();
        spriteView.setImage(spriteImage);
        spriteView.setPreserveRatio(true);
        spriteView.setSmooth(true);
        spriteView.setCache(true);
        spriteView.setLayoutX(0);
        spriteView.setLayoutY(0);
    }
    
    public void setSpriteName(String _spriteName) {
        spriteName = _spriteName;
    }
    
    public String getSpriteName() {
        return spriteName;
    }
    
    public void setSpriteImage(String _spriteName, Image _spriteImage) {
        spriteName = _spriteName;
        spriteImage = _spriteImage;
        spriteView.setImage(spriteImage);
    }
    
    public Image getSpriteImage() {
        return spriteImage;
    }
    
    public void setView(ImageView _spriteView) {
        spriteView = _spriteView;
    }
    
    public ImageView getView() {
        return spriteView;
    }
    
    public void setSpriteTileSizeX(int _spriteTileSizeX) {
        spriteTileSizeX = _spriteTileSizeX;
    }
    
    public int getSpriteTileSizeX() {
        return spriteTileSizeX;
    }
    
    public void setSpriteTileSizeY(int _spriteTileSizeY) {
        spriteTileSizeY = _spriteTileSizeY;
    }
    
    public int getSpriteTileSizeY() {
        return spriteTileSizeY;
    }
    
    public void setInternalPosition(Vec2 internalPos) {
        spriteView.setTranslateX(internalPos.getX() - spriteTileSizeX/2);
        spriteView.setTranslateY(internalPos.getY() - spriteTileSizeY/2);
    }
}
