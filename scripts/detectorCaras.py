import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3' 
import json
from deepface.DeepFace import extract_faces

data_file_path = "json/detectorCaras.json"

# Leemos el archivo de datos json
with open(data_file_path, "r") as read_file:
    data = json.load(read_file)

img_path = data['img_path']

# Extraemos caras de la imagen la imagen
caras = extract_faces(img_path, detector_backend='mtcnn')

data['caras'] = []

for cara in caras:
    x = cara['facial_area']['x']
    y = cara['facial_area']['y']
    w = cara['facial_area']['w']
    h = cara['facial_area']['h']
    data["caras"].append((x, y, w, h))

# Sobreescribimos el archivo de datos json
with open(data_file_path, "w") as write_file:
    json.dump(data, write_file, indent=4)
