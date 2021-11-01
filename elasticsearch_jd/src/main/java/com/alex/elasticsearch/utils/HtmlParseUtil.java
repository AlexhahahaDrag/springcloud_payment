package com.alex.elasticsearch.utils;

import com.alex.elasticsearch.entity.Content;
import com.alex.elasticsearch.entity.Info;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HtmlParseUtil {

    public static List<Content> parseJD(String keyword) throws IOException {
        String url = "http://erptestm.rloraerp.com:8006/OA_HTML/OA.jsp?OAFunc=OAHOMEPAGE";
        Document document = Jsoup.parse(new URL(new String(url.getBytes(), "utf-8")), 3000);
        Element j_goodsList = document.getElementById("AppsNavLink");
        Elements lis = j_goodsList.getElementsByTag("li");
        List<Content> list = new ArrayList<>();
        for (Element element : lis) {
            String img = element.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = element.getElementsByClass("p-price").eq(0).text();
            String name = element.getElementsByClass("p-name").eq(0).text();
            Content content = new Content(img, price, name);
            list.add(content);
        }
        return list;
    }

    public static List<Info> parseCamel(String keyword) throws IOException {
        String url = "https://camel.apache.org/components/latest/" + keyword + "-component.html";
        Document document = Jsoup.parse(new URL(new String(url.getBytes(), "utf-8")), 3000);
        Elements content1 = document.getElementsByClass("sect2");
        Element element1 = content1.get(1);
        Elements tr = element1.getElementsByTag("tr");
        List<Info> list = new ArrayList<>();
        for (Element element : tr) {
            Elements td = element.getElementsByTag("td");
            if (td != null && td.size() > 0) {
                Info info = new Info();
                info.setName(td.get(0).text());
                info.setDescription(td.get(1).text());
                info.setDefaultv(td.get(2).text());
                info.setType(td.get(3).text());
                list.add(info);
            }
        }
        return list;
    }

    public static void main(String[] args) throws IOException {
        List<Info> contents = HtmlParseUtil.parseCamel("");
        List<Content> contents11 = HtmlParseUtil.parseJD("vue");
        contents.forEach(System.out::println);
        System.out.println(contents11);
    }
}
