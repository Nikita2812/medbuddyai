from fastapi import FastAPI, File, UploadFile
import functions as fc
from fastapi.openapi.docs import get_swagger_ui_html
from fastapi.responses import JSONResponse
import os
from dotenv import load_dotenv
import json
import firebase_admin
from firebase_admin import db
from fastapi.responses import JSONResponse
import ragChat
import verifyResults
import re
import numpy as np

app = FastAPI(title='Medbuddy API', version='1.0.2', description='API for Medbuddy AI (Hack Systhesis)', redoc_url='/doc', docs_url='/')

@app.post('/text_classify')
async def text_classify(text: str):
    print(fc.textClassification(text)[0])
    return JSONResponse(content={
        "isMedicalText": float(np.float32(fc.textClassification(text)[0])) <= 0.5,
        "message": "Spam text detected" if float(np.float32(fc.textClassification(text)[0])) >= 0.5 else "Medical text detected"
    }, status_code=200)

@app.post('/chat/')
async def chat(serviceName:str, secretCode: str, message: str):
    chatbot = ragChat.ragChat(kwargs={"serviceName": serviceName, "secretCode": secretCode})
    response= chatbot.get_response(message)
    return JSONResponse(content={"message": response}, status_code=200)
    
@app.post("/verifyResults")
async def verifyResultsa(
    diseaseName: str = None,
    serviceName: str = None,
    secretCode: str = None,
    file: UploadFile = File(...),
):
    try:
        verifier= verifyResults.verifyScanResult(diseaseName=diseaseName, serviceName=serviceName, secretCode=secretCode)

        file_location = f"temp_files/{file.filename}"
        os.makedirs(os.path.dirname(file_location), exist_ok=True)
        
        with open(file_location, "wb") as f:
            f.write(await file.read())
         
        match serviceName:
            case "google":
                 result= verifier.verifyResultsGoogle(file_location)
            case "openai":
                result=  verifier.verifyResultsOpenAI(file_location)
            case _:
                result=  JSONResponse(content= {"error": "Invalid service name"}, status_code=401)

        print(result)
        os.remove(file_location)
        return JSONResponse(content=result, status_code=200)
 
    except Exception as e:
        return JSONResponse(content= {"error": str(e)}, status_code=401)

@app.get('/getUsername/{name}')
async def get_username(name: str):
    load_dotenv()
    cred= json.loads(os.environ["FIREBASE_CREDENTIALS"])
    cred1= firebase_admin.credentials.Certificate(cred)
    if not firebase_admin._apps:
        app=firebase_admin.initialize_app(cred1, {"databaseURL":"https://medbuddy-ai-default-rtdb.asia-southeast1.firebasedatabase.app/"})
    try:
        ref= db.reference("usernames").get()
        username_list_lower = [username.lower() for username in ref.values()]
        if username_list_lower is None:
            return JSONResponse(
                content= {
                    "firstName": None,
                    "lastName": None,
                    "username": None
                }
            )
        if name.lower() in username_list_lower:
            return JSONResponse(
                content= {
                    "firstName": name,
                    "lastName": name,
                    "username": name
                }
            )
        else:
            return JSONResponse(
                content= {
                    "firstName": None,
                    "lastName": None,
                    "username": None
                }
            )
    except Exception as e:
        print(e)

@app.get('/gen')
async def data():
    return fc.getDoctorGeneralMedApollo()

@app.get('/symptoms/{symptom_sentence}')
async def symptoms(symptom_sentence: str):
    return fc.getAISymptomsResponse(symptom_sentence)

@app.get('/next_symptom/{symptom_sentence}')
async def next_symptom(symptom_sentence: str):
    return fc.getSymptomPredictionResponse(symptom_sentence)

@app.post('/upload')
async def decodeReport(file: UploadFile = File(...)):
    try:
        # Read the file into memory
        contents = await file.read()
        #image = Image.open(BytesIO(contents))

        # Perform analysis on the image
        #analysis_result = analyze_image(image)

        return JSONResponse(
            content={
                "message": "Image processed successfully", 
                "analysis": "analysis_result"
            }, 
            status_code=200
        )
    except Exception as e:
        return JSONResponse(content={"message": f"An error occurred: {str(e)}"}, status_code=500)
    

    def parse_error_message(error_str):
        try:
            # Try to parse the error string as JSON
            error_dict = json.loads(error_str)
            if isinstance(error_dict, dict) and 'error' in error_dict:
                return error_dict['error']['message']
        except json.JSONDecodeError:
            pass

        # If JSON parsing fails, use regex to extract the message
        match = re.search(r"'message': '(.+?)'", error_str)
        if match:
            return match.group(1)

        # If all else fails, return the original error string
        return error_str