/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Author: Ken Ngai
 * Simple metrics to check count of term freq and doc freq
 */
package org.apache.lucene.demo;

import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

 import java.util.Date;

import org.apache.lucene.demo.CMPT456Analyzer;

import org.apache.lucene.analysis.Analyzer;
// import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class SimpleMetrics {
    private int doc_freq;
    private long term_freq;

    public SimpleMetrics(int doc_freqs, long term_freqs) {
        this.set_term_freq(term_freqs);
        this.set_doc_freq(doc_freqs);
    }

    public SimpleMetrics(IndexReader reader,Term term) {
        try {
            this.set_term_freq(reader.totalTermFreq(term));
            this.set_doc_freq(reader.docFreq(term));
            
        } catch (IOException e) {
            //TODO: handle exception
            e.printStackTrace();
        }
       
    }

    public int get_doc_freq(){
        return doc_freq;
    }

    public void set_doc_freq(int doc_freq){
        this.doc_freq = doc_freq;
    }

    public long get_term_freq(){
        return term_freq;
    }

    public void set_term_freq(long term_freq){
        this.term_freq = term_freq;
    }

    // uses the searchfiles.java code for taking input
    public static void main(String[] args) {
        String usage = "Usage:\tjava org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-query string] [-raw] [-paging hitsPerPage]\n\nSee http://lucene.apache.org/core/4_1_0/demo/ for details.";
        if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
            System.out.println(usage);
            System.exit(0);
        }

        String index = "index";
        String field = "contents";
        String queries = null;
        int repeat = 0;
        boolean raw = false;
        String queryString = null;
        int hitsPerPage = 10;
        
        for(int i = 0;i < args.length;i++) {
            if ("-index".equals(args[i])) {
                index = args[i+1];
                i++;
            } else if ("-field".equals(args[i])) {
                field = args[i+1];
                i++;
            } else if ("-queries".equals(args[i])) {
                queries = args[i+1];
                i++;
            } else if ("-query".equals(args[i])) {
                queryString = args[i+1];
                i++;
            } else if ("-repeat".equals(args[i])) {
                repeat = Integer.parseInt(args[i+1]);
                i++;
            } else if ("-raw".equals(args[i])) {
                raw = true;
            } else if ("-paging".equals(args[i])) {
                hitsPerPage = Integer.parseInt(args[i+1]);
                if (hitsPerPage <= 0) {
                System.err.println("There must be at least 1 hit per page.");
                System.exit(1);
                }
                i++;
            }
        }
        try{
            IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
            BufferedReader in = null;
            if (queries != null) {
                in = Files.newBufferedReader(Paths.get(queries), StandardCharsets.UTF_8);
            } else {
                in = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
            }
            while (true) {
                if (queries == null && queryString == null) {                        // prompt the user
                    System.out.println("Please enter the query:");
                }

                String line = queryString != null ? queryString : in.readLine();

                if (line == null || line.length() == -1) {
                    break;
                }

                line = line.trim();
                if (line.length() == 0) {
                    break;
                }
                // for multiple word query 
                String[] input_fields = line.split("\\s+");
                Date start = new Date();
                
                for (String input_field : input_fields){
                    
                    System.out.println("Searching for the term: " + input_field);
                    Term term = new Term("contents", input_field);
                    SimpleMetrics simpleM = new SimpleMetrics(reader,term);
                    System.out.println("Total Document in Index: " + reader.numDocs());
                    System.out.println("The Document Frequency for the term " + input_field + " is " + simpleM.get_doc_freq());
                    System.out.println("The Term Frequency for the term " + input_field + " is " + simpleM.get_term_freq());
                    System.out.println("************************************************************");

                }
                
                Date end = new Date();
                System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
                
                if (queryString != null) {
                    break;
                }
            }    
            reader.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        
    }
          
}