
package com.aliyun.activity.service.op.api.impl.propagation;

import com.google.common.base.Stopwatch;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by arthur.dy.lee on 2018/3/10.
 */
public class TecentCrawler {

    private Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public String sendGet(String url, String param) {
        String result = "";
        String urlName = url + "?" + param;
        try {
            URL realURL = new URL(urlName);
            URLConnection conn = realURL.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
            conn.connect();
            Map<String, List<String>> map = conn.getHeaderFields();
            /*for (String s : map.keySet()) {
                System.out.println(s + "-->" + map.get(s));
            }*/
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 发送http请求，并得到返回值
     *
     * @param url
     * @param param
     * @return
     */
    public String sendPost(String url, String param) {
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.106 Safari/537.36");
            //post设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += "\n" + line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 爬取目标数据，初步解析
     *
     * @param groupId
     * @return
     */
    public JSONObject getTecentCloudData(int groupId) {
        String sendRecvGet = this.sendGet("https://cloud.tencent.com/act/campus/group/detail", "group=" + groupId);
        if (StringUtils.isBlank(sendRecvGet)) {
            return null;
        }
        Document doc = Jsoup.parse(sendRecvGet);
        if (doc == null) {
            return null;
        }
        Elements elements = doc.getElementsByTag("script");
        if (elements == null || elements.get(0) == null) {
            return null;
        }
        Element element = elements.get(0);
        String content = element.toString();
        if (StringUtils.isBlank(content)) {
            return null;
        }
        content = content.replace("<script>", "")
                .replace("</script>", "")
                .replace("window.__INITIAL_STATE__ =", "")
                .trim();

        JSONObject object = JSONObject.fromObject(content);
        if (object.getJSONObject("actData") == null) {
            return null;
        }
        JSONObject actData = object.getJSONObject("actData");
        if (actData == null) {
            return null;
        }
        return actData;
    }

    /**
     * 解析团长id
     *
     * @param actData
     * @return
     */
    public String getCreator(int groupId, JSONObject actData) {
        String creator;
        try {
            creator = actData.getString("creator");
        } catch (JSONException e) {
            System.out.println("groupId=" + groupId + "还未开团，或团长节点不存在");
            return null;
        }
        if (actData.getString("creator") == null || StringUtils.isBlank(creator)) {
            return null;
        }
        return creator;
    }

    /**
     * 解析拼团的成员id
     *
     * @param actData
     * @return
     */
    public String getUsers(JSONObject actData) {
        String users;
        try {
            users = actData.getString("users");
        } catch (JSONException e) {
            System.out.println("不存在，成员节点不存在");
            return null;
        }
        if (actData.getString("users") == null || StringUtils.isBlank(users)) {
            return null;
        }

        if (actData.getString("users") == null || StringUtils.isNotBlank(users)) {
            users = users.replace("[", "").
                    replace("]", "").
                    replace("\"", "");
        }
        return users;

    }

    /**
     * 计数团队总人数
     *
     * @param users
     * @return
     */
    public int countTeamNumber(String users) {
        int paidUserNum = 0;
        int teamNumber = 1; //是团长计1
        if (StringUtils.isNotBlank(users)) {
            paidUserNum = users.split(",").length;
            teamNumber = paidUserNum + teamNumber;
        }
        return teamNumber;
    }

    /**
     * 生成文件里写的内容
     *
     * @param groupId
     * @param teamNumber
     * @param creator
     * @param users
     * @return
     */
    public String buildWriteFileContent(int groupId, int teamNumber, String creator, String users) {
        StringBuilder groupContent = new StringBuilder(64);
        groupContent.append(groupId).append(",").append(teamNumber).append(",").append(creator).append("#").append(",");
        if (StringUtils.isNotBlank(users)) {
            groupContent.append(users.replace(",", "#"));
        } else {
            groupContent.append(users);
        }
        groupContent.append("#");
        return groupContent.toString();
    }

    /**
     * 写文件，以及记录爬取数据countMap，以便统计
     *
     * @param filePath
     * @param countMap
     * @param groupId
     * @return
     */
    public Map<Integer, Integer> writeDataAndCountMap(String filePath, Map<Integer, Integer> countMap,
                                                      int groupId) {

        JSONObject actData = getTecentCloudData(groupId);
        if (actData == null) {
            return countMap;
        }
        String creator = getCreator(groupId, actData);
        if (StringUtils.isBlank(creator)) {
            return countMap;
        }
        String users = getUsers(actData);

        int teamNumber = countTeamNumber(users);
        String writeContent = buildWriteFileContent(groupId, teamNumber, creator, users);

        this.writeFile(filePath, writeContent);

        //Integer num = countMap.get("拼团数量为" + teamNumber); //取共有几个这样的组
        Integer num = countMap.get(teamNumber);
        if (num == null) {
            num = 0;
        }
        countMap.put(teamNumber, ++num);

        return countMap;
    }

    /**
     * 往指定目录写文件
     *
     * @param filePath
     * @param content
     */
    public static void writeFile(String filePath, String content) {
        String bOM = new String(new byte[] { (byte) 0xEF, (byte) 0xBB, (byte) 0xBF });

        //FileWriter fw = null;
        BufferedWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(filePath);
            //fw = new FileWriter(f, true);
            fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f, true), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(bOM + content);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计数据
     *
     * @param filePath
     * @param countNumberMap
     */
    public void writeCountMap2FilesEnd(String filePath, Map<Integer, Integer> countNumberMap) {
        this.writeFile(filePath, "\r\n");
        this.writeFile(filePath, "========统计数据==========");
        StringBuilder countTitles = new StringBuilder();
        countTitles.append("序号").append(",").append("团内人数").append(",").append("团数").append(",")
                .append("总人数");
        this.writeFile(filePath, countTitles.toString());
        int i = 1;
        for (Map.Entry<Integer, Integer> entry : countNumberMap.entrySet()) {
            StringBuilder countNumbers = new StringBuilder(24);
            int key = entry.getKey();
            int val = entry.getValue();

            countNumbers.append(i++).append(",").append(key).append(",").append(val).append(",")
                    .append(key * val);
            this.writeFile(filePath, countNumbers.toString());
        }
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                System.out.println("删除单个文件" + fileName + "成功！");
                return true;
            } else {
                System.out.println("删除单个文件" + fileName + "失败！");
                return false;
            }
        } else {
            System.out.println("删除单个文件失败：" + fileName + "不存在！");
            return false;
        }
    }

    //得到一个任务
    public Callable<Map<Integer, Integer>> getTask(String filePath, Map<Integer, Integer> countMap, int groupId) {
        Callable<Map<Integer, Integer>> task = new Callable<Map<Integer, Integer>>() {
            @Override
            public Map<Integer, Integer> call() throws Exception {
                TecentCrawler crawler = new TecentCrawler();
                crawler.writeDataAndCountMap(filePath, countMap, groupId);
                return countMap;
            }
        };
        return task;
    }

    public Map<Integer, Integer> readFileContent(String path, Map<Integer, Integer> map) {
        BufferedReader br = null;
        String readLine = null;
        try {
            br = new BufferedReader(new FileReader(path));
            while ((readLine = br.readLine()) != null) {
                if (StringUtils.isBlank(readLine)) {
                    continue;
                }
                String[] contentArray = readLine.split(",");
                if (contentArray == null || contentArray.length < 2) {
                    continue;
                }
                String number = contentArray[1];
                if (!isNumeric(contentArray[1])) {
                    continue;
                }
                Integer teamNumber = 0;
                teamNumber = Integer.valueOf(number);

                if (map.get(teamNumber) != null) {
                    int total = map.get(teamNumber);
                    map.put(teamNumber, ++total);
                } else {
                    map.put(teamNumber, 1);
                }

            }
            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            br = null;
        }

        return map;
    }

    public boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 单线程执行任务
     *
     * @param filePath
     * @param retMap
     */
    public void singleThreadCount(String filePath, Map<Integer, Integer> retMap,
                                  final int crawlerBeginGroupId, final int crawlerEnGroupId) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        Map<Integer, Integer> countMap = new ConcurrentHashMap<>();
        for (int groupId = crawlerBeginGroupId; groupId <= crawlerEnGroupId; groupId++) {
            //countMap = this.writeDataAndCountMap(filePath, countMap, groupId);
            countMap = this.writeDataAndCountMap(filePath, countMap, groupId);
        }
        retMap = this.readFileContent(filePath, retMap);
        this.writeCountMap2FilesEnd(filePath, retMap);

        long nanos = stopwatch.elapsed(TimeUnit.NANOSECONDS);
        long second = TimeUnit.NANOSECONDS.toSeconds(nanos);
        writeFile(filePath, "统计用时: " + second
                + "  单线程统计 统计数据从groupId=" + crawlerBeginGroupId + " 开始 直到 groupId=" + crawlerEnGroupId);
        System.out.println("统计用时: " + second);

        //直接从结果中去读，不从文件中读
        writeFile(filePath, "\r\n从结果中开始读------------------------------");
        this.writeCountMap2FilesEnd(filePath, retMap);

    }

    /**
     * 多线程执行任务
     *
     * @param filePath
     * @param retMap
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public void multiThreadCount(String filePath, Map<Integer, Integer> retMap, final int crawlerBeginGroupId,
                                 final int crawlerEnGroupId) throws InterruptedException, ExecutionException {

        Stopwatch stopwatch = Stopwatch.createStarted();
        TecentCrawler crawler = new TecentCrawler();

        ExecutorService exec = Executors.newFixedThreadPool(20);
        CompletionService<Map<Integer, Integer>> completionService =
                new ExecutorCompletionService<Map<Integer, Integer>>(exec);
        for (int groupId = crawlerBeginGroupId; groupId <= crawlerEnGroupId; groupId++) {
            TecentCrawler tecentCrawler = new TecentCrawler();
            Map<Integer, Integer> contentMap = new ConcurrentHashMap<>();
            completionService.submit(tecentCrawler.getTask(filePath, contentMap, groupId));
        }
        for (int groupId = crawlerBeginGroupId; groupId <= crawlerEnGroupId; groupId++) {
            //检索并移除表示下一个已完成任务的 Future，如果目前不存在这样的任务，则等待。
            Future<Map<Integer, Integer>> future = completionService.take();
            Map<Integer, Integer> tempMap = future.get();

            for (Map.Entry<Integer, Integer> entry : tempMap.entrySet()) {
                Integer key = entry.getKey();
                if (key == null) {
                    continue;
                }
                Integer val = entry.getValue();
                if (retMap.get(key) != null) {
                    retMap.put(key, retMap.get(key) + val);
                } else {
                    retMap.put(key, 1);
                }
            }
        }
        System.out.println("retMap：" + retMap);
        crawler.writeCountMap2FilesEnd(filePath, retMap);
        exec.shutdown();

        long nanos = stopwatch.elapsed(TimeUnit.NANOSECONDS);
        long second = TimeUnit.NANOSECONDS.toSeconds(nanos);

        //writeFile(filePath, "统计用时: " + second + "  多线程统计  统计数据从groupId=" + crawlerBeginGroupId + " 开始 - 直到 groupId=" + crawlerEnGroupId + "  统计完成时间: " + format.format(new Date()));
        writeFile(filePath, "统计数据从groupId=" + crawlerBeginGroupId + " 开始 - 直到 groupId=" + crawlerEnGroupId + "  统计完成时间: " + format.format(new Date()));
        System.out.println("统计用时: " + second);

        //        writeFile(filePath, "\r\n再读一次文件统计数据----------------------");
        //        retMap = new TreeMap<Integer, Integer>(new Comparator<Integer>() {
        //            public int compare(Integer obj1, Integer obj2) {
        //                return obj1.compareTo(obj2);
        //            }
        //        });
        //        retMap = this.readFileContent(filePath, retMap);
        //        this.writeCountMap2FilesEnd(filePath, retMap);
    }

    public static void main(String[] args) throws Exception {
        final String filePath = "E:\\TecentCrawler-201080330.csv";
        TecentCrawler.deleteFile(filePath);

        TecentCrawler crawler = new TecentCrawler();
        final int crawlerBeginGroupId = 10010;
        final int crawlerEnGroupId = 30000;

        String title = "groupId,团员数量,团长,成员";
        TecentCrawler.writeFile(filePath, title);

        Map<Integer, Integer> retMap = new TreeMap<Integer, Integer>(new Comparator<Integer>() {
            public int compare(Integer obj1, Integer obj2) {
                return obj1.compareTo(obj2);
            }
        });

        //crawler.singleThreadCount(filePath, retMap, crawlerBeginGroupId, crawlerEnGroupId);
        crawler.multiThreadCount(filePath, retMap, crawlerBeginGroupId, crawlerEnGroupId);

    }
}



