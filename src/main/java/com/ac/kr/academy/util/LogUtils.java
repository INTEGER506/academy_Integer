package com.ac.kr.academy.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
   LogUtils : 공용 기능 (파일읽기) -> static => 주입 필요X

 * [로그 모니터링]
 * 로그 파일에서 마지막 N줄을 읽고, filter 키워드가 있으면 필터링
 */

public class LogUtils {

    private static final String LOG_FILE_PATH = "./logs/app.log";

    public static List<String> tail(int lines, String filter){
        List<String> result = new ArrayList<>();

        Path path = Paths.get(LOG_FILE_PATH);
        if(!Files.exists(path)){
            result.add("로그 파일이 존재하지 않습니다: " + LOG_FILE_PATH);
            return result;
        }
        try{
            List<String> allLines = Files.readAllLines(path);   //전체 라인 다 읽기
            int start = Math.max(allLines.size() - lines, 0);   //뒤에서부터 N줄 가져오기
            List<String> recentLines = allLines.subList(start, allLines.size());

            //필터가 없거나 비어있거나 있으면 해당 키워드 포함된 line만 출력
            for(String line : recentLines){
                if(filter == null || filter.isEmpty() || line.contains(filter)){
                    result.add(line);
                }
            }
        } catch (IOException e){
            result.add("로그 읽기 실패" + e.getMessage());
        }
        if(result.isEmpty()){
            result.add(("최근" ) + lines + "줄에서 '" + filter + "' 로그가 없습니다.");
        }
        return result;
    }
}
