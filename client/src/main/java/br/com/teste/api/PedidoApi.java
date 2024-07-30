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
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                sb.append(responseLine.trim());
            }
            br.close();
            throw new RuntimeException(sb.toString());
        }
    }

    public JSONArray obterPedidos() throws Exception {
        URL url = new URL(API_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder sb = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    sb.append(responseLine.trim());
                }
                return new JSONArray(sb.toString());
            }
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                sb.append(responseLine.trim());
            }
            throw new RuntimeException("Failed: HTTP error code : " + responseCode + "\n" + sb.toString());
        }
    }

    public JSONArray obterPedidosAgrupadosPorCliente() throws Exception {
        URL url = new URL(API_URL + "/clientes");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                return new JSONArray(response.toString());
            }
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                sb.append(responseLine.trim());
            }
            throw new RuntimeException("Failed: HTTP error code : " + responseCode + "\n" + sb.toString());
        }
    }

    public JSONArray obterPedidosAgrupadosPorProduto() throws Exception {
        URL url = new URL(API_URL + "/produtos");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                return new JSONArray(response.toString());
            }
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                sb.append(responseLine.trim());
            }
            throw new RuntimeException("Failed: HTTP error code : " + responseCode + "\n" + sb.toString());
        }
    }

    public boolean editarQuantidade(long pedidoId, int novaQuantidade) throws Exception {
        URL url = new URL(API_URL + "/" + pedidoId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);

        JSONObject jsonPedido = new JSONObject();
        jsonPedido.put("quantidade", novaQuantidade);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPedido.toString().getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = conn.getResponseCode();
        return responseCode == HttpURLConnection.HTTP_NO_CONTENT;
    }


    public boolean deletarPedido(Long pedidoId) throws Exception {
        URL url = new URL(API_URL + "/" + pedidoId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Content-Type", "application/json; utf-8");

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            return true;
        } else {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "utf-8"));
            StringBuilder sb = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                sb.append(responseLine.trim());
            }
            br.close();
            throw new RuntimeException("Failed: HTTP error code : " + responseCode + "\n" + sb.toString());
        }
    }


}
