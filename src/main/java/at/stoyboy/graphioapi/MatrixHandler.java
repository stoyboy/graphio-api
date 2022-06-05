package at.stoyboy.graphioapi;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

@RestController
public class MatrixHandler {

    @CrossOrigin
    @PostMapping("/calculate")
    public HashMap<String, Object> matrix(@RequestParam("file") MultipartFile file,
                                          RedirectAttributes redirectAttributes) {
        try {
            System.out.println(file.getContentType());
            if (file.getContentType().equals("text/csv") || file.getContentType().equals("application/vnd.ms-excel")) {
                String fileContent = new String(file.getBytes());

                HashMap<String, Object> m = new HashMap<>();

                AdjacencyMatrix adjacencyMatrix = new AdjacencyMatrix(toMatrix(fileContent));
                DistanceMatrix distanceMatrix = new DistanceMatrix(adjacencyMatrix);
                Eccentricity eccentricity = new Eccentricity(distanceMatrix);
                PathMatrix pathMatrix = new PathMatrix(adjacencyMatrix);
                Components components = new Components(pathMatrix);
                Articulation articulation = new Articulation(adjacencyMatrix, components.getComponents().size());
                Bridges bridges = new Bridges(adjacencyMatrix, components.getComponents().size());

                m.put("adjacencyMatrix", adjacencyMatrix.getMatrix());
                m.put("distanceMatrix", distanceMatrix.getMatrix());
                m.put("eccentricity", eccentricity.eccentricityToString());
                m.put("radius", eccentricity.radiusToString());
                m.put("diameter", eccentricity.diameterToString());
                m.put("center", eccentricity.centerToString());
                m.put("pathMatrix", pathMatrix.getMatrix());
                m.put("components", components.toStringArray());
                m.put("articulation", articulation.toString());
                m.put("bridges", bridges.toStringArray());

                return m;
            }
            else {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "transmitted file is not a csv file"
                );
            }
        } catch (IOException | GraphIOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Integer[][] toMatrix(String fileContent) {
        try (
                BufferedReader br = new BufferedReader(new StringReader(fileContent))
        )
        {
            String line = br.readLine();
            String separator = ";";
            int lineCount = 0;

            ArrayList<String> lines = new ArrayList<>();

            while (line != null) {
                lines.add(line);
                lineCount++;
                line = br.readLine();
            }

            if (lineCount > 0) {
                //Erstellt ein neues Array anhand der Anzahl der Zeilen der CSV-Datei.
                String[][] stringMatrix = new String[lineCount][lineCount];
                Integer[][] matrix = new Integer[lineCount][lineCount];

                for (int i = 0; i < lines.size(); i++) {
                    String[] items = lines.get(i).split(separator);
                    if (items.length >= 0)
                        System.arraycopy(items, 0, stringMatrix[i], 0, items.length);
                }

                for (int i = 0; i < stringMatrix.length; i++) {
                    for (int j = 0; j < stringMatrix[i].length; j++) {
                        matrix[i][j] = Integer.parseInt(stringMatrix[i][j]);
                    }
                }

                return matrix;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Integer[0][];
    }
}
