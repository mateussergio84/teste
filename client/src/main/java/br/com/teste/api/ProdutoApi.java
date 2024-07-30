package br.com.teste.api;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ProdutoApi {

    private static final String API_URL = "http://localhost:8080/produto";

    public boolean cadastrarProduto(String descricao, String precoText) {
        try {
            String json = String.format("{\"descricao\":\"%s\",\"preco\":%s}",
                    descricao, precoText);

            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = con.getResponseCode();
            return responseCode == HttpURLConnection.HTTP_CREATED;

        } catch (Exception ex) {
            return false;
        }
    }

    public List<String[]> buscarProdutos() throws Exception {
        List<String[]> produtos = new ArrayList<>();
        URL url = new URL(API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String jsonResponse = response.toString();
            System.out.println("Resposta JSON: " + jsonResponse);

            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String codigo = jsonObject.optString("codigo", "");
                String descricao = jsonObject.optString("descricao", "");
                String preco = jsonObject.optString("preco", "");

                produtos.add(new String[]{codigo, descricao, preco});
            }
        } else {
            throw new RuntimeException("Erro ao obter produtos: " + responseCode);
        }
        return produtos;
    }

    public boolean deleteProduto(long codigo) throws Exception {
        URL url = new URL(API_URL + codigo);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            return true;
        } else {
            String responseMessage = connection.getResponseMessage();
            System.err.println("Erro ao deletar produto: " + responseCode + " - " + responseMessage);
            return false;
        }
    }
}
