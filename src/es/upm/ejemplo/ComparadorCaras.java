package es.upm.ejemplo;

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

public class ComparadorCaras extends Agent {
    
    protected void setup() {
        System.out.println("ComparadorCaras iniciado.");

        addBehaviour(new CyclicBehaviour(this) {
            public void action() {
                // Recibir mensaje
                ACLMessage msg = this.myAgent.blockingReceive();
                if (msg != null) {
                    String contenido = msg.getContent();
                    String resultado = "";
                    // Verificar la ontologia
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
        System.out.println("Ejecutando script ComparadorCaras...\n");

        // Ruta al script de Python
        String Script_Path = "scripts/comparadorCaras.py";
        String data_path = "json/comparadorCaras.json";

        // Crear el archivo JSON con los datos
        FileWriter file = new FileWriter(data_path);
        file.write(data);
        file.close();
        // Ejecutar el script
        ProcessBuilder processBuilder = new ProcessBuilder("py", Script_Path).inheritIO();
        Process process = processBuilder.start();
        process.waitFor();

        // Leer el resultado del archivo json en data_path y transformarlo a objeto JSON
        String return_data = new String(Files.readAllBytes(Paths.get(data_path)));
        return return_data;
    }
}