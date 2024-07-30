package br.com.teste.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClienteApi {

    public boolean cadastrarCliente(String nome, String limiteCompra, String diaFechamentoText) throws Exception {
        BigDecimal limite = new BigDecimal(limiteCompra);
        Integer diaFechamento = Integer.parseInt(diaFechamentoText);

        String url = "http://localhost:8080/cliente";

        String json = String.format("{\"nome\":\"%s\",\"limite\":%s,\"dtFechamento\":%d}",
                nome, limite.toPlainString(), diaFechamento);

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
        return responseCode == HttpURLConnection.HTTP_CREATED;
    }

    private static final String BASE_URL = "http://localhost:8080/cliente";


    public List<String[]> buscarClientes() throws Exception {
        List<String[]> clientes = new ArrayList<>();
        URL url = new URL(BASE_URL);
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

            JSONArray jsonArray = new JSONArray(jsonResponse);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String codigo = jsonObject.optString("codigo", "");
                String nome = jsonObject.optString("nome", "");

                clientes.add(new String[]{codigo, nome});
            }
        } else {
            throw new RuntimeException("Erro ao obter produtos: " + responseCode);
        }
        return clientes;
    }



}
