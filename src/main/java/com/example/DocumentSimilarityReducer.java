package com.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Reducer for the document similarity job.
 *
 * Input:  whatever your mapper emits, grouped by key by the shuffle/sort phase.
 *
 * Output: one line per pair of documents that share at least one word, in exactly this
 *         format (see README.md):
 *
 *             Doc01, Doc02 Similarity: 0.18
 *
 *         where the two IDs are in ascending String order (Doc01 before Doc02), and the
 *         Jaccard similarity  |A ∩ B| / |A ∪ B|  is printed with two decimals, e.g.
 *         String.format("%.2f", similarity). Note that "%.2f" uses the machine's locale;
 *         use  String.format(java.util.Locale.US, "%.2f", similarity)  to be safe.
 *
 * Hint: in the design suggested in README.md all documents reach a single reducer, one per
 *       reduce() call. You cannot compare documents until you have seen all of them, so
 *       reduce() only stores each document, and the pairwise comparison happens in
 *       cleanup(), which Hadoop calls once after the last reduce() call.
 */
public class DocumentSimilarityReducer
        extends Reducer<Text, Text, Text, Text> {

    private final Map<String, Set<String>> documents =
            new TreeMap<>();

    @Override
    protected void reduce(
            Text key,
            Iterable<Text> values,
            Context context)
            throws IOException, InterruptedException {

        Set<String> words = new HashSet<>();

        for (Text value : values) {

            String wordList = value.toString().trim();

            if (wordList.isEmpty()) {
                continue;
            }

            for (String word : wordList.split("\\s+")) {

                if (!word.isEmpty()) {
                    words.add(word);
                }
            }
        }

        documents.put(key.toString(), words);
    }

    @Override
    protected void cleanup(Context context)
            throws IOException, InterruptedException {

        List<Map.Entry<String, Set<String>>> entries =
                new ArrayList<>(documents.entrySet());

        // Compare every unique pair of documents.
        for (int i = 0; i < entries.size(); i++) {

            String docA = entries.get(i).getKey();
            Set<String> wordsA = entries.get(i).getValue();

            for (int j = i + 1; j < entries.size(); j++) {

                String docB = entries.get(j).getKey();
                Set<String> wordsB = entries.get(j).getValue();

                // Calculate intersection.
                Set<String> intersection =
                        new HashSet<>(wordsA);

                intersection.retainAll(wordsB);

                // Do not output pairs with no shared words.
                if (intersection.isEmpty()) {
                    continue;
                }

                // Calculate union.
                Set<String> union =
                        new HashSet<>(wordsA);

                union.addAll(wordsB);

                double similarity =
                        (double) intersection.size()
                        / union.size();

                String formatted =
                        String.format(
                                Locale.US,
                                "%.2f",
                                similarity
                        );

                context.write(
                        new Text(docA + ", " + docB),
                        new Text("Similarity: " + formatted)
                );
            }
        }
    }
}