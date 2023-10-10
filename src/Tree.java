import processing.core.PImage;

import java.util.List;
public final class Tree implements Plant{
    public static final String STUMP_KEY = "stump";
    public String id;
    public Point position;
    public List<PImage> images;

    public double actionPeriod;
    public double animationPeriod;
    public int health;
    public int imageIndex;



    public Tree(String id, Point position, double actionPeriod, double animationPeriod, int health,List<PImage> images) {
        this.id = id;
        this.position = position;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.images = images;
        this.health = health;
        this.imageIndex = 0;

    }


    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.getImageIndex());
    }

    public void addEntity(WorldModel world) {
        if (world.withinBounds(this.position)) {
            world.setOccupancyCell(this,this.position);
            world.entities.add(this);
        }
    }
    public double getAnimationPeriod() {

        return this.animationPeriod;

    }
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {

        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());

    }
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {

        if (!this.transformPlant(world, scheduler, imageStore)) {

            scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.actionPeriod);
        }
    }

    private boolean transformTree(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.health <= 0) {
            Entity stump = new Stump(STUMP_KEY + "_" + this.id, this.position, imageStore.getImageList( STUMP_KEY));

            world.removeEntity(scheduler, this);

            stump.addEntity(world);

            return true;
        }

        return false;
    }
    public boolean transformPlant(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {

            return this.transformTree(world, scheduler, imageStore);

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
