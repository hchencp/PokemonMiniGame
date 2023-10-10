import processing.core.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Random;

public final class VirtualWorld extends PApplet {
    private static String[] ARGS;

    public static final int VIEW_WIDTH = 640;
    public static final int VIEW_HEIGHT = 480;
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;

    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;

    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final String DEFAULT_IMAGE_NAME = "background_default";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;

    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.10;

    public String loadFile = "world.sav";
    public long startTimeMillis = 0;
    public double timeScale = 1.0;

    public ImageStore imageStore;
    public WorldModel world;
    public WorldView view;
    public EventScheduler scheduler;

    public void settings() {
        size(VIEW_WIDTH, VIEW_HEIGHT);
    }

    /*
       Processing entry point for "sketch" setup.
    */
    public void setup() {
        parseCommandLine(ARGS);
        loadImages(IMAGE_LIST_FILE_NAME);
        loadWorld(loadFile, this.imageStore);

        this.view = new WorldView(VIEW_ROWS, VIEW_COLS, this, world, TILE_WIDTH, TILE_HEIGHT);
        this.scheduler = new EventScheduler();
        this.startTimeMillis = System.currentTimeMillis();
        this.scheduleActions(world, scheduler, imageStore);
    }

    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = (appTime - scheduler.currentTime)/timeScale;
        this.update(frameTime);
        view.drawViewport();
    }

    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    // Just for debugging and for P5
    // Be sure to refactor this method as appropriate
    public void mousePressed() {
        Point location = mouseToPoint();
        if (!(world.isOccupied(location))) {
        for (int i = location.x - 1; i < location.x + 2; i++) {
            for (int j = location.y - 1; j < location.y + 2; j++) {
                if (!(world.isOccupied(location))&&world.withinBounds(new Point(i, j)))
                    world.setBackgroundCell(new Point(i, j), new Background("magma", imageStore.getImageList("magma")));
                if (world.withinBounds(new Point(i, j)) && world.getOccupancyCell(new Point(i, j)) instanceof Tree){
                    world.setBackgroundCell(new Point(i, j), new Background("magma", imageStore.getImageList("magma")));
                    world.removeEntity(scheduler,world.getOccupancyCell(new Point(i, j)));
                    Charmander entity = new Charmander("Charmander", new Point(i, j), 1, 0.5, imageStore.getImageList("charmander"));
                    Chansey entity2 = new Chansey("Chansey", new Point(i, j), 1, 0.5, imageStore.getImageList("chansey"));
                    Random rand = new Random();
                    int randomInt = rand.nextInt(2);
                    if (randomInt ==0) {
                        entity2.addEntity(world);
                        entity2.scheduleActions(scheduler, world, imageStore);
                    }
                    if (randomInt ==1) {
                        entity.addEntity(world);
                        entity.scheduleActions(scheduler, world, imageStore);
                    }
                }



            }
        }
        Charmander entity = new Charmander("Charmander", location,  1, 0.5,imageStore.getImageList("charmander"));
            entity.addEntity(world);
            entity.scheduleActions(scheduler, world, imageStore);
        }
        if (world.getOccupancyCell(location) instanceof Obstacle) {
            Piplup entity = new Piplup("Piplup", location,  1, 0.5,imageStore.getImageList("piplup"));
            entity.addEntity(world);
            entity.scheduleActions(scheduler, world, imageStore);
        }

    }

    public void scheduleActions(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        for (Entity entity : world.entities) {
            if (entity instanceof animatedEntity) {
                ((animatedEntity)entity).scheduleActions(scheduler, world, imageStore);
            }

        }
    }

    private Point mouseToPoint() {
        return view.viewport.viewportToWorld(mouseX / TILE_WIDTH, mouseY / TILE_HEIGHT);
    }

    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }
            view.shiftView(dx, dy);
        }
    }

    public static Background createDefaultBackground(ImageStore imageStore) {
        return new Background(DEFAULT_IMAGE_NAME, imageStore.getImageList(DEFAULT_IMAGE_NAME));
    }

    public static PImage createImageColored(int width, int height, int color) {
        PImage img = new PImage(width, height, RGB);
        img.loadPixels();
        Arrays.fill(img.pixels, color);
        img.updatePixels();
        return img;
    }

    public void loadImages(String filename) {
        this.imageStore = new ImageStore(createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        try {
            Scanner in = new Scanner(new File(filename));
            imageStore.loadImages(in, this);
        } catch (FileNotFoundException e) {
            System.err.println(e.getMessage());
        }
    }

    public void loadWorld(String file, ImageStore imageStore) {
        this.world = new WorldModel();
        try {
            Scanner in = new Scanner(new File(file));
            world.load(in, imageStore, createDefaultBackground(imageStore));
        } catch (FileNotFoundException e) {
            Scanner in = new Scanner(file);
            world.load(in, imageStore, createDefaultBackground(imageStore));
        }
    }

    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                default -> loadFile = arg;
            }
        }
    }

    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }
}
