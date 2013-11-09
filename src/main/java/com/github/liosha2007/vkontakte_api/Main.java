package com.github.liosha2007.vkontakte_api;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: liosha
 * Date: 06.11.13
 * Time: 18:47
 */
public class Main {

    /**
     * {APP_ID} – идентификатор Вашего приложения (выдается после создания приложения http://vk.com/editapp?act=create&site=1);
     * {PERMISSIONS} – запрашиваемые права доступа приложения (http://vk.com/dev/permissions);
     * {DISPLAY} – внешний вид окна авторизации, поддерживаются: page, popup и mobile.
     * {REDIRECT_URI} – адрес, на который будет передан access_token, для desktop приложений - https://oauth.vk.com/blank.html.
     * {API_VERSION} – версия API, которую Вы используете (http://vk.com/dev/versions). Актуальная версия: 5.3.
     * Кроме основных параметров есть дополнительный параметр revoke=1, позволяющий в случае, если пользователь уже авторизовывался и давал необходимые права – не пропускать этап подтверждения прав.
     * Если Вы разрабатываете браузерное Javascript-приложение (обращаетесь к API с внешнего сайта), необходимо указывать ссылку в рамках домена, указанного в настройках приложения. В остальных случаях в качестве redirect_uri нужно использовать адрес https://oauth.vk.com/blank.html.
     */
    private static final String AUTH_URL = "https://oauth.vk.com/authorize?client_id={APP_ID}&scope={PERMISSIONS}&redirect_uri={REDIRECT_URI}&display={DISPLAY}&v={API_VERSION}&response_type=token";
    /**
     * METHOD_NAME – название метода из списка функций API (http://vk.com/dev/methods),
     * PARAMETERS – параметры соответствующего метода API,
     * ACCESS_TOKEN – ключ доступа, полученный в результате успешной авторизации приложения.
     */
    private static final String API_REQUEST = "https://api.vk.com/method/{METHOD_NAME}?{PARAMETERS}&access_token={ACCESS_TOKEN}&v=5.3";
    private static final int GET = 1;
    private static final int POST = 2;

    public static void main(String[] args) throws Exception{
        System.out.println("========================= begin =========================");

        String appId = System.getProperty("appId");
        String appKey = System.getProperty("appKey");

        String reqUrl = AUTH_URL
                .replace("{APP_ID}", appId)
                .replace("{PERMISSIONS}", "photos")
                .replace("{REDIRECT_URI}", "https://oauth.vk.com/blank.html")
                .replace("{DISPLAY}", "page")
                .replace("{API_VERSION}", "5.3");

//        System.out.println("Open URL in browser and give access. Then add GET parameters to environment variables: " + reqUrl);

        String access_token = System.getProperty("access_token");
        String expires_in = System.getProperty("expires_in");
        String user_id = System.getProperty("user_id");

        if (access_token == null || user_id == null) {
            throw new Exception("access_token or user_id is null");
        }

//        Коды возвращаемых ошибок
//        14 Captcha needed – требуется ввод кода с картинки (Captcha). Данный процесс описан на отдельной странице (http://vk.com/dev/captcha_error).
//        16 HTTP authorization failed – требуется выполнение запросов по протоколу https, т.к. пользователь включил настройку, требующую работу через безопасное соединение.
//        17 Validation required – требуется валидация пользователя. Процесс валидации пользователя описан на отдельной странице (http://vk.com/dev/need_validation).

        String resp = null;

        /**
         * user_idsперечисленные через запятую идентификаторы пользователей или их короткие имена (screen_name). По умолчанию — идентификатор текущего пользователя.
         * список строк, разделенных через запятую, количество элементов должно составлять не более 1000
         * fieldsсписок дополнительных полей, которые необходимо вернуть.
         * Доступные значения: nickname, screen_name, sex, bdate, city, country, timezone, photo_50, photo_100, photo_200_orig, has_mobile, contacts, education, online, counters, relation, last_seen, status, can_write_private_message, can_see_all_posts, can_see_audio, can_post, universities, schools,verified
         * список строк, разделенных через запятую
         * name_caseпадеж для склонения имени и фамилии пользователя. Возможные значения: именительный – nom,
         * родительный – gen, дательный – dat, винительный – acc, творительный – ins, предложный – abl. По умолчанию nom. строка
         */
        reqUrl = API_REQUEST
                .replace("{METHOD_NAME}", "users.get")
                .replace("{PARAMETERS}&", "")
                .replace("{ACCESS_TOKEN}", access_token);
        resp = invokeApi(reqUrl, GET, MapUtils.asMap("user_ids", user_id).pt("fields", "nickname,photo_200_orig"));
        System.out.println("users.get: " + resp);
        Thread.sleep(1000);

        /**
         * owner_idидентификатор пользователя или сообщества, которому принадлежат альбомы.
         * Обратите внимание, идентификатор сообщества в параметре owner_id необходимо указывать со знаком "-" — например, owner_id=-1 соответствует идентификатору сообщества ВКонтакте API (club1)
         * int (числовое значение), по умолчанию идентификатор текущего пользователя
         * album_idsперечисленные через запятую ID альбомов.
         * список чисел, разделенных запятыми
         * offsetсмещение, необходимое для выборки определенного подмножества альбомов.
         * положительное число
         * countколичество альбомов, которое нужно вернуть. (по умолчанию – все альбомы) положительное число
         * need_system1 – будут возвращены системные альбомы, имеющие отрицательные идентификаторы.
         * флаг, может принимать значения 1 или 0
         * need_covers1 — будет возвращено дополнительное поле thumb_src. По умолчанию поле thumb_src не возвращается.
         * флаг, может принимать значения 1 или 0
         * photo_sizes1 — будут возвращены размеры фотографий в специальном формате (http://vk.com/dev/photo_sizes).
         * флаг, может принимать значения 1 или 0
         */
        reqUrl = API_REQUEST
                .replace("{METHOD_NAME}", "photos.getAlbums")
                .replace("{PARAMETERS}&", "")
                .replace("{ACCESS_TOKEN}", access_token);
        resp = invokeApi(reqUrl, GET, MapUtils.asMap("owner_id", user_id).pt("photo_sizes", "1").pt("thumb_src", "1"));
        System.out.println("photos.getAlbums: " + resp);
        Thread.sleep(1000);

        String albumId = "180311381";

        /**
         * Параметры
         * owner_idидентификатор владельца альбома.
         * Обратите внимание, идентификатор сообщества в параметре owner_id необходимо указывать со знаком "-" — например, owner_id=-1 соответствует идентификатору сообщества ВКонтакте API (club1)
         * int (числовое значение), по умолчанию идентификатор текущего пользователя
         * album_idидентификатор альбома. строка
         * photo_idsидентификаторы фотографий, информацию о которых необходимо вернуть.
         * список строк, разделенных через запятую
         * revпорядок сортировки фотографий (1 — антихронологический, 0 — хронологический).
         * флаг, может принимать значения 1 или 0
         * extended1 — будут возвращены дополнительные поля likes, comments, tags, can_comment. Поля comments и tags содержат только количество объектов. По умолчанию данные поля не возвращается.
         * флаг, может принимать значения 1 или 0
         * feed_typeТип новости получаемый в поле type метода newsfeed.get, для получения только загруженных пользователем фотографий, либо только фотографий, на которых он был отмечен. Может принимать значения photo, photo_tag.  строка
         * feedUnixtime, который может быть получен методом newsfeed.get в поле date, для получения всех фотографий загруженных пользователем в определённый день либо на которых пользователь был отмечен. Также нужно указать параметр uid пользователя, с которым произошло событие.
         * int (числовое значение)
         * photo_sizesВозвращать ли доступные размеры фотографии в специальном формате.
         * флаг, может принимать значения 1 или 0
         */
        reqUrl = API_REQUEST
                .replace("{METHOD_NAME}", "photos.get")
                .replace("{PARAMETERS}&", "")
                .replace("{ACCESS_TOKEN}", access_token);
        resp = invokeApi(reqUrl, GET, MapUtils.asMap("owner_id", user_id).pt("album_id", albumId).pt("photo_sizes", "1"));
        System.out.println("photos.get: " + resp);
        Thread.sleep(1000);
        // 09.11.2013 23:51
        // 56/75 http://cs419429.vk.me/v419429581/16a03/wghc9NDoZow.jpg
        // 768/1024 http://cs419429.vk.me/v419429581/16a07/Yl1BEqdb1dE.jpg

        String photoId = "310883100";

        /**
         * Параметры
         * photosперечисленные через запятую идентификаторы, которые представляют собой идущие через знак подчеркивания id пользователей, разместивших фотографии, и id самих фотографий. Чтобы получить информацию о фотографии в альбоме группы, вместо id пользователя следует указать -id группы.
         * Пример значения photos: 1_129207899,6492_135055734,-20629724_271945303
         * Некоторые фотографии, идентификаторы которых могут быть получены через API, закрыты приватностью, и не будут получены. В этом случае следует использовать ключ доступа фотографии (access_key) в её идентификаторе.
         * Пример значения photos: 1_129207899_220df2876123d3542f, 6492_135055734_e0a9bcc31144f67fbd
         * Поле access_key будет возвращено вместе с остальными данными фотографии в методах, которые возвращают фотографии, закрытые приватностью но доступные в данном контексте. Например данное поле имеют фотографии, возвращаемые методом newsfeed.get.
         * список строк, разделенных через запятую, обязательный параметр
         * extended1 — будут возвращены дополнительные поля likes, comments, tags, can_comment, can_repost. Поля comments и tags содержат только количество объектов. По умолчанию данные поля не возвращается.
         * флаг, может принимать значения 1 или 0
         * photo_sizesвозвращать ли доступные размеры фотографии в специальном формате.
         * флаг, может принимать значения 1 или 0
         */
        reqUrl = API_REQUEST
                .replace("{METHOD_NAME}", "photos.getById")
                .replace("{PARAMETERS}&", "")
                .replace("{ACCESS_TOKEN}", access_token);
        resp = invokeApi(reqUrl, GET, MapUtils.asMap("photos", user_id + "_" + photoId).pt("photo_sizes", "1"));
        System.out.println("photos.getById: " + resp);




        String _albumId = "166381544";
        int sec = getDownloadTime(access_token, user_id, _albumId);
        System.out.println("Download time: " + sec + " sec");
        // 1280/960 - 92шт - 32 секунды

        System.out.println("========================== end ==========================");
    }

    public static String invokeApi(String requestUrl, int method, Map<String, String> params) throws Exception {
        if (method == GET) {
            String getParams = "";
            for (String key : params.keySet()){
                getParams += "&" + key + "=" + params.get(key);
            }
            requestUrl += getParams;
        }
        URL url = new URL(requestUrl);
        InputStream is = url.openStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));

        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
    public static int getDownloadTime(String access_token, String user_id, String albumId) throws Exception {
        String reqUrl = API_REQUEST
                .replace("{METHOD_NAME}", "photos.get")
                .replace("{PARAMETERS}&", "")
                .replace("{ACCESS_TOKEN}", access_token);
        String resp = invokeApi(reqUrl, GET, MapUtils.asMap("owner_id", user_id).pt("album_id", albumId).pt("photo_sizes", "1"));

        JSONObject jsonObject = new JSONObject(resp);
        JSONObject response = jsonObject.getJSONObject("response");
        JSONArray items = response.getJSONArray("items");
        long start = new Date().getTime();
        int imageCount = 0;
        for (int n = 0; n < items.length(); n++){
            JSONObject item = (JSONObject)items.get(n);
            JSONArray sizes = item.getJSONArray("sizes");
            for (int m = 0; m < sizes.length(); m++) {
                JSONObject size = (JSONObject)sizes.get(m);
                if (size.getInt("width") == 1280) {
                    URL url = new URL(size.getString("src"));
                    URLConnection urlConnection = url.openConnection();
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
                    File tmpFile = File.createTempFile("sdf", m + "_" + imageCount + ".jpg");
                    OutputStream outputStream = new FileOutputStream(tmpFile);
                    IOUtils.copy(bufferedInputStream, outputStream);
                    outputStream.close();
                    bufferedInputStream.close();
                    System.out.println("Downloaded file: " + (imageCount + 1) + " (" + tmpFile.getAbsolutePath() + ")");
                    imageCount++;
                    break;
                }
            }
        }
        long end = new Date().getTime();
        return (int)(end - start) / 1000;
    }
}
