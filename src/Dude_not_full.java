import processing.core.PImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
public final class Dude_not_full implements active,moving{
    public String id;
    public Point position;
    public List<PImage> images;
    public int resourceLimit;
    public int resourceCount;
    public double actionPeriod;
    public double animationPeriod;
    public int imageIndex;



    public Dude_not_full(String id, Point position, List<PImage> images, int resourceLimit, double actionPeriod, double animationPeriod) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.resourceLimit = resourceLimit;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.imageIndex = 0;



    }

    /**
     * Helper method for testing. Preserve this functionality while refactoring.
     */
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strategy = new AStarPathingStrategy();
        List<Point> points = strategy.computePath(this.position, destPos, p -> world.withinBounds(p) && (!(world.isOccupied(p)) || world.getOccupancyCell(p) instanceof Stump), (p1, p2) -> adjacent(p1, p2), PathingStrategy.CARDINAL_NEIGHBORS);
        if (points.size() == 0) {
            return this.position;
        }
        return points.get(0);
    }



    private boolean transformNotFull(WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
        if (this.resourceCount >= this.resourceLimit) {
            animatedEntity dude = new Dude_full(this.id, this.position, this.actionPeriod, this.animationPeriod, this.resourceLimit, this.images);

            world.removeEntity(scheduler, this);
            scheduler.unscheduleAllEvents(this);

            dude.addEntity(world);
            dude.scheduleActions(scheduler, world, imageStore);

            return true;
        }

        return false;
    }


    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> target = world.findNearest(this.position, new ArrayList<>(Arrays.asList(Tree.class, Sapling.class)));
        if (target.isEmpty() || !moveTo(world, target.get(), scheduler) || !this.transformNotFull(world, scheduler, imageStore)) {
            scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.actionPeriod);
        }
    }
    public static boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
    }


    public double getAnimationPeriod() {

                return this.animationPeriod;

    }
    public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {

                scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.actionPeriod);
                scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());

    }
    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
    }

    public void addEntity(WorldModel world) {
        if (world.withinBounds(this.position)) {
            world.setOccupancyCell(this,this.position);
            world.entities.add(this);
        }
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
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (adjacent(this.position, target.getPosition())) {
            this.resourceCount += 1;
            ((Tree)target).health--;
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler,this, nextPos);
            }
            return false;
        }
    }
    public List<PImage> getImages(){ return images;}

}
