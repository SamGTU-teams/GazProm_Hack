package DataStreams.Readers;

import Data.Building;
import Data.Location;
import DataStreams.DataStream;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ReaderBuildings implements DataStream<Building> {

    private static final Logger LOG = Logger.getLogger(ReaderBuildings.class.getName());

    private String[] paths;

    public ReaderBuildings(String... path) {
        LOG.info("Init " + getClass().getSimpleName() + '\n');
        this.paths = path;
    }

    @Override
    public Stream<Building> generateStream() {
        return getList().stream();
    }

    public List<Building> getList() {
        List<Building> list = new LinkedList<>();
        for (String path : paths) {
            Path filePath = Path.of(path);
            Gson gson = new GsonBuilder().
                    registerTypeAdapter(Location.class, Building.geoDeser).
                    create();

            try (BufferedReader reader = Files.newBufferedReader(filePath)) {
                JsonArray jsonArray = gson.fromJson(reader, JsonArray.class);
                for (JsonElement j : jsonArray) {
                    Building building = gson.fromJson(j, Building.class);
                    building.setType(filePath.getName(filePath.getNameCount() - 1).toString().replaceAll("(?iU)(.+?)\\.(.+)", "$1"));
                    building.setPriority(Building.PRIORITY_MAP.getOrDefault(building.getType(), (short) -1));
                    list.add(building);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
