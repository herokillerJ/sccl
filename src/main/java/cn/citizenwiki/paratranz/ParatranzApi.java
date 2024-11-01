package cn.citizenwiki.paratranz;

import cn.citizenwiki.HttpStatus;
import cn.citizenwiki.http.ParatranzJacksonBodyHandler;
import cn.citizenwiki.model.dto.paratranz.PZFile;
import cn.citizenwiki.model.dto.paratranz.PZTranslation;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * Paratranz API封装
 *
 * @see <a href="https://paratranz.cn/docs">接口文档</a>
 */
public class ParatranzApi {


    public static final String ENV_PZ_PROJECT_ID = "PZ_PROJECT_ID";
    public static final String ENV_PZ_TOKEN = "PZ_TOKEN";
    public final String urlPrefix;
    public final String urlFiles;
    private final HttpClient httpClient;
    private ParatranzConfig config;

    public ParatranzApi() {
        //Paratranz相关配置
        this.config = loadConfig();
        //http客户端
        this.httpClient = buildHttpClient();
        //生成好所有的url
        this.urlPrefix = "https://paratranz.cn/api/projects/" + this.config.getProjectId();
        this.urlFiles = urlPrefix + "/files";
        // 注册关闭钩子
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    /**
     * 从环境变量中加载Paratranz配置
     */
    private static ParatranzConfig loadConfig() {
        ParatranzConfig config = new ParatranzConfig();
        config.setProjectId(System.getenv(ENV_PZ_PROJECT_ID));
        config.setToken(System.getenv(ENV_PZ_TOKEN));
        if (config.getProjectId() == null || config.getProjectId().trim().isEmpty()) {
            System.err.println(ENV_PZ_PROJECT_ID + " is null");
            throw new RuntimeException(ENV_PZ_PROJECT_ID + " is null");
        }
        if (config.getToken() == null || config.getToken().trim().isEmpty()) {
            System.err.println(ENV_PZ_TOKEN + " is null");
            throw new RuntimeException(ENV_PZ_TOKEN + " is null");
        }
        return config;
    }

    /**
     * 构建http客户端
     *
     * @return
     */
    private HttpClient buildHttpClient() {
        final HttpClient httpClient;
        httpClient = HttpClient.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).build();

        return httpClient;
    }

    /**
     * 销毁资源
     */
    private void close() {
        this.httpClient.close();
    }

    /**
     * 构建auth请求头，锁有接口通用
     *
     * @return
     */
    private HttpRequest.Builder authRequestBuilder() {
        return HttpRequest.newBuilder().header("Authorization", this.config.getToken());
    }

    /**
     * 通用发送请求方法
     *
     * @param request     请求
     * @param bodyHandler 响应体处理器
     * @param <T>
     * @return
     */
    private <T> HttpResponse<T> sendRequest(HttpRequest request, HttpResponse.BodyHandler<T> bodyHandler) {
        try {
            HttpResponse<T> response = httpClient.send(request, bodyHandler);
            if (HttpStatus.OK.getCode() != response.statusCode()) {
                String msg = "http error，code：%d，msg：%s%n%n".formatted(response.statusCode(), response.body());
                throw new RuntimeException(msg);
            }
            return response;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * /projects/{projectId}/files
     */
    public List<PZFile> projectFiles() {
        HttpRequest request = authRequestBuilder()
                .uri(URI.create(urlFiles))
                .GET()
                .build();
        return sendRequest(request, ParatranzJacksonBodyHandler.LIST_FILE).body();
    }

    /**
     * /projects/{projectId}/files/{fileId}/translation
     *
     * @param fileId 文件id
     * @return
     */
    public List<PZTranslation> fileTranslation(Integer fileId) {
        String urlString = this.urlFiles + "/" + fileId + "/translation";
        HttpRequest request = authRequestBuilder()
                .uri(URI.create(urlString))
                .GET()
                .build();
        return sendRequest(request, ParatranzJacksonBodyHandler.LIST_TRANSLATION).body();
    }


}
