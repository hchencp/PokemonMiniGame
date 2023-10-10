import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Chansey implements active,moving{
    public static final String DUDE_KEY = "dude";
    public String id;
    public Point position;
    public double actionPeriod;
    public double animationPeriod;
    public int imageIndex;
    public List<PImage> images;


    public Chansey(String id, Point position, double actionPeriod, double animationPeriod, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.actionPeriod = actionPeriod;
        this.animationPeriod = animationPeriod;
        this.images = images;
        this.imageIndex = 0;
    }


    public String log(){
        return this.id.isEmpty() ? null :
                String.format("%s %d %d %d", this.id, this.position.x, this.position.y, this.imageIndex);
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

    public void addEntity(WorldModel world) {
        if (world.withinBounds(this.position)) {
            world.setOccupancyCell(this,this.position);
            world.entities.add(this);
        }
    }
    public double getAnimationPeriod() {
        return this.animationPeriod;
    }
    public void scheduleActions(EventScheduler scheduler,WorldModel world, ImageStore imageStore) {
        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.actionPeriod);
        scheduler.scheduleEvent( this, new Animation(this, 0), this.getAnimationPeriod());

    }
    public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
        Optional<Entity> chanseyTarget = world.findNearest(this.position, new ArrayList<>(List.of(Ashes.class)));
        if (chanseyTarget.isPresent()) {
            Point tgtPos = chanseyTarget.get().getPosition();

            if (this.moveTo(world, chanseyTarget.get(), scheduler)) {

                active Dude = new Dude_not_full("Dude" + chanseyTarget.get().getID(), tgtPos, imageStore.getImageList( DUDE_KEY),2,1,1);
                Dude.addEntity(world);
                Dude.scheduleActions(scheduler,world, imageStore);
            }
        }

        scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.actionPeriod);
    }
    public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
        if (adjacent(this.position, target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = this.nextPosition(world, target.getPosition());

            if (!this.position.equals(nextPos)) {
                world.moveEntity(scheduler,this, nextPos);
            }
            return false;
        }
    }
    public Point nextPosition(WorldModel world, Point destPos) {
        PathingStrategy strategy = new AStarPathingStrategy();
        List<Point> points = strategy.computePath(this.position, destPos, p -> world.withinBounds(p) && (!(world.isOccupied(p))), (p1, p2) -> adjacent(p1, p2), PathingStrategy.CARDINAL_NEIGHBORS);
        if (points.size() == 0) {
            return this.position;
        }
        return points.get(0);
    }
    public boolean adjacent(Point p1, Point p2) {
        return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
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
