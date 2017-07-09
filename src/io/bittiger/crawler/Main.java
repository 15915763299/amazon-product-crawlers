package io.bittiger.crawler;

import java.io.*;

public class Main{

    public static void main(String[] args) throws IOException {
        Crawler crawler = new Crawler("resultExample.txt");
        crawler.readQueryFromFile("rawQuery.txt");
    }
}