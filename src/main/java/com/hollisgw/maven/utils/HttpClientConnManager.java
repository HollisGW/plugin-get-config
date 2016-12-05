package com.hollisgw.maven.utils;

import java.nio.charset.Charset;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Created by hollisgw on 15/8/28.
 */
public class HttpClientConnManager {

    private static RequestConfig httpConfig;
    private static PoolingHttpClientConnectionManager clientConnManager;
    /**
     * 最大连接数：{@value}
     */
    public static final int MAX_TOTAL_CONNECTIONS = 800;

    public static final int WAIT_TIMEOUT = 60000;
    /**
     * 每个路由基础的连接数：{@value}
     */
    public static final int MAX_ROUTE_CONNECTIONS = 400;
    /**
     * 默认连接超时时间：{@value}ms
     */
    public static final int CONNECT_TIMEOUT = 10000;
    /**
     * Socket 超时时间：{@value}ms
     */
    public static final int SOCKET_TIMEOUT = 10000;

    static {
        httpConfig = RequestConfig.custom().setConnectTimeout(CONNECT_TIMEOUT).setSocketTimeout(SOCKET_TIMEOUT).build();

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory()).register("https", SSLConnectionSocketFactory.getSocketFactory()).build();

        clientConnManager = new PoolingHttpClientConnectionManager(registry);
        clientConnManager.setMaxTotal(MAX_TOTAL_CONNECTIONS);
        clientConnManager.setDefaultMaxPerRoute(MAX_ROUTE_CONNECTIONS);
        clientConnManager.setDefaultConnectionConfig(ConnectionConfig.custom().setCharset(Charset.forName("utf-8")).build());
    }

    /**
     * <p>功能描述：
     * <pre>
     * 基于{@link PoolingHttpClientConnectionManager} 创建可关闭的 {@link CloseableHttpClient}.
     * 默认连接超时时间: {@link #CONNECT_TIMEOUT}, 默认 socket 超时时间: {@link #SOCKET_TIMEOUT}, 默认编码 utf-8.
     * TODO CloseableHttpClient 连接释放方法待研究
     * </pre>
     * </p>
     * <p>创建人：hollisgw</p>
     * <p>创建日期：2014年12月24日 下午5:51:23</p>
     *
     * @return
     */
    public static CloseableHttpClient getPoolingHttpClient() {
        return HttpClients.custom().setConnectionManager(clientConnManager).setDefaultRequestConfig(httpConfig).build();
    }

    /**
    * <p>功能描述：创建可关闭的 {@link CloseableHttpClient}，默认连接超时时间: {@link #CONNECT_TIMEOUT}, 默认 socket 超时时间: {@link #SOCKET_TIMEOUT}, 默认编码 utf-8</p>
    * <p>创建人：hollisgw</p>
    * <p>创建日期：2014年12月24日 下午5:51:23</p>
    *
    * @return
    */
    public static CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setDefaultRequestConfig(httpConfig).build();
    }

    /**
     * <p>功能描述：
     * <pre>
     * 基于{@link PoolingHttpClientConnectionManager} 创建可关闭的 {@link CloseableHttpClient}，默认编码 utf-8.
     * TODO CloseableHttpClient 连接释放方法待研究
     * </pre>
     * </p>
     * <p>创建人：hollisgw</p>
     * <p>创建日期：2014年12月24日 下午5:56:26</p>
     *
     * @param connectTimeout
     * @param socketTimeout
     * @return
     */
    public static CloseableHttpClient getPoolingHttpClient(int connectTimeout, int socketTimeout) {
        RequestConfig httpConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        return HttpClients.custom().setConnectionManager(clientConnManager).setDefaultRequestConfig(httpConfig).build();
    }

    /**
    * <p>功能描述：创建可关闭的 {@link CloseableHttpClient}, 默认编码 utf-8</p>
    * <p>创建人：hollisgw</p>
    * <p>创建日期：2014年12月24日 下午5:56:26</p>
    *
    * @param connectTimeout
    * @param socketTimeout
    * @return
    */
    public static CloseableHttpClient getHttpClient(int connectTimeout, int socketTimeout) {
        RequestConfig httpConfig = RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build();
        return HttpClients.custom().setDefaultRequestConfig(httpConfig).build();
    }
}
