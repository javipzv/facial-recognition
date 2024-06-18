# Instrucciones
## Instalación código Java
Para instalarse el código Java basta con descomprimir el archivo .zip del proyecto y añadir en la configuración de ejecución en la parte de los argumentos el siguiente texto.
```
-gui GUI:es.upm.ejemplo.AgenteGUI;AgenteBroker:es.upm.ejemplo.Broker;AgenteDetector:es.upm.ejemplo.DetectorCaras;AgenteCalculadora:es.upm.ejemplo.CalculadoraEmbeddings;AgenteComparador:es.upm.ejemplo.ComparadorCaras;AgenteBusqueda:es.upm.ejemplo.BuscadorFotos
```

## Instalación módulos de Python 
Para instalarse todos los módulos necesarios de Python hay que ejecutar el siguiente comando en la terminal del sistema.
```bash
python -m pip install deepface opencv_python tf-keras
```

# Uso 
## Reconocer Caras
Para reconocer caras conocidas dentro de una imagen debe darle al botón de *Subir Imagen* y a continuación escoger una imagen de su sistema.

Preferiblemente en la imagen se deben distinguir claramente al menos una cara. A continuación dar al botón *Reconocer* para empezar el reconocimiento.

La primera vez que se ejecute no se reconocerá ninguna cara, pues no hay caras guardadas todavía. El resultado será parecido al siguiente:

![alt text](img/SinNombre.png)

Para aprender nuevas caras deberemos darle al botón de *Nombrar Caras* y nombrar todas, incluso las que no son conocidas clasificándolas con el nombre **Desconocido**. El resultado, una vez las caras están guardadas, será parecido al siguiente:


![alt text](img/ConNombre.png)

## Buscar por nombre
Otra funcionalidad del sistema es la de buscar todas las imágenes guardadas (es decir, todas a las que se le ha dado a Subir Imagen) en las que aparezcan ciertas personas. Para ello hay que darle al botón de *Hacer Búsqueda* y a continuación poner todos los nombres de las personas que se quiere que aparezcan en las imágenes. Finalmente, tras darle al botón de *Buscar*, se podrá navegar por todas la imágenes en las que aparecen esas personas.