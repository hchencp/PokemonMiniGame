import processing.core.PImage;

import java.util.List;
public final class Sapling implements Plant {
    public static final double SAPLING_ACTION_ANIMATION_PERIOD = 1.000; // have to be in sync since grows and gains health at same time
    public static final int SAPLING_HEALTH_LIMIT = 5;

    public static final int SAPLING_HEALTH = 0;
    public static final int SAPLING_NUM_PROPERTIES = 1;
    public static final int TREE_HEALTH_MAX = 3;
    public static final int TREE_HEALTH_MIN = 1;
    public static final double TREE_ANIMATION_MAX = 0.600;
    public static final double TREE_ANIMATION_MIN = 0.050;
    public static final double TREE_ACTION_MIN = 1.000;
    public static final double TREE_ACTION_MAX = 1.400;
    public static final String STUMP_KEY = "stump";
    public static final String TREE_KEY = "tree";
    public static final String SAPLING_KEY = "sapling";

    public String id;
    public Point position;
    public List<PImage> images;
    public double actionPeriod;
    public double animationPeriod;
    public int imageIndex;
    public int health;
    public int healthLimit;


    public Sapling(String id, Point position, List<PImage> images, double actionPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = actionPeriod;
        this.imageIndex = 0;
        this.health = health;
        this.healthLimit = healthLimit;


    }


    public String log() {
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public void addEntity(WorldModel world) {
        if (world.withinBounds(this.position)) {
            world.setOccupancyCell(this, this.position);
            world.entities.add(this);
        }
    }

    public double getAnimationPeriod() {

        return this.animationPeriod;

    }

    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {

        scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());

    }

    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        this.health++;
        if (!this.transformPlant(world, scheduler, imageStore)) {
            scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
        }
    }

    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        return this.transformSapling(world, scheduler, imageStore);

    }

    private boolean transformSapling(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Entity stump = new Stump(STUMP_KEY + "_" + this.id, this.position, imageStore.getImageList( STUMP_KEY));

            world.removeEntity(scheduler, this);

            stump.addEntity(world);

            return true;
        } else if (this.health >= this.healthLimit) {
            Entity tree = new Tree(TREE_KEY + "_" + this.id, this.position, Functions.getNumFromRange(TREE_ACTION_MAX, TREE_ACTION_MIN), Functions.getNumFromRange(TREE_ANIMATION_MAX, TREE_ANIMATION_MIN), Functions.getIntFromRange(TREE_HEALTH_MAX, TREE_HEALTH_MIN), imageStore.getImageList( TREE_KEY));

            world.removeEntity(scheduler, this);

            tree.addEntity(world);
            ((animatedEntity)tree).scheduleActions(scheduler, world, imageStore); //maybe issues???

            return true;
        }

        return false;
    }
    public Point getPosition()
    {
        return position;
    }
    public void setPosition(Point position)
    {
        this.position = position;
    }
    public String getID()
    {
        return id;
    }
    public int getImageIndex()
    {
        return imageIndex;
    }
    public void setImageIndex(int imageIndex)
    {
        this.imageIndex = imageIndex;
    }
    public List<PImage> getImages(){ return images;}
}
