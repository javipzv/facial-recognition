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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class DetectorCaras extends Agent {
    
protected void setup() {
    System.out.println("DetectorCaras iniciado.");

    addBehaviour(new CyclicBehaviour(this) {
        public void action() {
            // Recibir mensaje
            ACLMessage msg = receive();
            if (msg != null) {
                String contenido = msg.getContent();
                //System.out.println(contenido);
                String resultado = "";
                try {
                    resultado = ejecutarScriptPython(contenido);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
                // Responder al mensaje con el resultado
                ACLMessage respuesta = msg.createReply();
                respuesta.setPerformative(ACLMessage.INFORM);
                respuesta.setContent(resultado);
                send(respuesta);  
            }
        }
    });
}

private String ejecutarScriptPython(String data) throws IOException, InterruptedException {
    System.out.println("\nEjecutando script DetectorCaras...");

    // Ruta al script de Python
    String Script_Path = "scripts/detectorCaras.py";
    String data_path = "json/detectorCaras.json";
    
    // Crear el archivo JSON con los datos
    FileWriter file = new FileWriter(data_path);
    file.write(data);
    file.close();

    // Ejecutar el script
    ProcessBuilder processBuilder = new ProcessBuilder("py", Script_Path).inheritIO();
    Process process = processBuilder.start();
    process.waitFor();

    String return_data = new String(Files.readAllBytes(Paths.get(data_path)));
    return return_data;
    }
}

