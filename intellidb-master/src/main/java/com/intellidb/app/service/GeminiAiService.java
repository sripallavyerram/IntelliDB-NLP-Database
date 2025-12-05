package com.intellidb.app.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateSql(String schema, String question) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=" + apiKey;

        // Sanitize schema and question to be safely embedded in JSON
        String sanitizedSchema = schema.replace("\"", "\\\"").replace("\n", "\\n");
        String sanitizedQuestion = question.replace("\"", "\\\"");

        String prompt = "Given the following SQL schema definition:\\n" + sanitizedSchema + "\\nWrite a single, executable SQL SELECT query to answer the following question. Do not explain the query, only provide the raw SQL code. Question: " + sanitizedQuestion;

        String requestBody = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + prompt + "\"}]}]}";

        try {
            String response = restTemplate.postForObject(url, requestBody, String.class);
            return response.split("\"text\": \"")[1].split("\"")[0]
                    .replace("```sql", "")
                    .replace("```", "")
                    .replace("\\n", " ")
                    .trim();
        } catch (Exception e) {
            System.err.println("Error calling Gemini API: " + e.getMessage());
            return "Error generating SQL";
        }
    }
}