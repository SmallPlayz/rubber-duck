import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class OpenAIRequest {
    private final String API_URL = "https://api.openai.com/v1/audio/transcriptions";
    private final String BOUNDARY = "----Boundary" + System.currentTimeMillis();

    public String sendRequest(String openAIKey, String model, String filePath) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        connection.setRequestProperty("Authorization", "Bearer " + openAIKey);

        String filename = new File(filePath).getName();
        FileInputStream fileInputStream = new FileInputStream(filePath);

        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes("--" + BOUNDARY + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"model\"\r\n\r\n");
        outputStream.writeBytes(model + "\r\n");

        outputStream.writeBytes("--" + BOUNDARY + "\r\n");
        outputStream.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"\r\n");
        outputStream.writeBytes("Content-Type: audio/mp3\r\n\r\n");

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fileInputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.writeBytes("\r\n--" + BOUNDARY + "--\r\n");

        fileInputStream.close();
        outputStream.close();

        int responseCode = connection.getResponseCode();
        BufferedReader reader;
        if (responseCode >= 200 && responseCode < 300) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        StringBuilder responseBody = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            responseBody.append(line);
        }
        reader.close();

        return responseBody.toString();
    }
}
