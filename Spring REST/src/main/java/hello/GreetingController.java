package hello;

import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class GreetingController {

    private static final String template = "%s";
    private final AtomicLong counter = new AtomicLong();

    public static String callURL(String myURL) {
        System.out.println("Requested URL:" + myURL);
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(myURL);
            urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception while calling URL:"+ myURL, e);
        }

        return sb.toString();
    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }

    @RequestMapping("/greetings")
    public Answer greetings(@RequestParam(value="q", defaultValue="") String question) {
        if(question.toLowerCase().contains("your")&&question.toLowerCase().contains("name"))
            return new Answer("Hello, Kitty! My name is Rakib");
        else if(question.toLowerCase().contains("your")&&question.toLowerCase().contains("name"))
            return new Answer("Hello, Kitty! My name is Rakib");
        return new Answer(String.format(template, question));
    }

    @RequestMapping("/weather")
    public Answer weather(@RequestParam(value="q", defaultValue="") String question) throws Exception {
        if(question.toLowerCase().contains("your")&&question.toLowerCase().contains("name"))
            return new Answer("Hello, Kitty! My name is Rakib");
        else if(question.toLowerCase().contains("temperature")&&question.toLowerCase().contains("in"))
        {
            String[] cityName = question.split("\\s");
            String json = callURL("http://api.openweathermap.org/data/2.5/weather?q=" + cityName[cityName.length - 1]);
            try {
                //JSONArray jsonArray = new JSONArray(json);
				JSONObject obj = new JSONObject(json);
				String temp = obj.getJSONObject("main").getString("temp");
				return new Answer("Hello, Kitty! Today's temperature is (Celsius) "+(Double.parseDouble(temp)-273.5));
                
            } catch (JSONException e) {
                e.printStackTrace();
            }
        
        }
        else if(question.toLowerCase().contains("humidity")&&question.toLowerCase().contains("in"))
        {
            String[] cityName = question.split("\\s");
            String json = callURL("http://api.openweathermap.org/data/2.5/weather?q=" + cityName[cityName.length - 1]);
            try {
                //JSONArray jsonArray = new JSONArray(json);
                JSONObject obj = new JSONObject(json);
                String hum = obj.getJSONObject("main").getString("humidity");
                return new Answer("Hello, Kitty! Today's humidity is (Percentage) "+(Double.parseDouble(hum)-273.5));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else if(question.toLowerCase().contains("weather")&&question.toLowerCase().contains("in"))
        {
            String[] cityName = question.split("\\s");
            String stateOfWeather = cityName[2];
            String json = callURL("http://api.openweathermap.org/data/2.5/weather?q=" + cityName[cityName.length - 1]);
            try {
                JSONObject obj = new JSONObject(json);
                JSONArray jsonArray = obj.getJSONArray("weather");
                for(int i=0;i<jsonArray.length();i++)
                {
                    String temp = jsonArray.getJSONObject(i).getString("main");
                    if(temp ==stateOfWeather.toLowerCase())
                    return new Answer("Yes");
                    else return new Answer("No");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return new Answer(String.format(template, question));
    }
}