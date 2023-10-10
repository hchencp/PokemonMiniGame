import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
public final class Dude_full implements active,moving{
        public String id;
        public Point position;
        public double actionPeriod;
        public double animationPeriod;
        public int resourceLimit;
        public List<PImage> images;
        public int imageIndex;


        public Dude_full(String id, Point position, double actionPeriod, double animationPeriod, int resourceLimit,List<PImage> images) {
            this.id = id;
            this.position = position;
            this.actionPeriod = actionPeriod;
            this.animationPeriod = animationPeriod;
            this.resourceLimit = resourceLimit;
            this.images = images;
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



        public boolean moveTo(WorldModel world, Entity target, EventScheduler scheduler) {
            if (adjacent(this.position, target.getPosition())) {
                return true;
            } else {
                Point nextPos = this.nextPosition(world, target.getPosition());

                if (!this.position.equals(nextPos)) {
                    world.moveEntity(scheduler, this, nextPos);
                }
                return false;
            }
        }
        private void transformFull( WorldModel world, EventScheduler scheduler, ImageStore imageStore) {
            Entity dude = new Dude_not_full(this.id, this.position,this.images, this.resourceLimit,this.actionPeriod, this.animationPeriod);

            world.removeEntity(scheduler, this);

            dude.addEntity(world);
            ((animatedEntity)dude).scheduleActions(scheduler,world, imageStore);
        }


        public void executeActivity(WorldModel world, ImageStore imageStore, EventScheduler scheduler) {
            Optional<Entity> fullTarget = world.findNearest( this.position, new ArrayList<>(List.of(House.class)));

            if (fullTarget.isPresent() && this.moveTo(world, fullTarget.get(), scheduler)) {
                this.transformFull(world, scheduler, imageStore);
            } else {
                scheduler.scheduleEvent( this, new Activity(this, world, imageStore), this.actionPeriod);
            }
        }

        public double getAnimationPeriod() {

                    return this.animationPeriod;
        }
        public void scheduleActions(EventScheduler scheduler, WorldModel world, ImageStore imageStore) {
                    scheduler.scheduleEvent(this, new Activity(this, world, imageStore), this.actionPeriod);
                    scheduler.scheduleEvent(this, new Animation(this, 0), this.getAnimationPeriod());

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
        public static boolean adjacent(Point p1, Point p2) {
            return (p1.x == p2.x && Math.abs(p1.y - p2.y) == 1) || (p1.y == p2.y && Math.abs(p1.x - p2.x) == 1);
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

