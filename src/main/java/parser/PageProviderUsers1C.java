package parser;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PageProviderUsers1C implements PageProvider {

    private final CookieStore cookieStore;
    private final CloseableHttpClient httpClient;

    private final String userName;
    private final String password;
    private final String cookieFileName;
    
    private static final Logger log = LoggerFactory.getLogger(PageProviderUsers1C.class);

    public PageProviderUsers1C(String userName, String password, String cookieFileName) {
        this.userName = userName;
        this.password = password;
        this.cookieFileName = cookieFileName;

        File cookieFile = new File(cookieFileName);

        BasicCookieStore tmpStore = null;
        if (cookieFile.exists()) {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(cookieFile));
                tmpStore = (BasicCookieStore) in.readObject();
                in.close();
            } catch (Exception e) {
                log.error("Error while reading cookie file", e);
            }
        }

        if (tmpStore == null) {
            cookieStore = new BasicCookieStore();
        } else {
            cookieStore = tmpStore;
        }

        httpClient = HttpClients.custom()
                .setDefaultCookieStore(cookieStore)
                .build();
    }

    @Override
    public String getPage(String url) {

        log.debug("Trying to get page '{}' using cookie", url);
        String pageContent = getPageContent(url);

        if (!pageContent.isEmpty() && !isLoginForm(pageContent)) {
            log.debug("Success!");
            return pageContent;
        } else {
            log.debug("Failed. Need authorization.");
            if (authorize()) {
                log.info("Successfully authorized in users.v8.1c.ru!");
                return getPageContent(url);
            } else {
                log.error("Not authorized in users.v8.1c.ru");
            }
        }

        return null;
    }

    private boolean authorize() {
        
        // Запрашиваем страницу для ввода логина/пароля
        String loginPage = getPageContent("https://portal.1c.ru/applications/auth");
        
        List<org.apache.http.NameValuePair> formParameters = getFormParameters(loginPage);
        formParameters.add(new BasicNameValuePair("username", userName));
        formParameters.add(new BasicNameValuePair("password", password));

        String userProfilePage = postData(getLoginButtonUrl(loginPage), formParameters);

        return !userProfilePage.isEmpty();
    }

    private List<org.apache.http.NameValuePair> getFormParameters(String loginPage) {

        List<org.apache.http.NameValuePair> formParameters = new ArrayList<>();

        Document pageContent = Jsoup.parse(loginPage);
        Elements inputs = pageContent.select("form#loginForm input[type=hidden]");

        formParameters.addAll(inputs.stream().map(input -> new BasicNameValuePair(input.attr("name"), input.attr("value"))).collect(Collectors.toList()));

        return formParameters;
    }
    
    private String getLoginButtonUrl(String loginPage) {
        
        Document pageContent = Jsoup.parse(loginPage);
        Element form = pageContent.getElementById("loginForm");
        
        StringBuilder url = new StringBuilder("https://login.1c.ru");
        
        if (form == null) {
            url.append("/login");
        } else {
            url.append(form.attr("action"));
        }
        return url.toString();
    }

    private boolean isLoginForm(String page) {
        Document pageContent = Jsoup.parse(page);
        Element form = pageContent.getElementById("loginForm");

        return form != null;
    }

    private String getPageContent(String url) {
        
        String pageContent = "";

        HttpGet httpGet = new HttpGet(url.replace("\\", "%5C"));

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            
            if (log.isDebugEnabled()) {
                log.debug(url);
                log.debug(response.getStatusLine().toString());
            }
            pageContent = EntityUtils.toString(response.getEntity());

        } catch (IOException e) {
            log.error("Error while retrieving page "+url, e);
        }

        return pageContent;
    }

    private String postData(String url, List<org.apache.http.NameValuePair> postedData) {

        HttpPost httpPost = new HttpPost(url);

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(postedData));
            HttpResponse response = httpClient.execute(httpPost);

            if (log.isDebugEnabled())
                log.debug(response.getStatusLine().toString());

            Header redirectTo = response.getFirstHeader("Location");

            if (redirectTo != null) {
                return getPageContent(redirectTo.getValue());
            }
        } catch (IOException e) {
            log.error("Error while posting data to "+url, e);
        }

        return "";
    }

    @Override
    public void close() throws IOException {

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(cookieFileName));
            out.writeObject(cookieStore);
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("Error while writing cookie file", e);
        }

        httpClient.close();
    }
}
