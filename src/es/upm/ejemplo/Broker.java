package es.upm.ejemplo;

import org.json.JSONException;
import org.json.JSONObject;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class Broker extends Agent {
	
	protected void setup() {
		System.out.println("Broker iniciado.");
        // Comportamiento del agente receptor
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Esperar a recibir un mensaje
                ACLMessage mensaje = receive();
                if (mensaje != null) {
                    String ontologia = mensaje.getOntology();
                	if (ontologia.equals("busqueda")) {
                		//System.out.println("Mensaje recibido en el Broker...");
						// Leer mensaje de GUI y enviar a AgenteBusqueda
						String contenido = mensaje.getContent();

						ACLMessage mensaje_send = new ACLMessage(ACLMessage.INFORM);
						mensaje_send.addReceiver(getAID("AgenteBusqueda"));
						mensaje_send.setContent(contenido);
						send(mensaje_send);

						// Leer mensaje de AgenteBusqueda y enviar a AgenteGUI
						ACLMessage response = blockingReceive();
						String contenido_resp = response.getContent();
						
						ACLMessage new_response = new ACLMessage(ACLMessage.INFORM);
						new_response.setContent(contenido_resp);
						new_response.addReceiver(getAID("GUI"));
						send(new_response);
                	}
                	else if (ontologia.equals("reconocimiento")) {
						//System.out.println("Mensaje recibido en el Broker...");
						// Leer mensaje de GUI y enviar a DetectorCaras
						String contenido = mensaje.getContent();
						//System.out.println(contenido);
						
						ACLMessage mensaje_send = new ACLMessage(ACLMessage.INFORM);
						mensaje_send.addReceiver(getAID("AgenteDetector"));
						mensaje_send.setContent(contenido);
						send(mensaje_send);
						
						// Leer mensaje de DetectorCaras y enviar a CalculadoraEmbeddings
						ACLMessage response = blockingReceive();
						String contenido_resp = response.getContent();
						//System.out.println(contenido_resp);    
						
						ACLMessage new_response = new ACLMessage(ACLMessage.INFORM);
						new_response.setContent(contenido_resp);
						new_response.addReceiver(getAID("AgenteCalculadora"));
						send(new_response);
						
						// Leer mensaje de CalculadoraEmbeddings y enviar a ComparadorCaras
						ACLMessage response2 = blockingReceive();
						String contenido_resp2 = response2.getContent();
						//System.out.println(contenido_resp2);    
	
						// System.out.println("Enviando mensaje a ComparadorCaras...");
						ACLMessage new_response2 = new ACLMessage(ACLMessage.INFORM);
						new_response2.setContent(contenido_resp2);
						new_response2.addReceiver(getAID("AgenteComparador"));
						send(new_response2);
						
						// Leer mensaje de ComparadorCaras
						ACLMessage response3 = blockingReceive();
						String contenido_resp3 = response3.getContent();
	
						// responder a la GUI
						ACLMessage respuesta = mensaje.createReply();
						respuesta.setPerformative(ACLMessage.INFORM);
						respuesta.setContent(contenido_resp3);
						send(respuesta);
                	}
                } else {
                    block();
                }
            }
        });
    }
}
