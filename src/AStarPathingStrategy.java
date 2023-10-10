import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class AStarPathingStrategy implements PathingStrategy {

    class Node {
        private int g; //g is the distance from the start to the current position.
        private int h; //h is the distance from the current position to the goal
        private int f; //total distance f=g+h
        private Node prev_node; // prior node;
        private Point position;

        public Node(int g, int h, int f, Point position, Node prev_node) {
            this.g = g;
            this.h = h;
            this.f = f;
            this.prev_node = prev_node;
            this.position = position;

        }


        public int getF() {
            return f;
        }

        public int getG() {
            return g;
        }

        public Point getPosition() {
            return position;
        }


        public boolean equals(Object other) {
            if (other == null) return false;
            if (getClass() != other.getClass()) return false;
            Node p = (Node) other;
            return position.equals(p.position);
        }








    }


    public List<Point> computePath(Point start, Point end,
                                   Predicate<Point> canPassThrough,
                                   BiPredicate<Point, Point> withinReach,
                                   Function<Point, Stream<Point>> potentialNeighbors) {
        List<Point> finishedPath = new ArrayList<>();
        Map<Point, Node> closedSet = new HashMap<Point, Node>();
        Map<Point, Node> openMap = new HashMap<Point, Node>();
        Queue<Node> openSet = new PriorityQueue<Node>(Comparator.comparingInt(Node::getF)); //returns node with lowest f val

        Node startPoint = new Node(0, Heuristic(start, end), 0 + Heuristic(start, end), start, null);
        openSet.add(startPoint);
        Node current = null;

        while (!openSet.isEmpty()) {
            current = openSet.remove();

            if (withinReach.test(current.getPosition(), end)) {
                while (current.getPosition() != start) {
                    finishedPath.add(0,current.getPosition());
                    current = current.prev_node;
                }
                break;
            }
            List<Point> neighbors = potentialNeighbors.apply(current.getPosition())
                    .filter(canPassThrough)
                    .filter(p -> !p.equals(start) && !p.equals(end) && !closedSet.containsKey(p)).collect(Collectors.toList());

            for (Point neighbor : neighbors) {
                if (!closedSet.containsKey(neighbor)) {
                    int neighbor_g = current.getG() + 1;
                    Node neighborNode = new Node(neighbor_g, Heuristic(neighbor, end), Heuristic(neighbor, end) + neighbor_g, neighbor, current);

                    if (!openSet.contains(neighborNode)) {
                       openSet.add(neighborNode);
                        openMap.put(neighbor, neighborNode);
                    }
                    else if (neighbor_g < openMap.get(neighbor).getG()) {
                        openMap.put(neighbor, neighborNode);
                    }
                }
            }
                closedSet.put(current.getPosition(), current);
            }
            return finishedPath;
        }



        public int Heuristic (Point current, Point goal){
            return Math.abs(goal.x - current.x) + Math.abs(goal.y - current.y);
        }
    }








