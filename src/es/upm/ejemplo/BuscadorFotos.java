package es.upm.ejemplo;

import jade.core.Agent;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class BuscadorFotos extends Agent {
    
    protected void setup() {
        System.out.println("BuscadorFotos iniciado.");
        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                // Recibir mensaje
                ACLMessage msg = receive();            
                if (msg != null) {
                    String contenido = msg.getContent();
                    try {
                        JSONObject result = BuscarFotos(contenido);
                        ACLMessage reply = msg.createReply();
                        reply.setPerformative(ACLMessage.INFORM);
                        reply.setContent(result.toString());
                        send(reply);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public JSONObject BuscarFotos (String contenido) throws JSONException, IOException {
        JSONObject lista_nombres = new JSONObject(contenido);
        JSONArray nombres = lista_nombres.getJSONArray("nombres");
        
        String galeria_path = "json/galeria.json";
        JSONObject galeria_data;
        
        String galeria_dataStr = new String(Files.readAllBytes(Paths.get(galeria_path)));
        galeria_data = new JSONObject(galeria_dataStr);

        JSONArray galeria_array = galeria_data.getJSONArray("galeria");

        // Iterar sobre la lista de nombres de galería, y si un nombre está en names, añadir al set de fotos.
        Set<String> fotos = new HashSet<String>();
        
        for (int i = 0; i < galeria_array.length(); i++) {
            JSONObject item = galeria_array.getJSONObject(i);
            String nombre_galeria = item.getString("nombre");
            JSONArray fotos_array = item.getJSONArray("img_paths");
            Set<String> fotos_set = jsonArrayToSet(fotos_array);
            boolean nombre_encontrado = false;
            for (int j = 0; j < nombres.length(); j++) {
                String nombre_buscado = nombres.getString(j);
                if (nombre_galeria.equals(nombre_buscado)) {
                    nombre_encontrado = true;
                    break;
                }
            }
            if (nombre_encontrado) {
                if (fotos.isEmpty()) {
                    fotos = fotos_set;
                } else {
                    fotos.retainAll(fotos_set);
                }
            }
        }
        JSONObject jsonResult = new JSONObject();
        jsonResult.put("fotos", fotos);
        return jsonResult;
    }

    public static Set<String> jsonArrayToSet(JSONArray jsonArray) {
        Set<String> set = new HashSet<>();

        // Itera sobre los elementos del JSONArray y agrégalos al conjunto
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                String element = jsonArray.getString(i);
                set.add(element);
            } catch (JSONException e) {
                e.printStackTrace();
                // Manejar la excepción si hay un problema al obtener un elemento del JSONArray
            }
        }

        return set;
    }

    protected void takeDown() {
        System.out.println("BuscadorFotos finalizado.");
    }
}

