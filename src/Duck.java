import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Duck {
    public static void main(String[] args) throws Exception {

        JFrame frame = new JFrame("Rubber Duck");
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(500, 750);
        ImageIcon img = new ImageIcon("src/duck.png");
        Image scaleImage = img.getImage().getScaledInstance(250, 250, Image.SCALE_DEFAULT);
        ImageIcon icon = new ImageIcon(scaleImage);

        JLabel label = new JLabel();
        label.setBounds(15, 15, 470, 470);
        label.setIcon(icon);
        frame.add(label);

        JTextField textField = new JTextField();
        textField.setBounds(0,400,500,50);
        frame.add(textField);

        JTextArea textField1 = new JTextArea();
        textField1.setBounds(5,475,480,250);
        textField1.setEditable(false);
        Font newTextFieldFont=new Font(textField.getFont().getName(),textField.getFont().getStyle(),12);
        textField1.setFont(newTextFieldFont);
        textField.setFont(newTextFieldFont);
        textField.setHorizontalAlignment(SwingConstants.LEFT);
        textField1.setLineWrap(true);
        textField1.setWrapStyleWord(true);
        frame.add(textField1);

        JButton button = new JButton("Ask!");
        button.setBounds(400, 200, 75, 50);
        frame.add(button);

        button.addActionListener(e -> {
            textField1.setText("");
            String string;
            try {
                string = chatGPT(textField.getText());
                textField1.setText(string);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            /*
            for(int i = 0; i<string.length(); i++){
                textField1.setText(textField1.getText() + string.charAt(i));
            }*/

        });

        frame.setVisible(true);
/*
        String x = chatGPT("what is your name?");
        for(int i = 0; i<x.length(); i++) {
            System.out.print(x.charAt(i));
            if(x.charAt(i) == '.')
                System.out.println();
        }
 */
    }
    public static String chatGPT(String str) throws IOException {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = "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";
        String model = "gpt-3.5-turbo";
        String message = str;

        // Create the HTTP POST request
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + apiKey);
        con.setRequestProperty("Content-Type", "application/json");

        // Build the request body
        String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + "You will pretend to be a duck people talk to to get programming answers. You must answer the following question after the semicolon. Refer to yourself as rubber duck and not as an AI. If you understood all of that then answer this: " +message + "\"}]}";
        con.setDoOutput(true);
        OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
        writer.write(body);
        writer.flush();
        writer.close();

        // Get the response
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        // Print the response
        String x = (response.toString().split("\"content\":\"")[1].split("\"")[0]).substring(4);
        return x;
    }
}
