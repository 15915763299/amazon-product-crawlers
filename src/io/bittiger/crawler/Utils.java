package io.bittiger.crawler;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URLDecoder;
import java.util.*;

public class Utils {

    private static String[] proxy = {
            "199.101.97.130",
            "199.101.97.132",
            "199.101.97.139",
            "199.101.97.140",
            "199.101.97.143",
            "199.101.97.145",
            "199.101.97.146",
            "199.101.97.147",
            "199.101.97.148",
            "199.101.97.149",
            "199.101.97.154",
            "199.101.97.156",
            "199.101.97.159",
            "199.101.97.161",
            "199.101.97.162",
            "199.101.97.164",
            "199.101.97.169",
            "199.101.97.170",
            "199.101.97.173",
            "199.101.97.178",
            "199.101.97.181",
            "199.101.97.183",
            "199.101.97.185",
            "199.101.97.186",
            "199.101.97.188",
            "173.208.78.34",
            "173.208.78.36",
            "173.208.78.37",
            "173.208.78.38",
            "173.208.78.39"
    };

    static final String AMAZON_QUERY_URL = "https://www.amazon.com/s/ref=nb_sb_ss_c_1_6?field-keywords=";
    static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.86 Safari/537.36";
    private static final String AUTHOR = "bittiger";
    private static final String AUTHOR_PASSWORD = "cs504";
    private static CharArraySet stopWordsSet;

    /**
     * 获取随机代理地址
     */
    private static String getRandomProxy() {
        Random r = new Random();
        int randomNum = r.nextInt(proxy.length);
        return proxy[randomNum];
    }

    static void initProxy() {
        System.setProperty("socksProxyHost", getRandomProxy());
        System.setProperty("socksProxyPort", "61336");//HTTP:60099, SOCKS5:61336
        Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                AUTHOR,
                                AUTHOR_PASSWORD.toCharArray()
                        );
                    }
                }
        );
    }

    static void testProxy() {
        String test_url = "http://www.toolsvoid.com/what-is-my-ip-address";
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Accept", "*/*");
            headers.put("Accept-Encoding", "gzip, deflate, br");
            headers.put("Accept-Language", "zh-CN,zh;q=0.8");
            Document doc = Jsoup.connect(test_url).headers(headers).userAgent(USER_AGENT).timeout(10000).get();

            String IP = doc.select("body > section.articles-section > div > div > div > div.col-md-8.display-flex > div > div.table-responsive > table > tbody > tr:nth-child(1) > td:nth-child(2) > strong").text();
            System.out.println("IP-Address: " + IP);

            String country = doc.select("body > section.articles-section > div > div > div > div.col-md-8.display-flex > div > div.table-responsive > table > tbody > tr:nth-child(4) > td:nth-child(2) > strong").text();
            String province = doc.select("body > section.articles-section > div > div > div > div.col-md-8.display-flex > div > div.table-responsive > table > tbody > tr:nth-child(5) > td:nth-child(2)").text();
            System.out.println("Position: " + country + " " + province);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将单词间的一个或多个空格转为指定字符串
     */
    static String replaceSpace(String str, String replace) {
        Analyzer analyzer = new WhitespaceAnalyzer();
        TokenStream stream = analyzer.tokenStream("", str);
        CharTermAttribute termAtt = stream.addAttribute(CharTermAttribute.class);

        StringBuilder result = new StringBuilder();
        try {
            stream.reset();
            while (stream.incrementToken()) {
                result.append(termAtt.toString()).append(replace);
            }
            result.delete(result.length() - 3, result.length());
            stream.end();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                stream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result.toString();
    }


    /**
     * 获取 stop words
     */
    private static CharArraySet getStopWords() {
        if(stopWordsSet == null || stopWordsSet.size() == 0){
            ArrayList<String> stopWordsList = new ArrayList<>();
            File file = new File("stopWords.txt");
            BufferedReader reader;
            try {
                reader = new BufferedReader(new FileReader(file));
                String tempString;
                while ((tempString = reader.readLine()) != null) {
                    stopWordsList.add(tempString);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            stopWordsSet = new CharArraySet(stopWordsList, true);
        }
        return stopWordsSet;
    }

    /**
     * 将一段文字拆分为词
     */
    static List<String> cleanedTokenize(String input) {
        StringReader reader = new StringReader(input);
        Tokenizer tokenizer = new StandardTokenizer();
        tokenizer.setReader(reader);

        TokenStream tokenStream = new StandardFilter(tokenizer);
        //过滤stop words
        tokenStream = new StopFilter(tokenStream, getStopWords());
        //提取词干（词汇还原为一般形式）
        tokenStream = new KStemFilter(tokenStream);
        //转换为小写
        tokenStream = new LowerCaseFilter(tokenStream);

        CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
        Set<String> tokensSet = new HashSet<>();
        try {
            tokenStream.reset();
            while (tokenStream.incrementToken()) {
                String term = charTermAttribute.toString();
                tokensSet.add(term);
            }
            tokenStream.end();
            tokenStream.close();
            tokenizer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<String> tokensList = new ArrayList<>();
        tokensList.addAll(tokensSet);
        return tokensList;
    }

    static String decodeRedirectURL(String url){
        String query = url.substring(url.indexOf("?") + 1);
        String[] queryArray = query.split("&");
        String result = "";
        try{
            String s1 = "https://www.amazon.com";
            String s2 = "UTF8";
            for(String s : queryArray){
                if(s.startsWith("url=")){
                    s1 = s.substring(4);
                }
                if(s.startsWith("ie=")){
                    s2 = s.substring(3);
                }
            }
            result = URLDecoder.decode(s1, s2);
        } catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 线程休眠2秒
     */
    static void sleep() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
