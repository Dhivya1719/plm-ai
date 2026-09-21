package com.plm.plm_ai.ai.rag;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentChunkingService {

    private static final int CHUNK_SIZE = 800;
    private static final int OVERLAP = 100;

    public List<String> chunkText(String text) {

        List<String> chunks =
                new ArrayList<>();

        if (text == null
                || text.isBlank()) {

            return chunks;
        }

        String cleaned =
                text.replaceAll(
                        "\\s+",
                        " "
                ).trim();

        int start = 0;

        while (start < cleaned.length()) {

            int end =
                    Math.min(
                            start + CHUNK_SIZE,
                            cleaned.length()
                    );

            String chunk =
                    cleaned.substring(
                            start,
                            end
                    ).trim();

            if (!chunk.isBlank()) {
                chunks.add(chunk);
            }

            if (end >= cleaned.length()) {
                break;
            }

            start =
                    end - OVERLAP;
        }

        return chunks;
    }
}