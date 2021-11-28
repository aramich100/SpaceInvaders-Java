package spaceintruders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class SpaceIntruders extends Application {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private StackPane root;
    private Scene scene;
    private AssetManager am;
    private Keyboard input;

    private Player player;
    private List<Alien> aliens;
    private List<Bullet> bullets;

    private double lastFrameTime = 0.0;
    private Random rand;

    private int alienDescentAmount;
    private int alienDescentTimeInterval;
    private int alienBulletLaunchInverseProbability;
    private Boolean gameover;

    private static enum GameoverType {
        WON, LOST_DIED, LOST_LANDED, LOST_CRASH
    };
    GameoverType gameoverType;
    private Timeline gameoverChecker;
    private Sprite gameoverSprite;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        initializeWindow(stage);
        am = new AssetManager();
        
        HashMap<String, Integer> gameoverLostSpriteIDs = new HashMap<>();
        gameoverLostSpriteIDs.put("GAMEOVER_LOST", 0);
        am.addSpriteSheet("GAMEOVER_LOST", this.getClass().getResource("assets/gameover_lost.png"), gameoverLostSpriteIDs, WIDTH, HEIGHT);
        HashMap<String, Integer> gameoverWonSpriteIDs = new HashMap<>();
        gameoverWonSpriteIDs.put("GAMEOVER_WON", 0);
        am.addSpriteSheet("GAMEOVER_WON", this.getClass().getResource("assets/gameover_won.png"), gameoverWonSpriteIDs, WIDTH, HEIGHT);
        HashMap<String, Integer> backgroundSpriteIDs = new HashMap<>();
        backgroundSpriteIDs.put("0", 0);
        backgroundSpriteIDs.put("1", 1);
        backgroundSpriteIDs.put("2", 2);
        backgroundSpriteIDs.put("3", 3);
        backgroundSpriteIDs.put("4", 4);
        am.addSpriteSheet("BACKGROUND", this.getClass().getResource("assets/background.png"), backgroundSpriteIDs, WIDTH, HEIGHT);
        //am.addAsset("BACKGROUND_MUSIC", new Media("assets/music.mp3"));
        HashMap<String, Integer> entitySpriteIDs = new HashMap<>();
        entitySpriteIDs.put("ALIEN_1_1", 0);
        entitySpriteIDs.put("ALIEN_1_2", 1);
        entitySpriteIDs.put("ALIEN_2_1", 2);
        entitySpriteIDs.put("ALIEN_2_2", 3);
        entitySpriteIDs.put("ALIEN_3_1", 4);
        entitySpriteIDs.put("ALIEN_3_2", 5);
        entitySpriteIDs.put("PLAYER", 6);
        entitySpriteIDs.put("A_BULLET_1", 7);
        entitySpriteIDs.put("A_BULLET_2", 8);
        entitySpriteIDs.put("P_BULLET_1", 9);
        entitySpriteIDs.put("P_BULLET_2", 10);
        am.addSpriteSheet("ENTITIES", this.getClass().getResource("assets/sprite_sheet.png"), entitySpriteIDs, 32, 18);

        rand = new Random();
        Vec2 playerStartPos = new Vec2(WIDTH / 2, HEIGHT - am.getSpriteFromSheet("ENTITIES", "PLAYER").getSpriteTileSizeY() / 2);
        player = new Player(playerStartPos, 100, 3, 100, am.getSpriteFromSheet("ENTITIES", "PLAYER"));
        input = new Keyboard();
        aliens = new ArrayList<>();
        bullets = new ArrayList<>();
        gameover = false;
        alienBulletLaunchInverseProbability = 800;
        alienDescentAmount = 32;
        alienDescentTimeInterval = 5000;
        initializeAliens();
        addToPane(player.getSprite().getView());
        startInputEventHandlerThread();
        startRenderThread();
        setBackgroundImage(am.getSpriteFromSheet("BACKGROUND", "0").getSpriteImage());
        startBackgroundAnimationTimer();
        startAlienAnimationTimer();
        startBulletAnimationTimer();
        startAlienDescentTimer();
        startGameoverChecker();
    }

    private void startGameoverChecker() {
        gameoverChecker = new Timeline(new KeyFrame(
                Duration.millis(500), (ActionEvent ae) -> {
            if (gameover) {
                switch (gameoverType) {
                    case WON:
                        gameoverSprite = am.getSpriteFromSheet("GAMEOVER_WON", "GAMEOVER_WON");
                        break;
                    case LOST_DIED:
                    case LOST_LANDED:
                    case LOST_CRASH:
                        gameoverSprite = am.getSpriteFromSheet("GAMEOVER_LOST", "GAMEOVER_LOST");
                        break;
                }
                Platform.runLater(() -> addToPane(gameoverSprite.getView()));
                gameoverChecker.stop();
            }
        }));
        gameoverChecker.setCycleCount(Animation.INDEFINITE);
        gameoverChecker.play();
    }

    private void startAlienDescentTimer() {
        Timeline alienDescent = new Timeline(new KeyFrame(
                Duration.millis(alienDescentTimeInterval), (ActionEvent ae) -> {
            aliens.forEach((a) -> {
                if (a.getPos().getY() == HEIGHT - a.getSprite().getSpriteTileSizeY() / 2) {
                    gameover = true;
                    gameoverType = GameoverType.LOST_LANDED;
                } else if (HEIGHT - a.getPos().getY() - a.getSprite().getSpriteTileSizeY() / 2 <= alienDescentAmount) {
                    a.setPos(new Vec2(a.getPos().getX(), HEIGHT - a.getSprite().getSpriteTileSizeY() / 2));
                } else {
                    a.setPos(new Vec2(a.getPos().getX(), a.getPos().getY() + alienDescentAmount));
                }
            });
        }));
        alienDescent.setCycleCount(Animation.INDEFINITE);
        alienDescent.play();
    }

    private void startBulletAnimationTimer() {
        Timeline bulletAlternator = new Timeline(new KeyFrame(
                Duration.millis(300), new EventHandler<ActionEvent>() {
            private Integer count = 1;

            @Override
            public void handle(ActionEvent ae) {
                bullets.forEach((b) -> {
                    Sprite tmp = am.getSpriteFromSheet("ENTITIES", b.getSprite().getSpriteName().substring(0, 9) + count.toString());
                    b.getSprite().setSpriteImage(tmp.getSpriteName(), tmp.getSpriteImage());
                });
                if (count == 2) {
                    count = 1;
                } else {
                    ++count;
                }
            }
        }));
        bulletAlternator.setCycleCount(Animation.INDEFINITE);
        bulletAlternator.play();
    }

    private void startBackgroundAnimationTimer() {
        Timeline backgroundAlternator = new Timeline(new KeyFrame(
                Duration.millis(2500), new EventHandler<ActionEvent>() {
            private Integer count = 1;

            @Override
            public void handle(ActionEvent ae) {
                Image img = am.getSpriteFromSheet("BACKGROUND", count.toString()).getSpriteImage();
                if (count == 4) {
                    count = 0;
                } else {
                    ++count;
                }
                Platform.runLater(() -> setBackgroundImage(img));
            }
        }));
        backgroundAlternator.setCycleCount(Animation.INDEFINITE);
        backgroundAlternator.play();
    }

    private void startAlienAnimationTimer() {
        Timeline alienAlternator = new Timeline(new KeyFrame(
                Duration.millis(1000), new EventHandler<ActionEvent>() {
            private Integer count = 2;

            @Override
            public void handle(ActionEvent ae) {
                aliens.forEach((a) -> {
                    Sprite tmp = am.getSpriteFromSheet("ENTITIES", a.getSprite().getSpriteName().substring(0, 8) + count.toString());
                    a.getSprite().setSpriteImage(tmp.getSpriteName(), tmp.getSpriteImage());
                });
                if (count == 2) {
                    count = 1;
                } else {
                    ++count;
                }
            }
        }));
        alienAlternator.setCycleCount(Animation.INDEFINITE);
        alienAlternator.play();
    }

    private void setBackgroundImage(Image img) {
        BackgroundSize backgroundSize = new BackgroundSize(100, 100, true, true, true, false);
        BackgroundImage backgroundImage = new BackgroundImage(img, BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, backgroundSize);
        root.setBackground(new Background(backgroundImage));
    }

    private void initializeWindow(Stage stage) {
        root = new StackPane();
        root.setAlignment(Pos.TOP_LEFT);
        scene = new Scene(root, WIDTH, HEIGHT);
        stage.setTitle("Alien Intruders");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    private void initializeAliens() {
        for (int i = 0; i < 4; ++i) {
            double skip = 80;
            for (int j = 0; j < 8; ++j) {
                Sprite sprite = null;
                switch (rand.nextInt(3)) {
                    case 0:
                        sprite = am.getSpriteFromSheet("ENTITIES", "ALIEN_1_1");
                        break;
                    case 1:
                        sprite = am.getSpriteFromSheet("ENTITIES", "ALIEN_2_1");
                        break;
                    case 2:
                        sprite = am.getSpriteFromSheet("ENTITIES", "ALIEN_3_1");
                        break;
                }
                aliens.add(new Alien(new Vec2((j + 1) * skip, (i + 1) * skip), 1, 0, 25, 0, 1, sprite));
                addToPane(aliens.get(i * 8 + j).getSprite().getView());
            }
        }
    }

    private void startInputEventHandlerThread() {
        scene.addEventFilter(KeyEvent.ANY, (KeyEvent ke) -> {
            if (ke.getEventType() == KeyEvent.KEY_PRESSED || ke.getEventType() == KeyEvent.KEY_RELEASED) {
                Boolean key_pressed = (ke.getEventType() == KeyEvent.KEY_PRESSED);
                switch (ke.getCode()) {
                    case DOWN:
                        input.down = key_pressed;
                        break;
                    case UP:
                        input.up = key_pressed;
                        break;
                    case LEFT:
                        input.left = key_pressed;
                        break;
                    case RIGHT:
                        input.right = key_pressed;
                        break;
                    case T:
                        input.t = key_pressed;
                        break;
                    case SPACE:
                        input.space = key_pressed;
                        break;
                    case X:
                        input.x = key_pressed;
                        break;
                    case ENTER:
                        input.enter = key_pressed;
                        break;
                    case ESCAPE:
                        Platform.exit();
                        System.exit(0);
                        break;
                }
                ke.consume();
            }
        });
    }

    private void startRenderThread() {
        long initialTime = System.nanoTime();
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (player.getHealth() == 0) {
                    gameover = true;
                    gameoverType = GameoverType.LOST_DIED;
                }
                if (gameover) {
                    this.stop();
                }
                double timeElapsed = (now - initialTime) / 1000000000.0;
                double lastFrameDeltaTime = timeElapsed - lastFrameTime;
                render(lastFrameDeltaTime);
                lastFrameTime = timeElapsed;
            }
        }.start();
    }

    private void drawEntity(Entity e) {
        e.getSprite().setInternalPosition(e.getPos());
    }

    private void addToPane(Node n) {
        root.getChildren().add(n);
    }

    private void removeFromPane(Node n) {
        root.getChildren().remove(n);
    }

    private void render(double deltaTime) {
        int dir = 0;
        if (input.left) {
            dir = -1;
        } else if (input.right) {
            dir = 1;
        }

        Vec2 newPlayerPosition = new Vec2(player.getPos().getX() + player.getSpeed() * deltaTime * dir, player.getPos().getY());
        if (newPlayerPosition.getX() > WIDTH || newPlayerPosition.getX() < 0) {
            newPlayerPosition = player.getPos();
        } else if (newPlayerPosition.getY() > HEIGHT || newPlayerPosition.getY() < 0) {
            newPlayerPosition = player.getPos();
        }
        player.setPos(newPlayerPosition);

        for (int i = 0; i < aliens.size(); ++i) {
            Alien a = aliens.get(i);
            if ((a.getPos().getX() + a.getSprite().getSpriteTileSizeX() / 2 > player.getPos().getX() - player.getSprite().getSpriteTileSizeX() / 2
                    && a.getPos().getX() + a.getSprite().getSpriteTileSizeX() / 2 < player.getPos().getX() + player.getSprite().getSpriteTileSizeX() / 2
                    && a.getPos().getY() > player.getPos().getY() - player.getSprite().getSpriteTileSizeY() / 2
                    && a.getPos().getY() < player.getPos().getY() + player.getSprite().getSpriteTileSizeY() / 2)
                    || (a.getPos().getX() - a.getSprite().getSpriteTileSizeX() / 2 > player.getPos().getX() - player.getSprite().getSpriteTileSizeX() / 2
                    && a.getPos().getX() - a.getSprite().getSpriteTileSizeX() / 2 < player.getPos().getX() + player.getSprite().getSpriteTileSizeX() / 2
                    && a.getPos().getY() > player.getPos().getY() - player.getSprite().getSpriteTileSizeY() / 2
                    && a.getPos().getY() < player.getPos().getY() + player.getSprite().getSpriteTileSizeY() / 2)) {
                gameover = true;
                gameoverType = GameoverType.LOST_CRASH;
            }
        }

        if (input.space) {
            if (!input.spaceWasPressedLastFrame) {
                Bullet tmp = new Bullet(new Vec2(player.getPos()), -200, am.getSpriteFromSheet("ENTITIES", "P_BULLET_1"));
                bullets.add(tmp);
                Platform.runLater(() -> addToPane(tmp.getSprite().getView()));
            }
            input.spaceWasPressedLastFrame = true;
        } else if (input.spaceWasPressedLastFrame) {
            input.spaceWasPressedLastFrame = false;
        }

        if (!aliens.isEmpty()) {
            if (rand.nextInt(alienBulletLaunchInverseProbability) == 0) {
                int alienIndex = rand.nextInt(aliens.size());
                bullets.add(new Bullet(new Vec2(aliens.get(alienIndex).getPos()), 200, am.getSpriteFromSheet("ENTITIES", "A_BULLET_1")));
                Platform.runLater(() -> addToPane(bullets.get(bullets.size() - 1).getSprite().getView()));
            }
        } else {
            gameover = true;
            gameoverType = GameoverType.WON;
        }

        for (int i = 0; i < bullets.size(); ++i) {
            Bullet b = bullets.get(i);

            b.setPos(new Vec2(b.getPos().getX(), b.getPos().getY() + b.getSpeed() * deltaTime));

            if (b.getPos().getY() > HEIGHT || b.getPos().getY() < 0) {
                Platform.runLater(() -> {
                    removeFromPane(b.getSprite().getView());
                    bullets.remove(b);
                });
                continue;
            } else if (b.getSprite().getSpriteName().charAt(0) == 'A') {
                if (b.getPos().getX() > player.getPos().getX() - player.getSprite().getSpriteTileSizeX() / 2
                        && b.getPos().getX() < player.getPos().getX() + player.getSprite().getSpriteTileSizeX() / 2
                        && b.getPos().getY() > player.getPos().getY() - player.getSprite().getSpriteTileSizeY() / 2
                        && b.getPos().getY() < player.getPos().getY() + player.getSprite().getSpriteTileSizeY() / 2) {
                    Platform.runLater(() -> {
                        removeFromPane(b.getSprite().getView());
                    });
                    bullets.remove(b);
                    player.setHealth(player.getHealth() - 1);
                }
            } else if (b.getSprite().getSpriteName().charAt(0) == 'P') {
                for (int j = 0; j < aliens.size(); ++j) {
                    Alien a = aliens.get(j);
                    if (b.getPos().getX() > a.getPos().getX() - a.getSprite().getSpriteTileSizeX() / 2
                            && b.getPos().getX() < a.getPos().getX() + a.getSprite().getSpriteTileSizeX() / 2
                            && b.getPos().getY() > a.getPos().getY() - a.getSprite().getSpriteTileSizeY() / 2
                            && b.getPos().getY() < a.getPos().getY() + a.getSprite().getSpriteTileSizeY() / 2) {
                        Platform.runLater(() -> {
                            removeFromPane(b.getSprite().getView());
                            removeFromPane(a.getSprite().getView());
                        });
                        bullets.remove(b);
                        aliens.remove(a);
                    }
                }
            }

            drawEntity(b);
        }

        aliens.stream().map((a) -> {
            double angle = a.getPhi() + a.getAngularFrequency() * deltaTime;
            a.setPos(new Vec2(a.getAnchorPos().getX() + a.getStride() * Math.sin(angle), a.getPos().getY()));
            a.setPhi(angle);
            return a;
        }).forEachOrdered((a) -> {
            drawEntity(a);
        });

        drawEntity(player);
    }
}
