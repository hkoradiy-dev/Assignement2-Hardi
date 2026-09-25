# Assignment #2 — Report

**Name:Hardi Koradiya**

**Student ID:801484363**

**Email:hkoradiy@charlotte.edu**

---

## Design

Which design did you choose (A, B, or your own)? Explain in your own words:

- What your **Mapper** emits as key and value, and why that is the right thing to emit.
- What your **Reducer** receives for one key, what it does with it, and where the Jaccard
  similarity is computed.
- What you had to set in the **Driver** beyond what L4's `Controller` set, and why.

I used Design A. The Mapper processes one document at a time. It takes
the first whitespace-delimited token as the document ID and treats the
remaining text as the document content. It converts the text to lower
case, splits it on whitespace, removes every character except a-z and
0-9, removes empty tokens, and stores the remaining words in a Set so
duplicate words are counted only once.

The Mapper emits the document ID as the key and the document's distinct
words as a space-separated Text value.

The Driver uses exactly one reducer because Design A requires every
document to reach the same reducer. The Reducer stores each document's
word set in a TreeMap during reduce(). After all documents have been
received, cleanup() compares every unique pair of documents.

For each pair, the Reducer creates an intersection of the two word
sets and a union of the two word sets. The Jaccard similarity is then
calculated as the intersection size divided by the union size. Pairs
with an empty intersection are not written. The result is formatted
with Locale.US to two decimal places.

In the Driver, I set the Mapper and Reducer classes, set the number of
reducers to one, configure Text/Text as the output types, set the
output separator to a single space, and set the JAR class with
setJarByClass().



---

## How I ran it

The commands you used, in the order you used them. If you deviated from the steps in the
README, say where and why.

```bash
docker compose up -d

mvn clean package

docker cp target/DocumentSimilarity-0.0.1-SNAPSHOT.jar resourcemanager:/tmp/
docker cp shared-folder/input/data/small_dataset.txt resourcemanager:/tmp/
docker cp shared-folder/input/data/dataset.txt resourcemanager:/tmp/

docker exec -it resourcemanager bash

cd /tmp

hadoop fs -mkdir -p /input/data
hadoop fs -put ./small_dataset.txt /input/data
hadoop fs -put ./dataset.txt /input/data
hadoop fs -ls /input/data

hadoop jar /tmp/DocumentSimilarity-0.0.1-SNAPSHOT.jar \
  com.example.controller.DocumentSimilarityDriver \
  /input/data/small_dataset.txt \
  /output/small_dataset

hadoop fs -cat /output/small_dataset/*

hadoop jar /tmp/DocumentSimilarity-0.0.1-SNAPSHOT.jar \
  com.example.controller.DocumentSimilarityDriver \
  /input/data/dataset.txt \
  /output/dataset

hadoop fs -cat /output/dataset/*
hadoop fs -cat /output/dataset/* | wc -l

hdfs dfs -get /output /tmp/

exit

docker cp resourcemanager:/tmp/output/. shared-folder/output/


I did not deviate from the steps, I actually got the error for old Java version 
but i fixed that issue reloded the entire java language server workspace and it 
worked..  
```

---

## Output

### `small_dataset.txt` (3 lines)

```
bash-4.2$ hadoop fs -cat /output/small_dataset/*
Document1, Document2 Similarity: 0.18
Document1, Document3 Similarity: 0.20
Document2, Document3 Similarity: 0.10
```

### `dataset.txt` (66 lines)

```
                Bytes Read=3407
        File Output Format Counters 
                Bytes Written=1980
bash-4.2$ hadoop fs -cat /output/dataset/*
Doc01, Doc02 Similarity: 0.16
Doc01, Doc03 Similarity: 0.13
Doc01, Doc04 Similarity: 0.07
Doc01, Doc05 Similarity: 0.10
Doc01, Doc06 Similarity: 0.09
Doc01, Doc07 Similarity: 0.11
Doc01, Doc08 Similarity: 0.10
Doc01, Doc09 Similarity: 0.11
Doc01, Doc10 Similarity: 0.09
Doc01, Doc11 Similarity: 0.07
Doc01, Doc12 Similarity: 0.19
Doc02, Doc03 Similarity: 0.20
Doc02, Doc04 Similarity: 0.13
Doc02, Doc05 Similarity: 0.10
Doc02, Doc06 Similarity: 0.09
Doc02, Doc07 Similarity: 0.06
Doc02, Doc08 Similarity: 0.09
Doc02, Doc09 Similarity: 0.05
Doc02, Doc10 Similarity: 0.10
Doc02, Doc11 Similarity: 0.06
Doc02, Doc12 Similarity: 0.14
Doc03, Doc04 Similarity: 0.17
Doc03, Doc05 Similarity: 0.11
Doc03, Doc06 Similarity: 0.08
Doc03, Doc07 Similarity: 0.16
Doc03, Doc08 Similarity: 0.11
Doc03, Doc09 Similarity: 0.07
Doc03, Doc10 Similarity: 0.10
Doc03, Doc11 Similarity: 0.12
Doc03, Doc12 Similarity: 0.11
Doc04, Doc05 Similarity: 0.09
Doc04, Doc06 Similarity: 0.11
Doc04, Doc07 Similarity: 0.18
Doc04, Doc08 Similarity: 0.09
Doc04, Doc09 Similarity: 0.08
Doc04, Doc10 Similarity: 0.10
Doc04, Doc11 Similarity: 0.09
Doc04, Doc12 Similarity: 0.09
Doc05, Doc06 Similarity: 0.20
Doc05, Doc07 Similarity: 0.14
Doc05, Doc08 Similarity: 0.15
Doc05, Doc09 Similarity: 0.07
Doc05, Doc10 Similarity: 0.13
Doc05, Doc11 Similarity: 0.14
Doc05, Doc12 Similarity: 0.11
Doc06, Doc07 Similarity: 0.17
Doc06, Doc08 Similarity: 0.15
Doc06, Doc09 Similarity: 0.08
Doc06, Doc10 Similarity: 0.10
Doc06, Doc11 Similarity: 0.12
Doc06, Doc12 Similarity: 0.13
Doc07, Doc08 Similarity: 0.15
Doc07, Doc09 Similarity: 0.07
Doc07, Doc10 Similarity: 0.08
Doc07, Doc11 Similarity: 0.12
Doc07, Doc12 Similarity: 0.11
Doc08, Doc09 Similarity: 0.19
Doc08, Doc10 Similarity: 0.13
Doc08, Doc11 Similarity: 0.22
Doc08, Doc12 Similarity: 0.12
Doc09, Doc10 Similarity: 0.13
Doc09, Doc11 Similarity: 0.12
Doc09, Doc12 Similarity: 0.13
Doc10, Doc11 Similarity: 0.12
Doc10, Doc12 Similarity: 0.12
Doc11, Doc12 Similarity: 0.11
bash-4.2$ hadoop fs -cat /output/dataset/* | wc -l
66
bash-4.2$ hadoop fs -cat /output/dataset/* | sort
Doc01, Doc02 Similarity: 0.16
Doc01, Doc03 Similarity: 0.13
Doc01, Doc04 Similarity: 0.07
Doc01, Doc05 Similarity: 0.10
Doc01, Doc06 Similarity: 0.09
Doc01, Doc07 Similarity: 0.11
Doc01, Doc08 Similarity: 0.10
Doc01, Doc09 Similarity: 0.11
Doc01, Doc10 Similarity: 0.09
Doc01, Doc11 Similarity: 0.07
Doc01, Doc12 Similarity: 0.19
Doc02, Doc03 Similarity: 0.20
Doc02, Doc04 Similarity: 0.13
Doc02, Doc05 Similarity: 0.10
Doc02, Doc06 Similarity: 0.09
Doc02, Doc07 Similarity: 0.06
Doc02, Doc08 Similarity: 0.09
Doc02, Doc09 Similarity: 0.05
Doc02, Doc10 Similarity: 0.10
Doc02, Doc11 Similarity: 0.06
Doc02, Doc12 Similarity: 0.14
Doc03, Doc04 Similarity: 0.17
Doc03, Doc05 Similarity: 0.11
Doc03, Doc06 Similarity: 0.08
Doc03, Doc07 Similarity: 0.16
Doc03, Doc08 Similarity: 0.11
Doc03, Doc09 Similarity: 0.07
Doc03, Doc10 Similarity: 0.10
Doc03, Doc11 Similarity: 0.12
Doc03, Doc12 Similarity: 0.11
Doc04, Doc05 Similarity: 0.09
Doc04, Doc06 Similarity: 0.11
Doc04, Doc07 Similarity: 0.18
Doc04, Doc08 Similarity: 0.09
Doc04, Doc09 Similarity: 0.08
Doc04, Doc10 Similarity: 0.10
Doc04, Doc11 Similarity: 0.09
Doc04, Doc12 Similarity: 0.09
Doc05, Doc06 Similarity: 0.20
Doc05, Doc07 Similarity: 0.14
Doc05, Doc08 Similarity: 0.15
Doc05, Doc09 Similarity: 0.07
Doc05, Doc10 Similarity: 0.13
Doc05, Doc11 Similarity: 0.14
Doc05, Doc12 Similarity: 0.11
Doc06, Doc07 Similarity: 0.17
Doc06, Doc08 Similarity: 0.15
Doc06, Doc09 Similarity: 0.08
Doc06, Doc10 Similarity: 0.10
Doc06, Doc11 Similarity: 0.12
Doc06, Doc12 Similarity: 0.13
Doc07, Doc08 Similarity: 0.15
Doc07, Doc09 Similarity: 0.07
Doc07, Doc10 Similarity: 0.08
Doc07, Doc11 Similarity: 0.12
Doc07, Doc12 Similarity: 0.11
Doc08, Doc09 Similarity: 0.19
Doc08, Doc10 Similarity: 0.13
Doc08, Doc11 Similarity: 0.22
Doc08, Doc12 Similarity: 0.12
Doc09, Doc10 Similarity: 0.13
Doc09, Doc11 Similarity: 0.12
Doc09, Doc12 Similarity: 0.13
Doc10, Doc11 Similarity: 0.12
Doc10, Doc12 Similarity: 0.12
Doc11, Doc12 Similarity: 0.11
bash-4.2$ hdfs dfs -get /output /tmp/
bash-4.2$ exit
exit
```

---

## Analysis

Look at the results for `dataset.txt`.

- Which pairs are the most similar, and which the least?
        The results for dataset.txt show that the similarity scores are generally low and relatively close to one another. The most similar pairs are the document pairs with the highest cosine-similarity values in the output, while the least similar pairs are the ones with the lowest values.


- Do the most similar pairs make sense given what the documents are about?
        The higher-scoring pairs make sense when the documents discuss related topics because they share more words after tokenization. However, the similarity values are still fairly low and close together. This happens because the current tokenization treats many common words as tokens. Words such as “the,” “is,” “and,” “of,” and similar stop words can appear in many documents and add noise to the similarity calculation without providing much information about the actual topic.


- The values are all fairly low and close together. Why? What one change to the tokenization
  rules would make the numbers more meaningful?
        One change that would make the similarity values more meaningful would be to remove stop words during tokenization. This would make the calculation focus more on meaningful content words, such as topic-specific nouns and verbs, instead of common English words. As a result, documents discussing similar topics would be more likely to have noticeably higher similarity scores.



---

## Scalability

**If you used Design A:** it relies on a single reducer that holds every document in memory.
What concretely breaks when the collection has a million documents? Sketch how Design B
avoids the problem.

Design A does not scale well to a very large document collection
because all documents are sent to one reducer. That reducer stores
every document's word set in memory before it can perform the pairwise
comparisons.

With one million documents, the reducer would need to keep the word
sets for all one million documents in memory. This could exceed the
reducer's available memory and cause the task to fail. Even if enough
memory were available, one reducer becomes a processing bottleneck
because all pairwise comparisons are performed there.

Design B avoids the single-document-storage bottleneck by emitting
each word with the document ID that contains it. The reducer for a
word can generate document pairs that share that word, allowing the
work to be distributed across multiple reducers. The shared-word
counts can then be combined to obtain the intersection size for each
document pair, with document sizes also carried through or computed
in another pass to calculate Jaccard similarity.

**If you used Design B:** why did it need more than one pass (or how did you avoid that)?
What is its own bottleneck?



---

## Problems and fixes

Anything that went wrong and what resolved it. Paste the actual error message. If nothing
went wrong, say so.

While completing the assignment, I encountered a Maven/project configuration issue. Initially, Maven was run from the outer Assignment2 directory, which did not contain the pom.xml file. Maven produced the following error:

The goal you specified requires a project to execute but there is no POM in this directory
(/Users/hardikoradiya/Documents/CloudComputing/Assignment2).
Please verify you invoked Maven from the correct directory.


## Fix ##

- I located the actual project directory containing pom.xml and changed into the Assignement2-Hardi directory. I then ran:

mvn clean compile

The project compiled successfully with:

Compiling 3 source files with javac [debug release 8]
BUILD SUCCESS

I also verified that the required Hadoop dependencies were available through Maven, including:

org.apache.hadoop:hadoop-common:3.3.6
org.apache.hadoop:hadoop-mapreduce-client-core:3.3.6

The IDE initially continued showing Hadoop import errors even though Maven compilation succeeded. This was related to the IDE's Java/Maven dependency indexing rather than an actual compilation failure.

---

## Use of generative AI

If you used a generative AI tool, include the acknowledgment statement from the syllabus and
say specifically what you used it for. If you did not use one, say so.


I used a generative AI tool during this assignment as an AI agent to help troubleshoot problems, understand and resolve project configuration and dependency issues, and assist with the implementation and testing process. I also used generative AI to help organize and write the REPORT.md file, including structuring the analysis, problems and fixes, and documentation sections.


