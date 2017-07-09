package io.bittiger.crawler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;

public class Crawler {

    private ObjectMapper mapper; //Json转换器
    private HashSet<String> identifyID;
    private int adId;
    private int tempPage;
    private String resultFile;

    public Crawler(String resultFile) {
        mapper = new ObjectMapper();
        this.identifyID = new HashSet<>();
        this.resultFile = resultFile;
        adId = 0;
    }

    /**
     * 相对路径的根目录是project根目录
     */
    void readQueryFromFile(String filePath) {
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString;
            while ((tempString = reader.readLine()) != null) {
                if (tempString.length() > 0) {
                    tempPage = 1;
                    Utils.sleep();

                    String[] temp = tempString.split(",");
                    String query = Utils.replaceSpace(temp[0].trim(), "%20");
                    double bidPrice = Double.parseDouble(temp[1].trim());
                    int campaignId = Integer.parseInt(temp[2].trim());
                    int query_group_id = Integer.parseInt(temp[3].trim());

                    while (getAmazonProds(query, bidPrice, campaignId, query_group_id, tempPage)) {
                        Utils.sleep();
                        tempPage++;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean getAmazonProds(String query, double bidPrice, int campaignId, int query_group_id, int page) {
        String url = Utils.AMAZON_QUERY_URL + Utils.replaceSpace(query, "%20");
        if (page >= 0) {
            url += ("&page=" + page);
        }
        System.out.println("url: " + url);

        Utils.initProxy();
        Utils.testProxy();
        try {
            HashMap<String, String> headers = new HashMap<>();
            headers.put("Accept", "*/*");
            headers.put("Accept-Encoding", "gzip, deflate");
            headers.put("Accept-Language", "zh-CN,zh;q=0.8");
            Document doc = Jsoup.connect(url).headers(headers).userAgent(Utils.USER_AGENT).timeout(10000).get();

            //获取分类 category
            Element category = doc.select("#leftNavContainer > ul:nth-child(2) > div > li:nth-child(1) > span > a > h4").first();
            if (category == null) {
                System.out.println("*** Not found product ***");
                return false;//下一个query
            }
            String categoryStr = category.text();
            System.out.println(categoryStr);

            //获取产品列表
            Elements products = doc.getElementsByClass("s-result-item celwidget ");
            System.out.println("size: " + products.size());

            for (Element element : products) {
                //获取唯一标识ID
                String asin = element.attr("data-asin");
                Elements titleEleLise = element.getElementsByAttribute("title");

                //有些Item里面是没有title的，那些不是产品
                if (titleEleLise.size() > 0 && addIdentifyID(asin)) {
                    Ad ad = new Ad();
                    ad.setCampaignId(campaignId);
                    ad.setQuery_group_id(query_group_id);
                    ad.setBidPrice(bidPrice);
                    ad.setCategory(categoryStr);
                    ad.setQuery(query.replace("%20", " "));
                    ad.setAdId(adId);
                    adId++;

                    //获取标题，关键词
                    ad.setTitle(titleEleLise.get(0).attr("title"));
                    ad.setKeyWords(Utils.cleanedTokenize(ad.getTitle()));

                    //获取详情页链接
                    String detailUrl = titleEleLise.get(0).attr("href");
                    if(!detailUrl.startsWith("https://")){
                        detailUrl = Utils.decodeRedirectURL(detailUrl);
                    }
                    ad.setDetail_url(detailUrl);

                    //获取图标
                    Elements pictureEleList = element.getElementsByClass("s-access-image cfMarker");
                    if (pictureEleList.size() > 0) {
                        ad.setThumbnail(pictureEleList.get(0).attr("src"));
                    }

                    //获取价格
                    Elements wholePriceEleList = element.getElementsByClass("sx-price-whole");
                    Elements fractionalPriceEleList = element.getElementsByClass("sx-price-fractional");
                    String price = "0.00";
                    if (wholePriceEleList.size() > 0) {
                        price = wholePriceEleList.get(0).text();
                        if (fractionalPriceEleList.size() > 0) {
                            price += ("." + fractionalPriceEleList.get(0).text());
                        }
                    }else{
                        Elements temp = element.select("div > div > div > div.a-fixed-left-grid-col.a-col-right > div:nth-child(2) > div.a-column.a-span7 > div > div > a > span.a-size-base.a-color-base");
                        if(temp != null && temp.size() > 0){
                            price = temp.first().text().replace("$", "");
                        }
                    }
                    ad.setPrice(Double.parseDouble(price.replace(",","")));

                    //将对象转换成json并写入文档
                    String json = mapper.writeValueAsString(ad);
                    writeToFile(json);
                    System.out.println(json);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 将每条信息的唯一标识加入HashSet中，返回false代表set内已有该id
     */
    private boolean addIdentifyID(String id) {
        if (!identifyID.contains(id)) {
            identifyID.add(id);
            return true;
        }
        return false;
    }

    /**
     * 将信息写入file
     */
    private void writeToFile(String str) {
        File file = new File(resultFile);
        FileWriter fileWriter;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(str);
            printWriter.flush();
            fileWriter.flush();
            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
