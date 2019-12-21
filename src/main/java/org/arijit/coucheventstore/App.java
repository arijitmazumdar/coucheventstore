package org.arijit.coucheventstore;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.util.LinkedList;
import com.jayway.jsonpath.JsonPath;
import org.apache.commons.math3.primes.Primes;
import org.arijit.coucheventstore.*;

public final class App {
    private String urlStr = "localhost:5984/baseball/_changes?feed=live&heartbeat=6000&since=now";
    private Property property;
    private HttpURLConnection con;

    private App(final String urlStr, final Property property) throws java.io.IOException {
        this.urlStr = urlStr;
        this.property = property;
    }

    public App() {
	}

	private void close() {
        con.disconnect();
    }

    private void listen() throws IOException {
        final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            if (inputLine.length() == 0) {
                continue;
            }
            content.append(inputLine);
            // System.out.println(content);
            //q.add(content.toString());
            content = new StringBuffer();
        }
    }

    private void setup() throws IOException, MalformedURLException, ProtocolException {
        final HttpURLConnection con = (HttpURLConnection) new URL(urlStr).openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        // con.setConnectTimeout(5000);
        // con.setReadTimeout(5000);
        con.setInstanceFollowRedirects(true);

        // we create new `LinkedList` as a backing queue and decorate it
        ListenableQueue<String> q = new ListenableQueue<>(new LinkedList<>());
        // register a listener which polls a queue and prints an element
        switch (property) {
        case PRIME:
            q.registerListener(e -> processPrime(q.poll()));
            break;
        case PALINDROME:
            q.registerListener(e -> processPalindrome(q.poll()));
            break;
        case SQUARE:
            q.registerListener(e -> processSquare(q.poll()));
            break;
        case CUBE:
            q.registerListener(e -> processCube(q.poll()));
            break;
        default:
            break;
        }
    }

    private void processPrime(String message) {
        int no = getNumber(message);
        if (Primes.isPrime(no)) {
            publishMessage(no, "This is a prime number");
        }
    }

    private void publishMessage(int no, String string) {
    }

    private boolean isPalindrom(String text) {
        if (text == null) {
            return false;
        }
        int left = 0;
        int right = text.length() - 1;
        while (left < right) {
            if (text.charAt(left++) != text.charAt(right--)) {
                return false;
            }
        }
        return true;
    }

    private void processPalindrome(String message) {
        int no = getNumber(message);
        if (isPalindrom(new String())) {
            publishMessage(no, "This is a prime number");
        }
    }

    private boolean isSquare(double d) {
        double sqrt = Math.sqrt(d);
        return ((sqrt - Math.floor(sqrt)) == 0);
    }

    private void processSquare(String message) {
        int no = getNumber(message);
        if (isSquare(Double.valueOf(no))) {
            publishMessage(no, "This is a perfect square");
        }
    }

    private boolean isCube(double d) {
        double cbrt = Math.cbrt(d);
        return ((cbrt - Math.floor(cbrt)) == 0);
    }

    private void processCube(String message) {
        int no = getNumber(message);
        if (isCube(Double.valueOf(no))) {
            publishMessage(no, "This is a perfect cube");
        }
    }

    private int getNumber(String json) {
        if (json == null)
            return -1;
        String numberStr = JsonPath.read(json, "$.doc.number");
        return Integer.parseInt(numberStr);
    }

    /**
     * Says hello to the world.
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");
        App app = new App();
        try {
            app.setup();
            app.listen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            app.close();
        }
    }

}
