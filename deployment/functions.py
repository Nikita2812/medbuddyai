import pickle
from tensorflow import keras
import numpy as np
import random
import json
import tensorflow as tf
from tensorflow.keras.preprocessing import sequence
import pandas as pd
import numpy as np
from tensorflow.keras.preprocessing.sequence import pad_sequences
from tensorflow import keras
import tensorflow as tf
import string
from bs4 import BeautifulSoup
import requests
import tensorflow as tf
import numpy as np
import pickle
import pandas as pd
import os
from tokenizers import Tokenizer

def simple_tokenize(text):
    # Simple tokenization by splitting on spaces and removing punctuation
    return ''.join(c.lower() if c.isalnum() else ' ' for c in text).split()

def pad_sequences(sequences, maxlen, dtype='int32', padding='pre', truncating='pre', value=0.):
    num_samples = len(sequences)
    
    x = np.full((num_samples, maxlen), value, dtype=dtype)
    
    for idx, s in enumerate(sequences):
        if len(s) == 0:
            continue  # empty list/array was found
        if truncating == 'pre':
            trunc = s[-maxlen:]
        elif truncating == 'post':
            trunc = s[:maxlen]
        else:
            raise ValueError(f'Truncating type "{truncating}" not understood')

        if padding == 'post':
            x[idx, :len(trunc)] = trunc
        elif padding == 'pre':
            x[idx, -len(trunc):] = trunc
        else:
            raise ValueError(f'Padding type "{padding}" not understood')
    
    return x

def getSymptomPredictionResponse(symptom_sentence: str):
    interpreter = tf.lite.Interpreter(model_path="diseasePredV1.tflite")
    interpreter.allocate_tensors()

    # Get input and output details
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    with open("nextSymptomData.pickle", "rb") as f:
        word_to_index, max_seq_len = pickle.load(f)

    tokenized_sequence = simple_tokenize(symptom_sentence)
    sequence_indices = [word_to_index.get(word, 0) for word in tokenized_sequence]
    padded_sequence = pad_sequences([sequence_indices], maxlen=max_seq_len, dtype='float32')

    interpreter.set_tensor(input_details[0]['index'], padded_sequence)

    # Run the inference
    interpreter.invoke()

    # Get the output tensor
    output_data = interpreter.get_tensor(output_details[0]['index'])

    # Find the index of the highest probability
    next_symptom_index = np.argmax(output_data[0])

    # Reverse the mapping to get the symptom word
    next_symptom_word = list(word_to_index.keys())[list(word_to_index.values()).index(next_symptom_index)]

    print(list(symptom_sentence.split(', ')))

    print("Given the sequence: ", symptom_sentence)
    print("The predicted next symptom is: ", next_symptom_word)
    return {
        "message": next_symptom_word if next_symptom_word not in list(symptom_sentence.split(', ')) else None,
    }

def getAISymptomsResponse(symptom_sentence: str):
    interpreter = tf.lite.Interpreter(model_path="symptom_checker_model.tflite")
    interpreter.allocate_tensors()

    # Get input and output details
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    with open('symptom_varsV1.pickle', 'rb') as f:
        disease_labels, symptom_to_idx, max_length = pickle.load(f)

    data = pd.read_csv('disease_sympts_prec_full.csv')

    symptoms = simple_tokenize(symptom_sentence)
    tokenized_symptoms = [symptom_to_idx.get(token, 0) for token in symptoms]
    padded_symptoms = pad_sequences([tokenized_symptoms], maxlen=max_length)
    input_tensor = np.array(padded_symptoms, dtype=np.float32)

    # Set the input tensor
    interpreter.set_tensor(input_details[0]['index'], input_tensor)

    # Run the inference
    interpreter.invoke()

    # Get the output tensor
    output_data = interpreter.get_tensor(output_details[0]['index'])
    predicted_index = np.argmax(output_data[0])
    predicted_disease = disease_labels[predicted_index]

    # Print the predicted disease
    print(f"Predicted disease: {predicted_disease}")
    precautions = data[data['disease'] == predicted_disease]['precautions'].iloc[0]
    print(f"Precautions: {precautions}")
    
    return {
        'message': f"Based on the symptoms you provided, you may have {predicted_disease}. Here are some precautions you can take: {precautions}"
    }

def getChatResponse(prompt: str):
    nltk.download('punkt')
    nltk.download('wordnet')
    with open('dataV3.pickle', 'rb') as f:
        word_index, classes, max_length = pickle.load(f)
    interpreter = tf.lite.Interpreter(model_path='chatV3.tflite')
    interpreter.allocate_tensors()

# Get input and output tensors
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    intents= json.loads(open(r"chatbot_dataset_generalV1.json").read())
    input_vector = bag_of_words(nltk.word_tokenize(prompt), word_index, max_length)
    input_vector = np.array([input_vector], dtype=np.float32)

    interpreter.set_tensor(input_details[0]['index'], input_vector)
    interpreter.invoke()

    output_data = interpreter.get_tensor(output_details[0]['index'])
    results_index = np.argmax(output_data[0])
    tag = classes[results_index]

    for tg in intents['intents']:
        if tg['tag'] == tag:
            responses = tg['responses']
            print(random.choice(responses))
    return {
        'message': random.choice(responses)
    }

def bag_of_words(s, words, max_length): 
    bag = [0 for _ in range(len(words))]
    lemmatizer= nltk.stem.WordNetLemmatizer()
    word_index = {word: index for index, word in enumerate(words)}

    word_indices = [word_index.get(lemmatizer.lemmatize(word.lower()), len(word_index)) for word in s]
    padded_indices = sequence.pad_sequences([word_indices], maxlen=max_length, padding='post')[0]
    return padded_indices

def getDoctorGeneralMedApollo(
        city: str = 'kolkata'
):
    appollo_webpage = requests.get(
        f'https://www.askapollo.com/physical-appointment/city/{city}?speciality=General%20Physician&page=1').text
    appollo_soup = BeautifulSoup(appollo_webpage, 'lxml')
    doctor_details = appollo_soup.find_all('div', class_='dr-list')
    doctor_names = []
    for i in doctor_details:
        doctor_names.append(i.find_all('h3')[0].text.strip())
    spec_grp = appollo_soup.find_all('div', class_='spec-group')
    doctor_spec = []
    for i in spec_grp:
        doctor_spec.append(i.text.strip().split('|'))
    doctors_data = {}
    for i in range(len(doctor_names)):
        doctor_data = [doctor_names[i], doctor_spec[i][0], doctor_spec[i][1]]

        category = 'physical'
        if category not in doctors_data:
            doctors_data[category] = []

        doctors_data[category].append(doctor_data)
    return doctors_data


def getDoctorGeneralMedPracto(
        city='kolkata'
):
    practo_webpage = requests.get(
        f'https://www.practo.com/search/doctors?results_type=doctor&q=%5B%7B%22word%22%3A%22General%20Physician%22%2C%22autocompleted%22%3Atrue%2C%22category%22%3A%22subspeciality%22%7D%5D&city={city}'
    ).text

def textClassification(text: str):

    def preprocess_text(text):
        # Convert to lowercase
        text = text.lower()
        # Remove punctuation (you might want to expand this)
        text = ''.join([c for c in text if c.isalnum() or c.isspace()])
        return text

    interpreter = tf.lite.Interpreter(model_path= os.path.join(os.getcwd(), "utils/spamClassification.tflite"))
    interpreter.allocate_tensors()

    # Get input and output tensors
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()

    with open(os.path.join(os.getcwd(), "utils/tokenizer_scam.pickle"), 'rb') as handle:
        tokenizer = pickle.load(handle)
    processed = preprocess_text(text)
    sequence = tokenizer.texts_to_sequences([processed])
    padded = pad_sequences(sequence, maxlen=100, padding='post')
    
    # Ensure the input is in the correct format (float32)
    input_data = padded.astype(np.float32)
    
    # Set the input tensor
    interpreter.set_tensor(input_details[0]['index'], input_data)
    
    # Run inference
    interpreter.invoke()
    
    # Get the output tensor
    output_data = interpreter.get_tensor(output_details[0]['index'])
    return output_data[0][0]