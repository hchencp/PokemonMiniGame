import processing.core.PImage;

import java.util.*;

/**
 * Represents the 2D World in which this simulation is running.
 * Keeps track of the size of the world, the background image for each
 * location in the world, and the entities that populate the world.
 */
public final class WorldModel {
    public static final String STUMP_KEY = "stump";
    public static final int STUMP_NUM_PROPERTIES = 0;
    public static final String HOUSE_KEY = "house";
    public static final int HOUSE_NUM_PROPERTIES = 0;
    public static final String OBSTACLE_KEY = "obstacle";
    public static final int OBSTACLE_ANIMATION_PERIOD = 0;
    public static final int OBSTACLE_NUM_PROPERTIES = 1;
    public static final String TREE_KEY = "tree";
    public static final int TREE_ANIMATION_PERIOD = 0;
    public static final int TREE_ACTION_PERIOD = 1;
    public static final int TREE_HEALTH = 2;
    public static final int TREE_NUM_PROPERTIES = 3;

    public static final String FAIRY_KEY = "fairy";
    public static final int FAIRY_ANIMATION_PERIOD = 0;
    public static final int FAIRY_ACTION_PERIOD = 1;
    public static final int FAIRY_NUM_PROPERTIES = 2;
    public static final String DUDE_KEY = "dude";
    public static final int DUDE_ACTION_PERIOD = 0;
    public static final int DUDE_ANIMATION_PERIOD = 1;
    public static final int DUDE_LIMIT = 2;
    public static final int DUDE_NUM_PROPERTIES = 3;
    public static final String SAPLING_KEY = "sapling";
    public static final int SAPLING_HEALTH = 0;
    public static final int SAPLING_NUM_PROPERTIES = 1;
    public static final int PROPERTY_KEY = 0;
    public int numRows;
    public int numCols;
    public Background[][] background;
    public Entity[][] occupancy;
    public Set<Entity> entities;

    public WorldModel() {

    }

    /**
     * Helper method for testing. Don't move or modify this method.
     */
    public List<String> log(){
        List<String> list = new ArrayList<>();
        for (Entity entity : entities) {
            String log = entity.log();
            if(log != null) list.add(log);
        }
        return list;
    }
    public boolean withinBounds(Point pos) {
        return pos.y >= 0 && pos.y < this.numRows && pos.x >= 0 && pos.x < this.numCols;
    }
    public void removeEntityAt(Point pos) {
        if (this.withinBounds(pos) && getOccupancyCell(pos) != null) {
            Entity entity = getOccupancyCell(pos);

            /* This moves the entity just outside of the grid for
             * debugging purposes. */
            entity.setPosition(new Point(-1, -1));
            this.entities.remove(entity);
            setOccupancyCell(null, pos);
        }
    }
    public void removeEntity(EventScheduler scheduler, Entity entity) {
        scheduler.unscheduleAllEvents(entity);
        this.removeEntityAt(entity.getPosition());

    }
    private void parseEntity(String line, ImageStore imageStore) {
        String[] properties = line.split(" ", Functions.ENTITY_NUM_PROPERTIES + 1);
        if (properties.length >= Functions.ENTITY_NUM_PROPERTIES) {
            String key = properties[PROPERTY_KEY];
            String id = properties[Functions.PROPERTY_ID];
            Point pt = new Point(Integer.parseInt(properties[Functions.PROPERTY_COL]), Integer.parseInt(properties[Functions.PROPERTY_ROW]));

            properties = properties.length == Functions.ENTITY_NUM_PROPERTIES ?
                    new String[0] : properties[Functions.ENTITY_NUM_PROPERTIES].split(" ");

            switch (key) {
                case Functions.OBSTACLE_KEY -> this.parseObstacle(properties, pt, id, imageStore);
                case Functions.DUDE_KEY -> this.parseDude(properties, pt, id, imageStore);
                case Functions.FAIRY_KEY -> this.parseFairy(properties, pt, id, imageStore);
                case Functions.HOUSE_KEY -> this.parseHouse(properties, pt, id, imageStore);
                case Functions.TREE_KEY -> this.parseTree(properties, pt, id, imageStore);
                case Functions.SAPLING_KEY -> this.parseSapling(properties, pt, id, imageStore);
                case Functions.STUMP_KEY -> this.parseStump(properties, pt, id, imageStore);
                default -> throw new IllegalArgumentException("Entity key is unknown");
            }
        }else{
            throw new IllegalArgumentException("Entity must be formatted as [key] [id] [x] [y] ...");
        }
    }
    private Background getBackgroundCell(Point pos) {
        return this.background[pos.y][pos.x];
    }
    public Optional<PImage> getBackgroundImage(Point pos) {
        if (this.withinBounds(pos)) {
            return Optional.of(getCurrentImage(this.getBackgroundCell(pos)));
        } else {
            return Optional.empty();
        }
    }
    private void parseSapling(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == SAPLING_NUM_PROPERTIES) {
            int health = Integer.parseInt(properties[SAPLING_HEALTH]);
            Entity entity = new Sapling(id, pt, imageStore.getImageList( SAPLING_KEY), health);
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", SAPLING_KEY, SAPLING_NUM_PROPERTIES));
        }
    }
    private void parseDude(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == DUDE_NUM_PROPERTIES) {
            Entity entity = new Dude_not_full(id, pt, imageStore.getImageList( DUDE_KEY),Integer.parseInt(properties[DUDE_LIMIT]),Double.parseDouble(properties[DUDE_ACTION_PERIOD]), Double.parseDouble(properties[DUDE_ANIMATION_PERIOD]));
            this.tryAddEntity( entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", DUDE_KEY, DUDE_NUM_PROPERTIES));
        }
    }
    private void parseFairy(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == FAIRY_NUM_PROPERTIES) {
            Entity entity = new Fairy(id, pt, Double.parseDouble(properties[FAIRY_ACTION_PERIOD]), Double.parseDouble(properties[FAIRY_ANIMATION_PERIOD]), imageStore.getImageList(FAIRY_KEY));
            this.tryAddEntity( entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", FAIRY_KEY, FAIRY_NUM_PROPERTIES));
        }
    }

    private void parseTree(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == TREE_NUM_PROPERTIES) {
            Entity entity = new Tree(id, pt, Double.parseDouble(properties[TREE_ACTION_PERIOD]), Double.parseDouble(properties[TREE_ANIMATION_PERIOD]), Integer.parseInt(properties[TREE_HEALTH]), imageStore.getImageList( TREE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", TREE_KEY, TREE_NUM_PROPERTIES));
        }
    }
    private void parseObstacle(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == OBSTACLE_NUM_PROPERTIES) {
            Entity entity = new Obstacle(id, pt, Double.parseDouble(properties[OBSTACLE_ANIMATION_PERIOD]), imageStore.getImageList( OBSTACLE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", OBSTACLE_KEY, OBSTACLE_NUM_PROPERTIES));
        }
    }
    private void parseHouse(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == HOUSE_NUM_PROPERTIES) {
            Entity entity = new House(id, pt, imageStore.getImageList( HOUSE_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", HOUSE_KEY, HOUSE_NUM_PROPERTIES));
        }
    }
    private void parseStump(String[] properties, Point pt, String id, ImageStore imageStore) {
        if (properties.length == STUMP_NUM_PROPERTIES) {
            Entity entity = new Stump(id, pt, imageStore.getImageList(STUMP_KEY));
            this.tryAddEntity(entity);
        }else{
            throw new IllegalArgumentException(String.format("%s requires %d properties when parsing", STUMP_KEY, STUMP_NUM_PROPERTIES));
        }
    }
    private void parseBackgroundRow(String line, int row, ImageStore imageStore) {
        String[] cells = line.split(" ");
        if(row < this.numRows){
            int rows = Math.min(cells.length, this.numCols);
            for (int col = 0; col < rows; col++){
                this.background[row][col] = new Background(cells[col], imageStore.getImageList( cells[col]));
            }
        }
    }
    public void load(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        parseSaveFile(saveFile, imageStore, defaultBackground);
        if(this.background == null){
            this.background = new Background[this.numRows][this.numCols];
            for (Background[] row : this.background)
                Arrays.fill(row, defaultBackground);
        }
        if(this.occupancy == null){
            this.occupancy = new Entity[this.numRows][this.numCols];
            this.entities = new HashSet<>();
        }
    }
    private void tryAddEntity( Entity entity) {
        if (this.isOccupied(entity.getPosition())) {
            // arguably the wrong type of exception, but we are not
            // defining our own exceptions yet
            throw new IllegalArgumentException("position occupied");
        }

        entity.addEntity(this);
    }

    private void parseSaveFile(Scanner saveFile, ImageStore imageStore, Background defaultBackground){
        String lastHeader = "";
        int headerLine = 0;
        int lineCounter = 0;
        while(saveFile.hasNextLine()){
            lineCounter++;
            String line = saveFile.nextLine().strip();
            if(line.endsWith(":")){
                headerLine = lineCounter;
                lastHeader = line;
                switch (line){
                    case "Backgrounds:" -> this.background = new Background[this.numRows][this.numCols];
                    case "Entities:" -> {
                        this.occupancy = new Entity[this.numRows][this.numCols];
                        this.entities = new HashSet<>();
                    }
                }
            }else{
                switch (lastHeader){
                    case "Rows:" -> this.numRows = Integer.parseInt(line);
                    case "Cols:" -> this.numCols = Integer.parseInt(line);
                    case "Backgrounds:" -> this.parseBackgroundRow(line, lineCounter-headerLine-1, imageStore);
                    case "Entities:" -> this.parseEntity(line, imageStore);
                }
            }
        }
    }
    public Optional<Entity> getOccupant( Point pos) {
        if (this.isOccupied(pos)) {
            return Optional.of(getOccupancyCell(pos));
        } else {
            return Optional.empty();
        }
    }
    public static PImage getCurrentImage(Object object) {
        if (object instanceof Background background) {
            return background.images.get(background.imageIndex);
        } else if (object instanceof Entity entity) {
            return entity.getImages().get(entity.getImageIndex()% entity.getImages().size());
        } else {
            throw new UnsupportedOperationException(String.format("getCurrentImage not supported for %s", object));
        }
    }
    public boolean isOccupied(Point pos) {
        return this.withinBounds(pos) && getOccupancyCell(pos) != null;
    }


    public void setOccupancyCell(Entity entity, Point point) {
        occupancy[point.y][point.x] = entity;
    }

    public Entity getOccupancyCell(Point point) {
        return occupancy[point.y][point.x];
    }

    public void moveEntity(EventScheduler scheduler, Entity entity, Point point) {
        Point oldPos = entity.getPosition();
        if (withinBounds(point) && !point.equals(oldPos)) {
            setOccupancyCell(null, oldPos);
            Optional<Entity> occupant = getOccupant(point);
            occupant.ifPresent(target -> removeEntity(scheduler, target));
            setOccupancyCell(entity, point);
            entity.setPosition(point);
        }
    }
    public Optional<Entity> findNearest(Point pos, List<Class<?>> kinds) {
        List<Entity> ofType = new LinkedList<>();
        for (Class<?> kind : kinds) {
            for (Entity entity : this.entities) {
                if (kind.isInstance(entity)) {
                    ofType.add(entity);
                }
            }
        }

        return nearestEntity(ofType, pos);
    }
    public Optional<Entity> nearestEntity(List<Entity> entities, Point pos) {
        if (entities.isEmpty()) {
            return Optional.empty();
        } else {
            Entity nearest = entities.get(0);
            int nearestDistance = Functions.distanceSquared(nearest.getPosition(), pos);

            for (Entity other : entities) {
                int otherDistance = Functions.distanceSquared(other.getPosition(), pos);

                if (otherDistance < nearestDistance) {
                    nearest = other;
                    nearestDistance = otherDistance;
                }
            }

            return Optional.of(nearest);
        }
    }
    public void setBackgroundCell(Point pos, Background background) {
        this.background[pos.y][pos.x] = background;
    }
}
