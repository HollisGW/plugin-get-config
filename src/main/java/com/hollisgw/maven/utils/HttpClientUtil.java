package com.hollisgw.maven.utils;

import com.google.common.collect.Lists;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hollisgw on 15/8/28.
 */
public class HttpClientUtil  extends AbstractMojo{

    private final static Logger LOGGER = LogManager.getLogger(HttpClientUtil.class);

    /**
    * <p>功能描述：http get </p>
    * <p>创建人：hollisgw</p>
    * <p>创建日期：2014年12月24日 下午6:24:50</p>
    *
    * @param url
    * @return
    */
    public  FetchData getContentByUrl(String url) {
        return getContentByUrl(url, null, null);
    }

    /**
    * <p>功能描述：http get</p>
    * <p>创建人：hollisgw</p>
    * <p>创建日期：2014年12月24日 下午7:24:31</p>
    *
    * @param url
    * @param charsetName
    * @param cookieStr
    * @return
    */
    public FetchData getContentByUrl(String url, String charsetName, String cookieStr) {
        getLog().info("url is : " + url);
        FetchData fetchData = new FetchData();
        fetchData.setUrl(url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            HttpGet httpGet = new HttpGet(url);
            if (cookieStr != null && !"".equals(cookieStr)) {
                httpGet.setHeader("Cookie", cookieStr);
            }
            httpClient = HttpClientConnManager.getHttpClient(Constants.FIVE_SECONDS, Constants.TEN_SECONDS);
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                getLog().error("get " + url + " error, code:" + response.getStatusLine().getStatusCode());
                return fetchData;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                Charset charset = charsetName == null ? Consts.UTF_8 : Charset.forName(charsetName);
                if (len != -1 && len < 2048) {
                    fetchData.setContent(EntityUtils.toString(entity, charset));
                } else {
                    fetchData.setContent(EntityUtils.toString(new BufferedHttpEntity(entity), charset));
                }
            }
        } catch (Exception e) {
            String errorMsg = "Get ERROR!!! Exception is:" + e + " and url is:" + url;
            getLog().error(errorMsg);
            return fetchData;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                LOGGER.error("close http client failed.", e);
            }
        }
        return fetchData;
    }

     /**
     * <p>功能描述：http post</p>
     * <p>创建人：hollisgw</p>
     * <p>创建日期：2014年12月24日 下午10:15:16</p>
     *
     * @param postMethod
     * @return
     */
    public static FetchData getContentByPostMethod(final HttpPost postMethod) {
        return getContentByPostMethod(postMethod, null);
    }
    
    /**
    * <p>功能描述：http post</p>
    * <p>创建人：hollisgw</p>
    * <p>创建日期：2014年12月24日 下午7:24:23</p>
    *
    * @param postMethod
    * @param parameters
    * @return
    */
    public static FetchData getContentByPostMethod(final HttpPost postMethod, List<NameValuePair> parameters) {
        FetchData fetchData = new FetchData();
        fetchData.setUrl(postMethod.getURI().toString());
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            addParameter(postMethod, parameters);
            httpClient = HttpClientConnManager.getHttpClient(Constants.THREE_SECONDS, Constants.FIVE_SECONDS);
            response = httpClient.execute(postMethod);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOGGER.error("post " + postMethod.getURI().toString() + " error, code:" + response.getStatusLine().getStatusCode());
                return fetchData;
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1 && len < 2048) {
                    fetchData.setContent(EntityUtils.toString(entity));
                } else {
                    fetchData.setContent(EntityUtils.toString(new BufferedHttpEntity(entity), Consts.UTF_8));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Post Error!!! Exception:" + e + " and url is:" + postMethod.getURI());
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e) {
                LOGGER.error("close http client failed.", e);
            }
        }
        return fetchData;
    }
    
     /**
     * <p>功能描述：http get</p>
     * <p>创建人：hollisgw</p>
     * <p>创建日期：2014年12月24日 下午9:40:40</p>
     *
     * @param url
     * @param charset
     * @return
     */
    public static byte[] getBytesByUrl(final String url, String charset) {
        LOGGER.debug("url is :" + url);
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClientConnManager.getHttpClient(Constants.THREE_SECONDS, Constants.TEN_SECONDS);
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Content-Type", "charset=" + charset);
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LOGGER.error("post " + url + " error, code:" + response.getStatusLine().getStatusCode());
            }
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                long len = entity.getContentLength();
                if (len != -1 && len < 2048) {
                    return EntityUtils.toByteArray(entity);
                } else {
                    return EntityUtils.toByteArray(new BufferedHttpEntity(entity));
                }
            }
        } catch (Exception e) {
            LOGGER.error("Get ERROR!!! Exception is:" + e + " and url is:" + url);
        } finally {
            try {
                if (response != null) {
                    response.close();
                    response = null;
                }
                if (httpClient != null) {
                    httpClient.close();
                    httpClient = null;
                }
            } catch (Exception e) {
                LOGGER.error("close http client failed.", e);
            }
        }
        return null;
    }

    public static byte[] getpictruebytes(String url) {
        // 转码
        url = dealInvalidUri(url);
        try {
            Pattern pattern = Pattern.compile("[\u4e00-\u9fa5]");
            Matcher matcher = pattern.matcher(url);
            while (matcher.find()) {
                url = url.replace(matcher.group(), URLEncoder.encode(matcher.group(), "utf-8"));
            }
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("UnsupportedEncodingException>>" + e.getMessage());
        }
        // 重试3次
        int tryTimes = Constants.FETCH_TRY_TIMES;
        byte[] pic = null;
        for (int i = 1; i <= tryTimes; i++) {
            boolean rightResult = true;
            CloseableHttpClient httpClient = null;
            CloseableHttpResponse response = null;
            try {
                httpClient = HttpClientConnManager.getHttpClient(Constants.TWENTY_SECONDS, Constants.TWENTY_SECONDS);
                HttpGet httpGet = new HttpGet(url);
                response = httpClient.execute(httpGet);
                if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                    LOGGER.error("post " + url + " error, code:" + response.getStatusLine().getStatusCode());
                    break;
                }
                String value = response.getLastHeader("Content-Type").getValue();
                if ("text/html".equals(value)) {
                    LOGGER.info(" content-type is text/html...");
                    break;
                }
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    long len = entity.getContentLength();
                    if (len != -1 && len < 2048) {
                        pic = EntityUtils.toByteArray(entity);
                    } else {
                        pic = EntityUtils.toByteArray(new BufferedHttpEntity(entity));
                    }
                }
            } catch (Exception e) {
                rightResult = false;
                if (i == tryTimes) {
                    LOGGER.error("Error!!! IOException:", e);
                }
            } finally {
                try {
                    if (response != null) {
                        response.close();
                        response = null;
                    }
                    if (httpClient != null) {
                        httpClient.close();
                        httpClient = null;
                    }
                } catch (Exception e) {
                    LOGGER.error("close http client failed.", e);
                }
                if (rightResult) {
                    break;
                }
            }
        }
        return pic;
    }
    
     /**
     * <p>功能描述：构造参数</p>
     * <p>创建人：hollisgw</p>
     * <p>创建日期：2014年12月24日 下午10:21:44</p>
     *
     * @param name
     * @param value
     * @return
     */
    public static List<NameValuePair> getParameters(String name, String value) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        List<NameValuePair> parameters = Lists.newArrayList();
        parameters.add(new BasicNameValuePair(name, value));
        return parameters;
    }
    
     /**
     * <p>功能描述：添加 post 参数</p>
     * <p>创建人：hollisgw</p>
     * <p>创建日期：2014年12月24日 下午9:19:57</p>
     *
     * @param httpPost
     * @param nameValuePair
     */
    public static void addParameter(HttpPost httpPost, List<NameValuePair> nameValuePair) {
        if (httpPost != null && null != nameValuePair) {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, Consts.UTF_8));
        }
    }

    private static String dealInvalidUri(String url) {
        return url.replaceAll(" ", "%20");
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        // TODO Auto-generated method stub
        
    }

}
