package com.ac.kr.academy.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

/**
   LogUtils : 공용 기능 (파일읽기) -> static => 주입 필요X
 */

public class LogUtils {

    private static final String LOG_FILE_PATH = "./logs/app.log";

//WARN, ERROR 만 출력
    public static List<String> tail(int lines) throws IOException {
        List<String> all = Files.readAllLines(Paths.get(LOG_FILE_PATH));

        return all.stream()
                .skip(Math.max(0, all.size() - lines))
                .filter(line -> line.contains("ERROR") || line.contains("WARN"))
                .collect(Collectors.toList());
    }
}
