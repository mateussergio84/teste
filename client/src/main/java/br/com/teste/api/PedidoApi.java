package br.com.teste.api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONArray;
import org.json.JSONObject;

public class PedidoApi {

    private static final String API_URL = "http://localhost:8080/pedido";
    private static final String CLIENTES_URL = "http://localhost:8080/clientes";
    private static final String PRODUTOS_URL = "http://localhost:8080/produtos";

    private String getResponse(HttpURLConnection conn) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            return response.toString();
        }
    }

    private void handleError(HttpURLConnection conn) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"))) {
            StringBuilder sb = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                sb.append(responseLine.trim());
            }
            throw new RuntimeException(sb.toString());
        }
    }


    public boolean adicionarPedido(Long clienteId, Long produtoId, Integer quantidade) throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        JSONObject jsonPedido = new JSONObject();
        jsonPedido.put("cliente", new JSONObject().put("codigo", clienteId));
        jsonPedido.put("produto", new JSONObject().put("codigo", produtoId));
        jsonPedido.put("quantidade", quantidade);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPedido.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_CREATED) {
            return true;
        } else {
            handleError(conn);
            return false; // não será alcançado, mas necessário para compilação
        }
    }

    public JSONArray obterPedidos() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String response = getResponse(conn);
            return new JSONArray(response);
        } else {
            handleError(conn);
            return new JSONArray();
        }
    }

    public JSONArray obterPedidosAgrupadosPorCliente() throws Exception {
        URL url = new URL(API_URL + "/clientes");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String response = getResponse(conn);
            return new JSONArray(response);
        } else {
            handleError(conn);
            return new JSONArray(); // não será alcançado, mas necessário para compilação
        }
    }

    public JSONArray obterPedidosAgrupadosPorProduto() throws Exception {
        URL url = new URL(API_URL + "/produtos");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String response = getResponse(conn);
            return new JSONArray(response);
        } else {
            handleError(conn);
            return new JSONArray(); // não será alcançado, mas necessário para compilação
        }
    }


    public void alterarQuantidadePedido(int codigo, int quantidade) throws Exception {
        URL url = new URL(API_URL + "/" + codigo);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("PUT");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        con.setRequestProperty("Accept", "application/json");
        con.setDoOutput(true);

        JSONObject jsonInput = new JSONObject();
        jsonInput.put("quantidade", quantidade);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInput.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            handleError(con);
        }
    }

    public void deletarPedido(int codigo) throws Exception {
        URL url = new URL(API_URL + "/" + codigo);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("DELETE");
        con.setRequestProperty("Accept", "application/json");

        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
            handleError(con);
        }
    }


    public JSONArray obterTodosPedidos() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return new JSONArray(response.toString());
        } else {
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }



}
