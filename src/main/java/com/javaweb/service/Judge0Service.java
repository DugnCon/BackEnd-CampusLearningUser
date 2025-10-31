package com.javaweb.service;

import com.javaweb.model.dto.TestCasesDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class Judge0Service {

    private final WebClient webClient;

    public Judge0Service() {
        this.webClient = WebClient.builder()
                .baseUrl("https://judge0-ce.p.rapidapi.com") // Judge0 CE trên RapidAPI
                .defaultHeader("x-rapidapi-key", "84ec9f3030mshb3f5702dbc0bebep1ce0b0jsn7098b8781f69")
                .defaultHeader("x-rapidapi-host", "judge0-ce.p.rapidapi.com")
                .build();
    }

    public List<Map<String, Object>> submitMultipleTestCases(String sourceCode, int languageId, List<TestCasesDTO> testCases) {
        List<Map<String, Object>> results = new ArrayList<>();

        for (TestCasesDTO test : testCases) {
            String input = test.getInput();
            String expected = test.getOutput();

            Map<String, Object> body = new HashMap<>();
            body.put("language_id", 54); // 54 là C++
            body.put("source_code", sourceCode);
            body.put("stdin", input != null ? input : "");
            body.put("expected_output", expected != null ? expected : "");

            try {
                Map<String, Object> resp = webClient.post()
                        .uri("/submissions?base64_encoded=false&wait=true")
                        .bodyValue(body)
                        .retrieve()
                        .onStatus(status -> !status.is2xxSuccessful(), clientResponse ->
                                clientResponse.bodyToMono(String.class)
                                        .map(bodyStr -> new RuntimeException(
                                                "HTTP error: " + clientResponse.statusCode() + ", body: " + bodyStr)))
                        .bodyToMono(Map.class)
                        .block();

                String stdout = resp.get("stdout") != null ? resp.get("stdout").toString() : "";
                String stderr = resp.get("stderr") != null ? resp.get("stderr").toString() : "";
                String compileOutput = resp.get("compile_output") != null ? resp.get("compile_output").toString() : "";
                String time = resp.get("time") != null ? resp.get("time").toString() : "0";
                String status = "Unknown";

                if (resp.get("status") instanceof Map) {
                    Map statusMap = (Map) resp.get("status");
                    status = statusMap.get("description") != null ? statusMap.get("description").toString() : "Unknown";
                }

                Map<String, Object> r = Map.of(
                        "input", input,
                        "expected_output", expected,
                        "stdout", stdout,
                        "stderr", stderr,
                        "compile_output", compileOutput,
                        "status", status,
                        "time", time
                );

                results.add(r);
            } catch (Exception e) {
                System.err.println("Error submitting code for input: " + input);
                e.printStackTrace();

                Map<String, Object> r = Map.of(
                        "input", input,
                        "expected_output", expected,
                        "stdout", "",
                        "stderr", "",
                        "compile_output", "",
                        "status", "Internal Error: " + e.getMessage(),
                        "time", 0
                );
                results.add(r);
            }
        }

        return results;
    }
}
