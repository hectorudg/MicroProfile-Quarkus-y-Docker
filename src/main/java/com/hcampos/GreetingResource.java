package com.dcastillo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.microprofile.faulttolerance.Bulkhead;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/pokemon")
public class GreetingResource {

    @GET
    @Timeout(value=5000L)
    @Retry(maxRetries = 10)
    @Bulkhead(value = 1)
    @Fallback(fallbackMethod = "getFallback")
    @Produces(MediaType.TEXT_PLAIN)
    public String list() throws IOException {
        String resultado;
        JSONObject jsonObject;

        URL url = new URL("https://pokeapi.co/api/v2/pokemon/ditto");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer ");

        resultado = convertir(connection);

        jsonObject = new JSONObject(resultado);

        return jsonObject.toString(4);
    }
    
    public String convertir(HttpURLConnection connection) throws IOException{
        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder resultado = new StringBuilder();
        String linea;
        String res;
        
        while ((linea = rd.readLine()) != null) {   // Mientras el BufferedReader se pueda leer, agregar contenido a resultado
            resultado.append(linea);
        }
        rd.close();   // Cerrar el BufferedReader
        
        res = resultado.toString();   // Regresar resultado, pero como cadena, no como StringBuilder

        return res;
    }

    public String getFallback() {
        String mensaje = "{\"pockemon_id\":\"1bqfMWQo9tEvKZJg4oKL0R\",\"ERROR\":\"ID invalido.\"}";
        JSONObject json = new JSONObject(mensaje);
        return json.toString(4);
    }
}