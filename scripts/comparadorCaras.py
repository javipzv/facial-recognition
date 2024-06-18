import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3' 
import json
from deepface.DeepFace import verify

data_file_path = "json/comparadorCaras.json"

# Leemos el archivo de datos json
with open(data_file_path, "r") as read_file:
    data = json.load(read_file)

# Cargamos los datos de las caras conocidas
known_faces_file = "json/caras.json"
with open(known_faces_file, "r") as read_file:
    known_faces = json.load(read_file)

data["Nombres"] = []
# Por cada cara en el archivo de datos la comparamos con las caras conocidas
for i, coords in enumerate(data['caras']):
    x, y, w, h = coords
    face = data['embeddings'][i]
    distances = []
    for known_face in known_faces['caras']:
        known_face_embedding = known_face['embedding']
        # print(len(face), len(known_face_embedding),type(face[0]),type(known_face_embedding[0]))
        distance = verify(face, known_face_embedding,model_name='Facenet',silent=True)['distance']
        # print(distance)
        distances.append((known_face['nombre'], distance))
    distances.sort(key=lambda x: x[1])

    if len(known_faces['caras']) != 0 and distances[0][1] < 0.4:
        data["Nombres"].append(distances[0][0])
    else:
        data["Nombres"].append("")
# Sobreescribimos el archivo de datos json
with open(data_file_path, "w") as write_file:
    json.dump(data, write_file, indent=4)



