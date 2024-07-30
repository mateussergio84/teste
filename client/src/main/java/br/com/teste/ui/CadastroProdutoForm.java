package br.com.teste.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

public class CadastroProdutoForm extends JFrame {
    private JPanel panel;
    private JTextField descricaoField;
    private JTextField precoField;
    private JTextField codigoField;
    private JButton submitButton;

    public CadastroProdutoForm() {
        setTitle("Cadastro de Produto");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        descricaoField = new JTextField(15);
        add(descricaoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(new JLabel("Preço:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        precoField = new JTextField(15);
        add(precoField, gbc);



        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        submitButton = new JButton("Cadastrar");
        add(submitButton, gbc);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSubmit();
            }
        });
    }

    private void handleSubmit() {
        String descricao = descricaoField.getText();
        String precoText = precoField.getText();

        try {
            BigDecimal preco = new BigDecimal(precoText);

            String url = "http://localhost:8080/produto";

            String json = String.format("{\"descricao\":\"%s\",\"preco\":%s}",
                    descricao, preco.toPlainString());

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();

                JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(this, "Falha ao cadastrar produto. Código de resposta: " + responseCode);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao tentar cadastrar produto.");
        }
    }

    private String parseCodigoFromResponse(String jsonResponse) {

        int startIndex = jsonResponse.indexOf("\"codigo\":") + 9;
        int endIndex = jsonResponse.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = jsonResponse.indexOf("}", startIndex);
        }
        return jsonResponse.substring(startIndex, endIndex).trim();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CadastroProdutoForm().setVisible(true));
    }
}
