package com.software.redisapi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import spark.Spark;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static spark.Spark.*;
public class RedisApi {
    public static final Gson GSON = new Gson();
    private static final String UPLOAD_DIR = "uploads"; // 图片存储目录
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static String getMimeType(String filename) {
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (filename.endsWith(".png")) {
            return "image/png";
        } else {
            return "application/octet-stream";
        }//
    }
    public static void main(String[] args) {
        ipAddress("0.0.0.0");
        port(5005);

        new File(UPLOAD_DIR).mkdirs();
        // 图片上传路由（带文件名编码）
        post("/api/images/upload", (req, res) -> {
            try {
                DiskFileItemFactory factory = new DiskFileItemFactory();
                ServletFileUpload upload = new ServletFileUpload(factory);
                upload.setSizeMax(MAX_FILE_SIZE);

                List<FileItem> items = upload.parseRequest(req.raw());
                Map<String, String> result = new HashMap<>();

                for (FileItem item : items) {
                    if (!item.isFormField()) {
                        String originalName = new File(item.getName()).getName();
                        String fileName = UUID.randomUUID() + "_" + originalName;
                        File uploadedFile = new File(UPLOAD_DIR, fileName);
                        item.write(uploadedFile);

                        // 编码文件名
                        result.put("url", "http://myredisapi.com/api/images/" + fileName);
                        result.put("status", "success");
                    }
                }

                res.type("application/json");
                return GSON.toJson(result);
            } catch (Exception e) {//
                res.status(500);
                return GSON.toJson(Map.of("error", e.getMessage()));
            }
        });

        // 图片获取路由（带文件名解码）
        get("/api/images/:filename", (request, response) -> {
            try {
                String encodedFilename = request.params(":filename");

                File file = new File(UPLOAD_DIR, encodedFilename);

                if (file.exists()) {
                    response.type(getMimeType(encodedFilename));

                    // 使用 Java 原生方法复制流
                    try (InputStream is = new FileInputStream(file)) {
                        is.transferTo(response.raw().getOutputStream());
                    }
                    return "";
                } else {
                    response.status(404);
                    response.type("text/plain");
                    return "Image not found";
                }
            } catch (Exception e) {
                response.status(500);
                return "Error: " + e.getMessage();
            }
        });
        before((request, response) -> response.type("application/json"));
        // 设置键值对（带过期时间）
        post("/api/string/:key", (req, res) -> {
            String value = req.queryParams("value");
            int expire = Integer.parseInt(req.queryParams("expire")); // 0表示无过期时间
            if(expire == -1){
                RedisService.set(req.params("key"), value);
            }
            else{
                RedisService.setEx(req.params("key"), value, expire);
            }

            return GSON.toJson(Map.of("status", "success"));
        });
        get("/api/string/:key", (req, res) -> {
            String value = RedisService.get(req.params("key"));
            return value != null ?
                    GSON.toJson(Map.of("value", value)) :
                    halt(404, GSON.toJson(Map.of("error", "Key not found")));
        });
        post("/api/hash/:key", (req, res) -> {
            Map<String, String> fields = GSON.fromJson(req.body(), Map.class);
            RedisService.hSet(req.params("key"), fields);
            return GSON.toJson(Map.of("status", "success"));
        });

        get("/api/hash/:key/:field", (req, res) -> {
            String value = RedisService.hGet(req.params("key"), req.params("field"));
            return value != null ?
                    GSON.toJson(Map.of("value", value)) :
                    halt(404, GSON.toJson(Map.of("error", "Field not found")));
        });
        post("/api/zadd/:key", (req, res) -> {
            Map<String, Double> members = GSON.fromJson(req.body(), new TypeToken<Map<String, Double>>(){}.getType());
            RedisService.zAdd(req.params("key"), members);
            return GSON.toJson(Map.of("status", "success"));
        });
        post("/api/sadd/:key", (req, res) -> {
            String member = req.queryParams("member");

            Long added = RedisService.sAdd(req.params("key"), member);

            return GSON.toJson(Map.of("value", added));
        });
        get("/api/scard/:key",(req,res)->{
            Long value = RedisService.sCard(req.params("key"));
            return GSON.toJson(Map.of("value", value.toString()));
        });
        get("/api/sismember/:key",(req,res) -> {
            String member = req.queryParams("member");
            boolean value = RedisService.sIsMembers(req.params("key"),member);
            return GSON.toJson(Map.of("value", Boolean.toString(value)));
        });
        get("/api/smembers/:key",(req,res)->{
           Set<String> value = RedisService.sMembers(req.params("key"));
           return GSON.toJson(value);
        });
        post("/api/srem/:key",(req,res) -> {
            String member = req.queryParams("member");
            Long value = RedisService.sRem(req.params("key"),member);
            return GSON.toJson(Map.of("value", Long.toString(value)));
        });
        get("/api/del/:key",(req,res) -> {

            Long count = RedisService.del(req.params("key"));
            return GSON.toJson(Map.of("value", count.toString()));
        });

        get("/api/zset/:key/range", (req, res) -> {
            int min = Integer.parseInt(req.queryParams("min"));
            int max = Integer.parseInt(req.queryParams("max"));
            Set<String> members = RedisService.zRange(req.params("key"), min, max);
            return GSON.toJson(members);
        });
        get("/api/zset/:key/revrange",(req,res)->{
            int min = Integer.parseInt(req.queryParams("min"));
            int max = Integer.parseInt(req.queryParams("max"));
            Set<String> members = RedisService.zRevRange(req.params("key"),min,max);
            return GSON.toJson(members);
        });
        get("/api/zcard/:key",(req,res)->{
            Long value = RedisService.zCard(req.params("key"));
            return GSON.toJson(Map.of("value", value.toString()));
        });
        post("/api/list/:key/lpush", (req, res) -> {
            String[] values = GSON.fromJson(req.body(), String[].class);
            RedisService.lPush(req.params("key"), values);
            return GSON.toJson(Map.of("status", "success"));
        });
        post("/api/zset/zincrby/:key",(req,res)->{
            Double v = Double.valueOf(req.queryParams("value"));
            String member = req.queryParams("member");
            Double value = RedisService.zIncrBy(req.params("key"),v,member);
            return GSON.toJson(Map.of("value",value.toString()));
        });


        get("/api/list/:key", (req, res) -> {
            long start = Long.parseLong(req.queryParams("start"));
            long end = Long.parseLong(req.queryParams("end"));
            List<String> items = RedisService.lRange(req.params("key"), start, end);
            return GSON.toJson(items);
        });
        post("/api/object/:key", (req, res) -> {
            Object obj = GSON.fromJson(req.body(), Object.class); // 实际应指定具体类型
            int expire = Integer.parseInt(req.queryParams("expire"));
            RedisService.setObject(req.params("key"), obj, expire);
            return GSON.toJson(Map.of("status", "success"));
        });
        get("/api/object/:key", (req, res) -> {
            Object obj = RedisService.getObject(req.params("key"), Object.class);
            return obj != null ?
                    GSON.toJson(obj) :
                    halt(404, GSON.toJson(Map.of("error", "Object not found")));
        });
        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.body(GSON.toJson(Map.of(
                    "error", "Internal Server Error",
                    "detail", e.getMessage()
            )));
        });

        exception(NumberFormatException.class, (e, req, res) -> {
            res.status(400);
            res.body(GSON.toJson(Map.of("error", "Invalid parameter format")));
        });
        exception(Exception.class, (e, req, res) -> {
            res.status(500);
            res.body(GSON.toJson(Map.of("error", "Internal Server Error"))); // 强制 JSON
        });

    }
}
