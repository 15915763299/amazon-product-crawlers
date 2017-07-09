# amazon-product-crawlers
CS504 Week5 Homework<br>
该项目根据指定的关键词在亚马逊上进行数据的爬取<br>

    过程：feeds --> web crawler --> crawled data
***

## 1.feeds
爬虫的饲料，就是上面目录中的 `rowQuery.txt` 文件。爬虫将按行读取feeds中的信息进行爬取，并将结果保存成文件。

## 2.web crawler
爬虫主要根据工具包 [Jsoup](https://jsoup.org/download) 实现，至于怎么用，就自行查找吧。

####    爬虫的实现思路如下：
* 读取 feeds 中每一行的信息
* 根据每行的关键词进行查询，一个关键词将会有多页的信息
* 对于每一页，使用 Jsoup 进行网页的读取与每条信息的抓取
* 对于每条信息，先将其整合为 Java 对象，再将对象以 Json 格式保存到文件中（转换对象为 Json 格式使用 Jackson，[下载](https://mvnrepository.com/artifact/com.fasterxml.jackson.core)）

####    为防止被屏蔽，以下是要注意的地方：
* 每次抓取网页前后，请让线程停留1~2秒
* 每次抓取网页，最好更换IP（使用Proxy，即代理服务器）

####    关于信息的抓取
* Jsoup中有各种 getElementByXXX ，可以充分利用。
* 如果想一步获取 Element ，可以使用 A.select("B") 方法，A 可以是 Document 也可以是 Element 。如何获取 B 呢，分两步（我用的是Chrome）：

      1.右键网页上的元素，选择“检查”，英文Inspect，浏览器会自动打开开发者界面，定位到该元素的位置。
      2.右键元素，选择 Copy --> Copy selector ，复制下来的东西就是 B ，是这个标签的查找路径。

####    信息去重（dedup）
亚马逊里面的每条信息都会有自己的唯一ID `data-asin="B072HX7B4W"`，使用HashSet记录每条信息的 data-asin ，抓取信息时先抓取 data-asin ，若ID存在则不继续抓取。

####    org.apache.lucene.analysis
此次作业有要求，将每个产品的标题切割为词，还有“2/2”要切割成两个“2”等，有时两个词中间不止一个空格也麻烦。
对于这种复杂情况的切词，可以使用 `lucene.analysis` 一步到位，还可以去除 “and”、 “to” 这一类的无效词汇：
[下载](http://mvnrepository.com/artifact/org.apache.lucene)，
[英文文档](http://lucene.apache.org/core/6_6_0/core/org/apache/lucene/analysis/package-summary.html)，
[中文参考](http://www.cnblogs.com/dennisit/p/3258664.html)

## 3.crawled data
这套程序抓一次用了9个小时，每次抓不一定完美，有时会报Timeout的错整页没抓取下来，有些产品没有价格等等，我设置抓取网页的间隔时间为2秒，可以改短一些。
上面的文件中 `resultExample.txt` 是部分抓取结果。
