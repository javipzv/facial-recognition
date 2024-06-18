import os
os.environ['TF_CPP_MIN_LOG_LEVEL'] = '3' 
from json import load, dump
from deepface.DeepFace import represent
from cv2 import imread

data_file_path = "json/calculadoraEmbeddings.json"

# Leemos el archivo de datos json
with open(data_file_path, "r") as read_file:
    data = load(read_file)

# Obtenemos los embeddings de las caras
data['embeddings'] = []
img_path = data['img_path']
image = imread(img_path)

for coords in data['caras']:
    x, y, w, h = coords
    face = image[y:y+h, x:x+w]
    embedding = represent(face, model_name='Facenet', enforce_detection=False)
    data['embeddings'].append(embedding[0]['embedding'])

# Sobreescribimos el archivo de datos json
with open(data_file_path, "w") as write_file:
    dump(data, write_file, indent=4)
