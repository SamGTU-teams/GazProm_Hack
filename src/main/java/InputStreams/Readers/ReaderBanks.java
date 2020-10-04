package InputStreams.Readers;

import Data.BankData;
import InputStreams.StreamData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ReaderBanks extends StreamData<BankData> {

    public static int startPage = 1;
    public static int endPage = 57;

    private String[] paths;

    public ReaderBanks(String... path) {
        super();
        this.paths = path;
    }

    @Override
    protected Stream<BankData> generateStream() {
        return getList().stream();
    }

    public List<BankData> getList() {
        Type bankType = new TypeToken<List<BankData>>() {
        }.getType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        List<BankData> cashMachines = new ArrayList<>();

        for (String path : paths) {
            for (int i = startPage; i <= endPage; i++) {
                cashMachines.addAll(gson.fromJson(BankData.getReader(path + i), bankType));
            }
        }
        return cashMachines;
    }
}
