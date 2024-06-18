package es.upm.ejemplo;

import jade.core.AID;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import javax.swing.SwingUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.ejemplo.JFrameEjemplo.RecognitionResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.InputStreamReader;
import java.io.IOException;


import javax.swing.SwingUtilities;

public class AgenteGUI extends Agent {
    
    private JFrameEjemplo interfaz;
    public String img_path_process;
    
    @Override
    protected void setup() {
        System.out.println(getLocalName() + " iniciado.");

        // Crear y mostrar la interfaz gráfica en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            interfaz = new JFrameEjemplo();
            interfaz.agent = this;
            interfaz.setVisible(true);
        });
    }
    
    public JSONObject recognize() throws JSONException {
    	System.out.println("\nPath de la imagen seleccionada: " + interfaz.path);
        JSONObject jsonObject = new JSONObject();
        try {
        	jsonObject.put("img_path", interfaz.path);
        } catch (JSONException e) {
        	e.printStackTrace();
        }

        // Send
        ACLMessage mensaje_send = new ACLMessage(ACLMessage.INFORM);
        mensaje_send.setOntology("reconocimiento");
        mensaje_send.addReceiver(getAID("AgenteBroker")); // Nombre del agente receptor
        mensaje_send.setContent(jsonObject.toString());
        send(mensaje_send);
        
        // Receive
        ACLMessage mensaje_receive = blockingReceive();
        JSONObject jsonObject_receive = new JSONObject();
        //System.out.println("Respuesta recibida por la GUI: " + mensaje_receive.getContent());
		try {
			jsonObject_receive = new JSONObject(mensaje_receive.getContent());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //interfaz.path = null;
        AmpliarGaleria(jsonObject_receive);
        return jsonObject_receive;
    }

    public void AmpliarGaleria(JSONObject jsonInfo) throws JSONException {
        String galeria_path = "galeria.json";
        JSONObject galeria_data;
    
        try {
            // Cargar el contenido actual del archivo galeria.json
            String galeria_dataStr = new String(Files.readAllBytes(Paths.get(galeria_path)));
            galeria_data = new JSONObject(galeria_dataStr);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return;
        }
    
        // Obtener el JSONArray "galeria" del objeto galeria_data
        JSONArray galeria_array = galeria_data.getJSONArray("galeria");
    
        // Obtener los nombres y la ruta de imagen del JSON recibido
        JSONArray nombresNuevos = jsonInfo.getJSONArray("Nombres");
        String imgPathNuevo = jsonInfo.getString("img_path");
    
        // Iterar sobre los nombres nuevos
        for (int i = 0; i < nombresNuevos.length(); i++) {
            String nuevoNombre = nombresNuevos.getString(i);
            if (nuevoNombre.toLowerCase().equals("")) {
                continue;
            }
    
            // Verificar si el nombre ya está en la galería
            boolean nombreExiste = false;
            for (int j = 0; j < galeria_array.length(); j++) {
                JSONObject persona = galeria_array.getJSONObject(j);
                String nombreEnGaleria = persona.getString("nombre");
                if (nombreEnGaleria.equals(nuevoNombre)) {
                    // Si el nombre ya está en la galería, agregar el nuevo path de imagen
                    JSONArray imgPaths = persona.getJSONArray("img_paths");
                    imgPaths.put(imgPathNuevo);
                    nombreExiste = true;
                    break;
                }
            }
    
            if (!nombreExiste) {
                // Si el nombre no está en la galería, crear una nueva entrada
                JSONObject nuevaPersona = new JSONObject();
                nuevaPersona.put("nombre", nuevoNombre);
                JSONArray imgPaths = new JSONArray();
                imgPaths.put(imgPathNuevo);
                nuevaPersona.put("img_paths", imgPaths);
                galeria_array.put(nuevaPersona);
            }
        }
    
        try {
            // Guardar los cambios en el archivo galeria.json
            Files.write(Paths.get(galeria_path), galeria_data.toString().getBytes());
            System.out.println("Galería actualizada exitosamente.");
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }

    public static List<Integer> jsonArrayToList(JSONArray jsonArray) {
        List<Integer> lista = new ArrayList<>();
        // Itera sobre los elementos del JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                // Añade cada elemento a la lista
                lista.add(jsonArray.getInt(i));
            } catch (JSONException e) {
                e.printStackTrace();
                // Maneja la excepción si el elemento no es un entero
                // o si ocurre algún otro problema al obtener el elemento
            }
        }
        return lista;
    }

    public JSONObject mostrarImagenes() {
        JSONObject jsonObject = new JSONObject();
        try {
        	jsonObject.put("nombres", interfaz.nombres);
        } catch (JSONException e) {
        	e.printStackTrace();
        }
        
        // Send
        ACLMessage mensaje_send = new ACLMessage(ACLMessage.INFORM);
        mensaje_send.setOntology("busqueda");
        mensaje_send.addReceiver(getAID("AgenteBroker")); // Nombre del agente receptor
        mensaje_send.setContent(jsonObject.toString());
        send(mensaje_send);

        // Receive
        ACLMessage mensaje_receive = blockingReceive();
        JSONObject jsonObject_receive = new JSONObject();
        System.out.println(mensaje_receive.getContent());
        try {
        	jsonObject_receive = new JSONObject(mensaje_receive.getContent());
        } catch (JSONException e) {
        	e.printStackTrace();
        }
        //System.out.println("Respuesta recibida por la GUI: " + jsonObject_receive);
        return jsonObject_receive;
    }

    public void actualizarNombres(JSONObject respuesta) {
    	//System.out.println(respuesta.toString());
    	JSONArray nombres = null;
		JSONArray caras;
		JSONArray embeddings = null;
		try {
			nombres = respuesta.getJSONArray("nombres");
			embeddings = respuesta.getJSONArray("embeddings");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // Terminar si no hay nombres
        if (nombres.length() == 0) {
            return;
        }
        String caras_data_path = "caras.json";
        // Añado los nuevos nombres y sus embeddings a los datos existentes en caras.json
        // Primero leo los datos existentes
        JSONObject caras_data = new JSONObject();
        try {
            String caras_data_str = new String(Files.readAllBytes(Paths.get(caras_data_path)));
            caras_data = new JSONObject(caras_data_str);
            caras = caras_data.getJSONArray("caras");
            for (int i = 0; i < nombres.length(); i++) {
                String nombre = nombres.getString(i);
                if (nombre.toLowerCase().equals("desconocido")) {
                    continue;
                }
                JSONObject new_face = new JSONObject();
                new_face.put("nombre", nombre);
                new_face.put("embedding", embeddings.get(i));
                caras.put(new_face);
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return;
        }
        
        // Escribo los datos actualizados
        try {
        	JSONObject new_data = new JSONObject();
        	new_data.put("caras",caras);
            FileWriter file = new FileWriter(caras_data_path);
            file.write(caras_data.toString());
            file.close();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        //System.out.println("Actualizacion terminada");
    }
    @Override
    protected void takeDown() {
        System.out.println("Terminando el agente " + getLocalName());
        if (interfaz != null) {
            interfaz.dispose();
        }
    }
}