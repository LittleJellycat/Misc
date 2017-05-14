import javafx.util.Pair;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static java.lang.Math.*;

public class AngleByMesh {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.UK);
        int numberOfTriangles = sc.nextInt();
        int numberOfVertexes = sc.nextInt();
        int[][] triangleVertices = new int[numberOfTriangles][3];
        for (int i = 0; i < numberOfTriangles; i++) {
            triangleVertices[i][0] = sc.nextInt();
            triangleVertices[i][1] = sc.nextInt();
            triangleVertices[i][2] = sc.nextInt();
        }
        double[][] coordinates = new double[numberOfVertexes][3];
        for (int i = 0; i < numberOfVertexes; i++) {
            coordinates[i][0] = sc.nextDouble();
            coordinates[i][1] = sc.nextDouble();
            coordinates[i][2] = sc.nextDouble();
        }
        sc.close();
        Collection<List<Integer>> adjacentTriangles = findAdjacentTriangles(triangleVertices);
        System.out.println(findMaxAngle(adjacentTriangles, coordinates, triangleVertices));
    }

    @NotNull
    private static Collection<List<Integer>> findAdjacentTriangles(int[][] vertices) {
        HashMap<Pair<Integer, Integer>, List<Integer>> adjacencyMap = new HashMap<>();
        int[][] vertexIndices = new int[vertices.length][3];
        for (int i = 0; i < vertices.length; i++) {
            System.arraycopy(vertices[i], 0, vertexIndices[i], 0, 3);
            Arrays.sort(vertexIndices[i]);
        }

        for (int i = 0; i < vertexIndices.length; i++) {
            int[] indices = vertexIndices[i];
            compute(adjacencyMap, i, indices[0], indices[1]);
            compute(adjacencyMap, i, indices[1], indices[2]);
            compute(adjacencyMap, i, indices[0], indices[2]);
        }
        return adjacencyMap.values();
    }

    private static void compute(HashMap<Pair<Integer, Integer>, List<Integer>> adjacencyMap,
                                int triangle, int first, int second) {
        adjacencyMap.compute(new Pair<>(first, second),
                (k, v) -> {
                    if (v == null) {
                        v = new ArrayList<>();
                    }
                    v.add(triangle);
                    return v;
                });
    }

    private static double findMaxAngle(Collection<List<Integer>> adjacentTriangles, double[][] coordinates, int[][] vertexIndices) {
        return adjacentTriangles.stream().filter(list -> list.size() > 1)
                .mapToDouble(list -> findMaxAngle(list, coordinates, vertexIndices))
                .max().orElse(-1);
    }

    private static double findMaxAngle(List<Integer> list, double[][] coordinates, int[][] vertexIndices) {
        double max = -1;
        for (int i = 0; i < list.size() - 1; i++) {
            double[] firstPlaneCoefficients = findPlaneCoefficients(coordinates[vertexIndices[list.get(i)][0]],
                    coordinates[vertexIndices[list.get(i)][1]], coordinates[vertexIndices[list.get(i)][2]]);
            for (int j = i + 1; j < list.size(); j++) {
                double[] secondPlaneCoefficients = findPlaneCoefficients(coordinates[vertexIndices[list.get(j)][0]],
                        coordinates[vertexIndices[list.get(j)][1]], coordinates[vertexIndices[list.get(j)][2]]);
                double current = acos((firstPlaneCoefficients[0] * secondPlaneCoefficients[0] + firstPlaneCoefficients[1] * secondPlaneCoefficients[1]
                        + firstPlaneCoefficients[2] * secondPlaneCoefficients[2]) / (
                        (sqrt(pow(firstPlaneCoefficients[0], 2) + pow(firstPlaneCoefficients[1], 2) + pow(firstPlaneCoefficients[2], 2))
                                * sqrt(pow(secondPlaneCoefficients[0], 2) + pow(secondPlaneCoefficients[1], 2) + pow(secondPlaneCoefficients[2], 2)))));
                if (current > max) {
                    max = current;
                }

            }

        }
        return max;
    }

    @Contract(pure = true)
    private static double[] findPlaneCoefficients(double[] firstPointCoordinates, double[] secondPointCoordinates, double[] thirdPointCoordinates) {
        double[] planeCoefficients = new double[3];
        planeCoefficients[0] = firstPointCoordinates[1] * (secondPointCoordinates[2] - thirdPointCoordinates[2]) +
                secondPointCoordinates[1] * (thirdPointCoordinates[2] - firstPointCoordinates[2]) +
                thirdPointCoordinates[1] * (firstPointCoordinates[2] - secondPointCoordinates[2]);
        planeCoefficients[1] = firstPointCoordinates[2] * (secondPointCoordinates[0] - thirdPointCoordinates[0]) +
                secondPointCoordinates[2] * (thirdPointCoordinates[0] - firstPointCoordinates[0]) +
                thirdPointCoordinates[2] * (firstPointCoordinates[0] - secondPointCoordinates[0]);
        planeCoefficients[2] = firstPointCoordinates[0] * (secondPointCoordinates[1] - thirdPointCoordinates[1]) +
                secondPointCoordinates[0] * (thirdPointCoordinates[1] - firstPointCoordinates[1]) +
                thirdPointCoordinates[0] * (firstPointCoordinates[1] - secondPointCoordinates[1]);
        return planeCoefficients;
    }
}
