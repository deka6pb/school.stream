package school.other;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Example2 {
    public static void main(String[] args) {
        // Стримы помогут
        Map<String, String> argsMap = new LinkedHashMap<>();
        argsMap.put("-i", "in.txt");
        argsMap.put("--limit", "40");
        argsMap.put("-d", "1");
        argsMap.put("-o", "out.txt");

        String[] argList = argsMap.entrySet().stream()
                .flatMap(e -> Stream.of(e.getKey(), e.getValue()))
                .toArray(String[]::new);
        System.out.println(String.join(" ", argList));
    }
}
