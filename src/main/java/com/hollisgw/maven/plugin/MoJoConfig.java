package com.hollisgw.maven.plugin;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.hollisgw.maven.utils.FetchData;
import com.hollisgw.maven.utils.HttpClientUtil;

/**
 * Goal which touches a timestamp file.
 *
 * @goal getConfig
 * 
 * @phase process-sources
 */
public class MoJoConfig extends AbstractMojo {

    /** 
     * @parameter expression="${configUrl}" 
     * @required 
     */
    String configUrl;

    /** 
     * @parameter expression="${appName}" 
     * @required 
     */
    String appName;

    /** 
     * @parameter expression="${env}" 
     * @required 
     */
    String env;

    /** 
     * @parameter expression="${configFilePath}" 
     * @required 
     */
    String configFilePath;

    public void execute() throws MojoExecutionException, MojoFailureException {
        String url = configUrl + "?appName=" + appName + "&env=" + env;

        HttpClientUtil httpClient = new HttpClientUtil();
        FetchData data;
        data = httpClient.getContentByUrl(url);
        getLog().info(data.getContent());

        String relativelyPath = System.getProperty("user.dir") + configFilePath;
        getLog().info(relativelyPath);

        JSONObject configJson = JSONObject.fromObject(data.getContent());
        @SuppressWarnings("rawtypes")
        Iterator it = configJson.keys();
        while (it.hasNext()) {
            String key = (String) it.next();
            JSONArray config = configJson.getJSONArray(key);
            String fileName = relativelyPath + "/" + key + ".properties";
            this.createFile(fileName, config);
        }
    }

    public boolean createFile(String fileName, JSONArray config) {
        getLog().info(fileName);
        File file = new File(fileName);
        FileWriter fw = null;
        BufferedWriter writer = null;
        try {
            file.createNewFile();
            fw = new FileWriter(file);
            writer = new BufferedWriter(fw);
            for (Object element : config) {
                // 创建文件 
                JSONObject json = JSONObject.fromObject(element);
                writer.write(json.get("key") + "=" + json.get("value"));
                writer.newLine();//换行
            }
            writer.flush();
        } catch (IOException e) {
            getLog().error("MoJoConfig " + e);
        } finally {
            try {
                writer.close();
                fw.close();
            } catch (IOException e) {
                getLog().error("MoJoConfig " + e);
            }
        }
        return true;
    }

    public static void main(String[] args) {
        String url = "http://localhost:8080/config/getConfigList.shtml?appName=testapp&env=test";

        FetchData data;
        HttpClientUtil httpClinet = new HttpClientUtil();
        data = httpClinet.getContentByUrl(url);

        JSONArray array = JSONArray.fromObject(data.getContent());
        //        JsonArray array = gson.fromJson(data.getContent(), JsonArray.class);
        for (Object element : array) {
            JSONObject json = JSONObject.fromObject(element);
            System.out.println(json.get("key"));
            System.out.println(json.get("value"));
        }

    }

}
