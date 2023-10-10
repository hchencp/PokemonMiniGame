public interface Action {
    void executeAction(EventScheduler scheduler);
}

















//public final class Action {
//    public ActionKind kind;
//    public Entity entity;
//    public WorldModel world;
//    public ImageStore imageStore;
//    public int repeatCount;
//
//    public Action(ActionKind kind, Entity entity, WorldModel world, ImageStore imageStore, int repeatCount) {
//        this.kind = kind;
//        this.entity = entity;
//        this.world = world;
//        this.imageStore = imageStore;
//        this.repeatCount = repeatCount;
//    }
//
//
//    private void executeActivityAction(EventScheduler eventScheduler) {
//        switch (entity.kind) {
//            case SAPLING:
//                entity.executeSaplingActivity(world, imageStore, eventScheduler);
//                break;
//            case TREE:
//                entity.executeTreeActivity(world, imageStore, eventScheduler);
//                break;
//            case FAIRY:
//                entity.executeFairyActivity(world, imageStore, eventScheduler);
//                break;
//            case DUDE_NOT_FULL:
//                entity.executeDudeNotFullActivity(world, imageStore, eventScheduler);
//                break;
//            case DUDE_FULL:
//                entity.executeDudeFullActivity(world, imageStore, eventScheduler);
//                break;
//            default:
//                throw new UnsupportedOperationException(String.format("executeActivityAction not supported for %s", entity.kind));
//        }
//    }
//
//    public void executeAction(EventScheduler eventScheduler) {
//        switch (kind) {
//            case ACTIVITY:
//                executeActivityAction(eventScheduler);
//                break;
//
//            case ANIMATION:
//                eventScheduler.executeAnimationAction(this);
//                break;
//        }
//    }
//
//    public static Action createAnimationAction(Entity entity, int repeatCount) {
//        return new Action(ActionKind.ANIMATION, entity, null, null, repeatCount);
//    }
////    public static Action createActivityAction(Entity entity, WorldModel world, ImageStore imageStore) {
////        return new Action(ActionKind.ACTIVITY, entity, world, imageStore, 0);
////    }
//    public void nextImage(Entity entity) {
//        entity.imageIndex = entity.imageIndex + 1;
//    }
//}
